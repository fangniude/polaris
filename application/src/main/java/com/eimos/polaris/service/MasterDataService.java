package com.eimos.polaris.service;

import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.validator.MdValidators;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void dropEntity(final String entityName) {
        this.metadataService.dropEntity(Namespace.MD, entityName, false);
    }

    public void dropEntity(final String entityName, final boolean force) {
        this.metadataService.dropEntity(Namespace.MD, entityName, force);
    }

    public MasterDataEntityVo fetchEntity(final String entityName) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);
        final List<Reference> references = this.metadataService.findRelationsBySourceEntity(entity);

        return MasterDataEntityVo.fromEntity(entity, references);
    }

    public void createAttribute(final String entityName, final AttributeVo attribute) {
        this.metadataService.createAttribute(Namespace.MD, entityName, attribute);
    }

    public void alterAttribute(final String entityName, final AttributeVo attribute) {
        this.metadataService.alterAttribute(Namespace.MD, entityName, attribute);
    }

    public void dropAttribute(final String entityName, final String attributeName, final boolean force) {
        this.metadataService.dropAttribute(Namespace.MD, entityName, attributeName, force);
    }

    public List<Map<String, Object>> list(final String entityName, final int pageIndex, final int pageSize) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);

        return this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entityName)))
                .limit(pageSize).offset((pageIndex - 1) * pageSize)
                .fetchMaps();
    }

    /**
     * 事务隔离级别 需要串行化，防止多条数据进入系统
     *
     * @param entityName 主数据
     * @param data       数据
     * @return ID
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public long add(final String entityName, final Map<String, Object> data) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);
        MdValidators.checkAdd(entity, data);

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

        this.dslContext.insertInto(DSL.table(DSL.name(Namespace.MD.tableName(entityName))), fields)
                .values(values)
                .execute();
        return id;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void modify(final String entityName, final Map<String, Object> data) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);
        MdValidators.checkModify(entity, data);

        final UpdateSetFirstStep<Record> update = this.dslContext.update(DSL.table(DSL.name(Namespace.MD.tableName(entityName))));
        UpdateSetMoreStep<Record> updateMore = update.set(DSL.field(DSL.name("update_time")), LocalDateTime.now());
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            updateMore = updateMore.set(DSL.field(DSL.name(entry.getKey())), entry.getValue());
        }
        updateMore.where(DSL.field(DSL.name("id")).equal(data.get("id")))
                .execute();
    }

    public void delete(final String entityName, final long id) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);
        MdValidators.checkDelete(entity, id);

        this.dslContext.delete(DSL.table(DSL.name(Namespace.MD.tableName(entityName))))
                .where(DSL.field(DSL.name("id")).equal(id))
                .execute();
    }

    public Map<String, Object> fetch(final String entityName, final long id) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, entityName);

        return this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entityName)))
                .where(DSL.field("id").equal(id))
                .fetchOneMap();
    }
}
