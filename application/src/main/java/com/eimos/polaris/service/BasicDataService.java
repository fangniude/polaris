package com.eimos.polaris.service;

import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.util.Constants;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author lipengpeng
 */
@Service
public class BasicDataService {
    private final MetadataService metadataService;
    private final DSLContext dslContext;

    public BasicDataService(final MetadataService metadataService, final DSLContext dslContext) {
        this.metadataService = metadataService;
        this.dslContext = dslContext;
    }

    public List<EntityVo> entities(final String queryKey, final int pageIndex, final int pageSize) {
        return this.metadataService.allEntities(Namespace.BD, queryKey, pageIndex, pageSize).stream()
                .map(EntityVo::valueOf)
                .toList();
    }

    public void create(final EntityVo entity) {
        final Entity e = new Entity(IdUtil.getSnowflakeNextId(), Namespace.BD, entity.getName(), entity.getComment(), Constants.BASIC_DATA_ATTRIBUTES);
        this.metadataService.createEntity(e);
    }

    public void drop(final String entityName, final boolean force) {
        this.metadataService.dropEntity(Namespace.BD, entityName, force);
    }

    public List<BasicDataVo> list(final String entityName, final String queryKey, final int pageIndex, final int pageSize) {
        final List<? extends Field<? extends Serializable>> list = List.of(DSL.field("code", String.class),
                DSL.field("name", String.class));
        return this.dslContext.select(list)
                .from(DSL.name(Namespace.BD.tableName(entityName)))
                .where(DSL.field("code").contains(DSL.value(queryKey))
                        .or(DSL.field("name").contains(DSL.value(queryKey))))
                .limit(pageSize).offset((pageIndex - 1) * pageSize)
                .fetch(r -> new BasicDataVo(r.get("code", String.class),
                        r.get("name", String.class)));
    }

    public Optional<BasicDataVo> fetch(final String entityName, final String code) {
        final List<? extends Field<? extends Serializable>> list = List.of(DSL.field("code", String.class),
                DSL.field("name", String.class));
        final BasicDataVo bd = this.dslContext.select(list)
                .from(DSL.name(Namespace.BD.tableName(entityName)))
                .where(DSL.field("code").equal(DSL.value(code)))
                .fetchOne(r -> new BasicDataVo(r.get("code", String.class),
                        r.get("name", String.class)));
        return Optional.ofNullable(bd);
    }

    public void add(final String entityName, final BasicDataVo basicData) {
        this.dslContext.insertInto(DSL.table(DSL.name(Namespace.BD.tableName(entityName))), Constants.BASIC_DATA_FIELDS)
                .values(basicData.getCode(), basicData.getName(), LocalDateTime.now(), LocalDateTime.now())
                .execute();
    }

    public void modify(final String entityName, final BasicDataVo basicData) {
        this.dslContext.update(DSL.table(DSL.name(Namespace.BD.tableName(entityName))))
                .set(DSL.field("name"), basicData.getName())
                .where(DSL.field("code").equal(basicData.getCode()))
                .execute();
    }

    public void delete(final String entityName, final String code) {
        this.dslContext.delete(DSL.table(DSL.name(Namespace.BD.tableName(entityName))))
                .where(DSL.field("code").equal(code))
                .execute();
    }
}
