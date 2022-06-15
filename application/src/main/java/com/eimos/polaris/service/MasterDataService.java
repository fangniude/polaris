package com.eimos.polaris.service;

import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.enums.MasterDataType;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lipengpeng
 */
@Service
public class MasterDataService {
    private final MetadataService metadataService;
    private final DSLContext dslContext;

    public MasterDataService(final MetadataService metadataService, final DSLContext dslContext) {
        this.metadataService = metadataService;
        this.dslContext = dslContext;
    }

    public void createEntity(final MasterDataEntityVo entity) {
        this.metadataService.createEntityWithRelation(entity.toEntity(), entity.toReferences());
    }

    public void dropEntity(final MasterDataType masterData) {
        this.metadataService.dropEntity(Namespace.MD, masterData.getName(), false);
    }

    public MasterDataEntityVo fetchEntity(final MasterDataType masterData) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, masterData.getName());
        final List<Reference> references = this.metadataService.findRelationsBySourceEntity(entity);

        return MasterDataEntityVo.fromEntity(entity, references);
    }

    public void createAttribute(final MasterDataType masterData, final AttributeVo attribute) {
        this.metadataService.createAttribute(Namespace.MD, masterData.getName(), attribute);
    }

    public void alterAttribute(final MasterDataType masterData, final AttributeVo attribute) {
        this.metadataService.alterAttribute(Namespace.MD, masterData.getName(), attribute);
    }

    public void dropAttribute(final MasterDataType masterData, final String attributeName, final boolean force) {
        this.metadataService.dropAttribute(Namespace.MD, masterData.getName(), attributeName, force);
    }

    public List<Map<String, Object>> list(final MasterDataType masterData, final String queryKey, final int pageIndex, final int pageSize) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, masterData.getName());

        return this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                .where(masterData.getSearchAttributes().stream()
                        .map(DSL::name)
                        .map(DSL::field)
                        .map(f -> f.contains(DSL.value(queryKey)))
                        .reduce(DSL::or)
                        .orElse(DSL.trueCondition()))
                .limit(pageSize).offset((pageIndex - 1) * pageSize)
                .fetchMaps();
    }

    /**
     * 事务隔离级别 需要串行化，防止多条数据进入系统
     *
     * @param masterData 主数据
     * @param data       数据
     * @return ID
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public long add(final MasterDataType masterData, final Map<String, Object> data) {

        if (masterData == MasterDataType.PRODUCT || masterData == MasterDataType.MATERIAL) {
            final MasterDataEntityVo entityVo = this.fetchEntity(masterData);
            final List<AttributeVo> attributes = entityVo.getAttributes();
            final List<AttributeVo> fkAttributes = attributes.stream().filter(AttributeVo::getForeignKey).toList();

            // 校验 产品物料 有没有重复的
            final Map<String, Object> map = this.dslContext.select(attributes.stream()
                            .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                            .toList())
                    .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                    .where(fkAttributes.stream()
                            .map(a -> {
                                final Object value = data.get(a.getName());
                                if (value == null) {
                                    return DSL.field(DSL.name(a.getName())).isNull();
                                } else {
                                    return DSL.field(DSL.name(a.getName())).equal(DSL.value(value));
                                }
                            })
                            .reduce(DSL.trueCondition(), DSL::and))
                    .limit(1)
                    .fetchOneMap();
            if (map != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：【%s】", map.get(MasterDataType.Constants.SOURCE_CODE)));
            }

            // 校验 源系统编码有没有重复
            if (this.dslContext.select(attributes.stream()
                            .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                            .toList())
                    .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                    .where(DSL.field(MasterDataType.Constants.SOURCE_CODE).equal(DSL.value(data.get(MasterDataType.Constants.SOURCE_CODE))))
                    .limit(1).fetchOneMap() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("源系统编码：【%s】已经存在", data.get(MasterDataType.Constants.SOURCE_CODE)));
            }
        }

        final List<Field<?>> fields = new ArrayList<>(data.size() + 1);
        final List<Field<?>> values = new ArrayList<>(data.size() + 1);

        fields.add(DSL.field(DSL.name("id"), Long.class));
        fields.add(DSL.field(DSL.name("create_time"), LocalDateTime.class));
        fields.add(DSL.field(DSL.name("update_time"), LocalDateTime.class));
        final long id = IdUtil.getSnowflakeNextId();
        values.add(DSL.value(id));
        values.add(DSL.value(LocalDateTime.now()));
        values.add(DSL.value(LocalDateTime.now()));

        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            fields.add(DSL.field(DSL.name(entry.getKey())));
            values.add(DSL.value(entry.getValue()));
        }

        this.dslContext.insertInto(DSL.table(DSL.name(Namespace.MD.schemaName(), masterData.getName())), fields)
                .values(values)
                .execute();
        return id;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void modify(final MasterDataType masterData, final Map<String, Object> data) {
        final Object id = data.get("id");
        Objects.requireNonNull(id, "id不能为空");

        if (masterData == MasterDataType.PRODUCT || masterData == MasterDataType.MATERIAL) {
            final MasterDataEntityVo entityVo = this.fetchEntity(masterData);
            final List<AttributeVo> attributes = entityVo.getAttributes();
            final List<AttributeVo> fkAttributes = attributes.stream().filter(AttributeVo::getForeignKey).toList();

            // 校验 产品物料 有没有重复的
            final Map<String, Object> map = this.dslContext.select(attributes.stream()
                            .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                            .toList())
                    .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                    .where(DSL.field(DSL.name("id")).ne(DSL.value(id)).and(fkAttributes.stream()
                            .map(a -> {
                                final Object value = data.get(a.getName());
                                if (value == null) {
                                    return DSL.field(DSL.name(a.getName())).isNull();
                                } else {
                                    return DSL.field(DSL.name(a.getName())).equal(DSL.value(value));
                                }
                            })
                            .reduce(DSL.trueCondition(), DSL::and)))
                    .limit(1)
                    .fetchOneMap();
            if (map != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：%s", map.get(MasterDataType.Constants.SOURCE_CODE)));
            }

            // 校验 源系统编码有没有重复
            if (this.dslContext.select(attributes.stream()
                            .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                            .toList())
                    .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                    .where(DSL.field(DSL.name("id")).ne(DSL.value(id))
                            .and(DSL.field(MasterDataType.Constants.SOURCE_CODE).equal(DSL.value(data.get(MasterDataType.Constants.SOURCE_CODE)))))
                    .limit(1).fetchOneMap() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("源系统编码：【%s】已经存在", data.get(MasterDataType.Constants.SOURCE_CODE)));
            }
        }

        final UpdateSetFirstStep<Record> update = this.dslContext.update(DSL.table(DSL.name(Namespace.MD.schemaName(), masterData.getName())));
        UpdateSetMoreStep<Record> updateMore = update.set(DSL.field(DSL.name("update_time")), LocalDateTime.now());
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            updateMore = updateMore.set(DSL.field(DSL.name(entry.getKey())), entry.getValue());
        }
        updateMore.where(DSL.field(DSL.name("id")).equal(id))
                .execute();
    }

    public void delete(final MasterDataType masterData, final long id) {
        this.dslContext.delete(DSL.table(DSL.name(Namespace.MD.schemaName(), masterData.getName())))
                .where(DSL.field(DSL.name("id")).equal(id))
                .execute();
    }

    public Map<String, Object> fetch(final MasterDataType masterData, final long id) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, masterData.getName());

        return this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.schemaName(), masterData.getName()))
                .where(DSL.field("id").equal(id))
                .fetchOneMap();
    }
}
