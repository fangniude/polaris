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
import org.springframework.stereotype.Service;

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

    public long add(final MasterDataType masterData, final Map<String, Object> data) {
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

    public void modify(final MasterDataType masterData, final Map<String, Object> data) {
        final Object id = data.get("id");
        Objects.requireNonNull(id, "id不能为空");

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
