package com.eimos.polaris.validator;

import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.service.MetadataService;
import com.eimos.polaris.util.Constants;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * @author lipengpeng
 */
@Component
public class Classifier implements MdValidator {
    private final MetadataService metadataService;
    private final DSLContext dslContext;

    public Classifier(final MetadataService metadataService, final DSLContext dslContext) {
        this.metadataService = metadataService;
        this.dslContext = dslContext;
    }

    @Override
    public void checkAdd(final Entity entity, final Map<String, Object> data) {
        final List<Reference> references = this.metadataService.findRelationsBySourceEntity(entity);
        final MasterDataEntityVo entityVo = MasterDataEntityVo.fromEntity(entity, references);

        final List<AttributeVo> attributes = entityVo.getAttributes();
        final List<AttributeVo> fkAttributes = attributes.stream().filter(AttributeVo::getForeignKey).toList();

        // 校验 产品物料 有没有重复的
        final Map<String, Object> map = this.dslContext.select(attributes.stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entity.getName())))
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：【%s】", map.get(Constants.SOURCE_CODE)));
        }
    }

    @Override
    public void checkModify(final Entity entity, final Map<String, Object> data) {
        final List<Reference> references = this.metadataService.findRelationsBySourceEntity(entity);
        final MasterDataEntityVo entityVo = MasterDataEntityVo.fromEntity(entity, references);

        final List<AttributeVo> attributes = entityVo.getAttributes();
        final List<AttributeVo> fkAttributes = attributes.stream().filter(AttributeVo::getForeignKey).toList();

        // 校验 产品物料 有没有重复的
        final Map<String, Object> map = this.dslContext.select(attributes.stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entity.getName())))
                .where(DSL.field(DSL.name("id")).ne(DSL.value(data.get("id"))).and(fkAttributes.stream()
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
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：%s", map.get(Constants.SOURCE_CODE)));
        }
    }
}
