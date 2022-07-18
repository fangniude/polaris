package com.eimos.polaris.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.domain.mapping.EqualMapping;
import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.entity.RelationEntity;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.repository.EntityRepository;
import com.eimos.polaris.repository.RelationRepository;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.EntityVo;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lipengpeng
 */
@Service
public class MetadataService {
    private final EntityRepository entityRepository;
    private final RelationRepository relationRepository;
    private final DSLContext dslContext;

    public MetadataService(final EntityRepository entityRepository, final RelationRepository relationRepository, final DSLContext dslContext) {
        this.entityRepository = entityRepository;
        this.relationRepository = relationRepository;
        this.dslContext = dslContext;
    }

    public List<EntityVo> entities(final Namespace namespace, final String queryKey, final int pageIndex, final int pageSize) {
        final List<EntityEntity> list = this.entityRepository.findByNameContainsOrCommentContains(queryKey, queryKey, PageRequest.of(pageIndex - 1, pageSize));

        return list.stream()
                .filter(e -> {
                    if (namespace == null) {
                        return true;
                    } else {
                        return e.getNamespace() == namespace;
                    }
                })
                .map(EntityVo::valueOf)
                .toList();
    }

    public List<EntityEntity> allEntities(final Namespace namespace, final String queryKey, final int pageIndex, final int pageSize) {
        return this.entityRepository.findByNamespaceAndNameContainsOrNamespaceAndCommentContains(namespace, queryKey, namespace, queryKey, PageRequest.of(pageIndex - 1, pageSize));
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEntity(final Entity e) {
        // 1. 创建元数据
        this.entityRepository.save(e.toEntity());

        // 2. 创建新的表
        // 2.1 如果 schema 不存在，先创建schema
//        final Name schemaName = DSL.name(e.getNamespace().schemaName());
//        try (final DDLQuery createSchemaIfNotExists = this.dslContext.createSchemaIfNotExists(schemaName)) {
//            createSchemaIfNotExists.execute();
//        }

        // 2.2 创建表，字段，表的备注
        final Name tableName = DSL.name(e.getNamespace().tableName(e.getName()));
        try (final CreateTableColumnStep createTable = this.dslContext.createTable(tableName)) {
            CreateTableColumnStep createColumns = createTable;
            for (final Attribute attribute : e.getAttributes()) {
                createColumns = createColumns.column(attribute.getName(), attribute.getDataType().dbDataType.nullable(attribute.getNullable()));
            }
            createColumns.execute();
        }
        this.dslContext.commentOnTable(tableName).is(e.getComment()).execute();

        // 2.3 创建索引
        for (final Attribute attribute : e.getAttributes()) {
            final String attributeCode = attribute.getName();
            final Name columnName = DSL.name(attributeCode);
            switch (attribute.getIndex()) {
                case UNIQUE -> this.dslContext.createUniqueIndex("uk_" + e.getName() + "_" + attributeCode).on(tableName, columnName).execute();
                case NAVIGABLE -> this.dslContext.createIndex("idx_" + e.getName() + "_" + attributeCode).on(tableName, columnName).execute();
                case HASH -> {
                    // todo
                }
                case BITMAP -> {
                    // todo
                }
                default -> {
                    // do nothing
                }
            }
        }

        // 2.4 增加中文备注
        for (final Attribute attribute : e.getAttributes()) {
            this.dslContext.commentOnColumn(DSL.name(tableName, DSL.name(attribute.getName())))
                    .is(attribute.getComment())
                    .execute();
        }
    }

    /**
     * 递归检查？强制删除时，把所有引用的，全部都删掉
     *
     * @param namespace  命名空间
     * @param entityName 实体名称
     * @param force      强制删除，会删除引用该基础数据的所有表，请谨慎使用
     */
    @Transactional(rollbackFor = Exception.class)
    public void dropEntity(final Namespace namespace, final String entityName, final boolean force) {
        final Optional<EntityEntity> optional = this.findEntity(namespace, entityName);
        optional.ifPresent(e -> this.dropEntity(e, force));
    }

    private void dropEntity(final EntityEntity e, final boolean force) {
        final Optional<EntityEntity> optional = this.entityRepository.findById(e.getId());
        if (optional.isPresent()) {
            this.entityRepository.deleteById(e.getId());
            if (force) {
                final List<RelationEntity> relations = this.relationRepository.findByReferenceEntityId(e.getId());
                if (CollUtil.isNotEmpty(relations)) {
                    // 删除所有关联
                    this.relationRepository.deleteAllByIdInBatch(relations.stream().map(RelationEntity::getId).toList());

                    final List<Long> sourceEntityIds = relations.stream().map(RelationEntity::getSourceEntityId).toList();
                    final List<EntityEntity> entities = this.entityRepository.findAllById(sourceEntityIds);
                    // 递归删除直接依赖当前实体 的 实体
                    for (final EntityEntity entity : entities) {
                        this.dropEntity(entity, force);
                    }
                }

                this.dslContext.dropTableIfExists(DSL.name(e.getNamespace().tableName(e.getName()))).cascade().execute();
            } else {
                this.dslContext.dropTableIfExists(DSL.name(e.getNamespace().tableName(e.getName()))).execute();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEntityWithRelation(final Entity entity, final List<Reference> references) {
        this.createEntity(entity);

        for (final Reference reference : references) {
            this.createRelation(reference);
        }
    }

    private void createRelation(final Reference ref) {
        // 1. 保存元数据
        this.relationRepository.save(ref.toRelation());

        // 2. 创建外键约束
        this.dslContext.alterTable(DSL.name(ref.getSourceNamespace().tableName(ref.getSourceEntity())))
                .add(DSL.constraint(this.foreignKeyName(ref.getSourceNamespace(), ref.getSourceEntity(), ref.getSourceAttribute()))
                        .foreignKey(ref.getSourceAttribute())
                        .references(DSL.name(ref.getRefNamespace().tableName(ref.getRefEntity())), DSL.name(ref.getRefAttribute())))
                .execute();
    }

    private String foreignKeyName(final Namespace sourceNamespace, final String sourceEntity, final String sourceAttribute) {
        return String.format("fk_%s_%s", sourceNamespace.tableName(sourceEntity), sourceAttribute);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createAttribute(final Namespace namespace, final String entityName, final AttributeVo attribute) {
        // 1. 实体：增加列
        // 1.1 元数据
        final Entity entity = this.findEntityNonNull(namespace, entityName);
        entity.addAttribute(attribute.toDomain());
        this.entityRepository.save(entity.toEntity());

        // 1.2 增加列
        this.dslContext.alterTable(DSL.name(namespace.tableName(entityName)))
                .addColumn(attribute.getName(), attribute.toDomain().getDataType().dbDataType.nullable(attribute.getNullable()))
                .execute();
        // 1.3 增加列的注释
        this.dslContext.commentOnColumn(DSL.name(namespace.tableName(entityName), attribute.getName()))
                .is(attribute.getComment())
                .execute();

        // 2. 关系：增加关联（如果需要）
        if (Boolean.TRUE.equals(attribute.getForeignKey())) {
            this.createRelation(attribute.toRef(namespace, entityName));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void dropAttribute(final Namespace namespace, final String entityName, final String attributeName, final boolean force) {
        final Entity entity = this.findEntityNonNull(namespace, entityName);

        // 1. 关系：查找并删除 关联
        final List<RelationEntity> relations = this.relationRepository.findBySourceEntityId(entity.getId());
        for (final RelationEntity relation : relations) {
            final EqualMapping mapping = JSONUtil.toBean(relation.getMapping(), EqualMapping.class);
            if (Objects.equals(mapping.getSourceAttributeName(), attributeName)) {
                this.relationRepository.deleteById(relation.getId());
                this.dslContext.alterTable(DSL.name(namespace.tableName(entityName)))
                        .dropConstraint(DSL.constraint(this.foreignKeyName(namespace, entityName, attributeName)))
                        .execute();
            }
        }

        // 2. 实体：删除实体中的属性
        entity.deleteAttribute(attributeName);
        this.entityRepository.save(entity.toEntity());
        this.dslContext.alterTable(DSL.name(namespace.tableName(entityName)))
                .dropColumn(attributeName)
                .execute();
    }

    @Transactional(rollbackFor = Exception.class)
    public void alterAttribute(final Namespace namespace, final String entityName, final AttributeVo attribute) {
        this.dropAttribute(namespace, entityName, attribute.getName(), false);
        this.createAttribute(namespace, entityName, attribute);
    }

    public Entity findEntityNonNull(final Namespace namespace, final String entityName) {
        final Optional<EntityEntity> optional = this.findEntity(namespace, entityName);
        if (optional.isPresent()) {
            return new Entity(optional.get());
        } else {
            throw new IllegalArgumentException(String.format("entity not exists, namespace: %s, entity: %s", namespace, entityName));
        }
    }

    public Optional<EntityEntity> findEntity(final Namespace namespace, final String entityName) {
        return this.entityRepository.findOneByNamespaceAndName(namespace, entityName);
    }

    public List<Reference> findRelationsBySourceEntity(final Entity entity) {
        final List<RelationEntity> relationEntities = this.relationRepository.findBySourceEntityId(entity.getId());

        final List<Long> refEntityIds = relationEntities.stream().map(RelationEntity::getReferenceEntityId).toList();

        final List<EntityEntity> entityList = this.entityRepository.findAllById(refEntityIds);
        final Map<Long, EntityEntity> entityMap = entityList.stream().collect(Collectors.toMap(EntityEntity::getId, Function.identity()));

        return relationEntities.stream()
                .map(r -> {
                    final EqualMapping mapping = Reference.fromMapping(r.getMapping());
                    final EntityEntity refEntity = entityMap.get(r.getReferenceEntityId());
                    return new Reference(entity.getNamespace(), entity.getName(), mapping.getSourceAttributeName(), refEntity.getNamespace(), refEntity.getName(), mapping.getReferenceAttributeName(), r.getOneToOne());
                })
                .toList();

    }
}
