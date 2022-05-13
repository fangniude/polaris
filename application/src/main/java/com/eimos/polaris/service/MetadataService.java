package com.eimos.polaris.service;

import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.repository.EntityRepository;
import com.eimos.polaris.repository.RelationRepository;
import com.eimos.polaris.vo.EntityVo;
import org.jooq.CreateTableColumnStep;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.impl.DSL;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    public List<EntityVo> basicDataEntities(final String queryKey, final int pageIndex, final int pageSize) {
        final List<EntityEntity> list = this.entityRepository.findByNameContainsOrCommentContains(queryKey, queryKey, PageRequest.of(pageIndex - 1, pageSize));

        return list.stream()
                .map(EntityVo::valueOf)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void createEntity(final Entity e) {
        // 1. 创建元数据
        this.entityRepository.save(e.toEntity());

        // 2. 创建新的表
        // 2.1 如果 schema 不存在，先创建schema
        final Name schemaName = DSL.name(e.getNamespace().schemaName());
        this.dslContext.createSchemaIfNotExists(schemaName)
                .execute();

        // 2.2 创建表，字段，表的备注
        final Name tableName = DSL.name(e.getNamespace().schemaName(), e.getName());
        try (final CreateTableColumnStep createTable = this.dslContext.createTable(tableName)) {
            CreateTableColumnStep createColumns = createTable;
            for (final Attribute attribute : e.getAttributes()) {
                createColumns = createColumns.column(attribute.getName(), attribute.getDataType().dbDataType.nullable(attribute.getNullable()));
            }
            createColumns.comment(e.getComment()).execute();
        }

        // 2.3 创建索引
        for (final Attribute attribute : e.getAttributes()) {
            final String attributeCode = attribute.getName();
            final Name columnName = DSL.name(attributeCode);
            switch (attribute.getIndex()) {
                case UNIQUE -> {
                    this.dslContext.createUniqueIndex("uk_" + e.getName() + "_" + attributeCode).on(tableName, columnName).execute();
                }
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
            this.dslContext.commentOnColumn(DSL.name(schemaName, tableName, DSL.name(attribute.getName())))
                    .is(attribute.getComment())
                    .execute();
        }
    }

    /**
     * 递归检查？强制删除时，把所有引用的，全部都删掉
     *
     * @param namespace  命名空间
     * @param entityCode 实体编码
     * @param force      强制删除，会删除引用该基础数据的所有表，请谨慎使用
     */
    @Transactional(rollbackFor = Exception.class)
    public void dropEntity(final Namespace namespace, final String entityCode, final boolean force) {
        final Optional<EntityEntity> optional = this.entityRepository.findOneByNamespaceAndName(namespace, entityCode);
        optional.ifPresent(e -> {
            this.entityRepository.deleteById(e.getId());
            this.dslContext.dropTableIfExists(DSL.name(e.getNamespace().schemaName(), e.getName()))
                    .execute();
        });
    }
}
