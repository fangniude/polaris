package com.eimos.polaris.validator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.ManufactureClassification;
import com.eimos.polaris.domain.ManufactureSpecValues;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.service.ManufactureClassificationService;
import com.eimos.polaris.util.Constants;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.eimos.polaris.service.ManufactureClassificationService.NAMES;

/**
 * @author lipengpeng
 */
@Component
public class Classifier implements MdValidator {
    private final ManufactureClassificationService classificationService;
    private final DSLContext dslContext;

    public Classifier(final ManufactureClassificationService classificationService, final DSLContext dslContext) {
        this.classificationService = classificationService;
        this.dslContext = dslContext;
    }

    @Override
    public void checkAdd(final Entity entity, final Map<String, Object> data) {
        // 1. 3个分类不全，不满足过分类器的条件
        final String entityName = entity.getName();
        final String usageName = ManufactureClassification.USAGES.get(entityName);
        final String featureName = ManufactureClassification.FEATURES.get(entityName);
        final String implName = ManufactureClassification.IMPLS.get(entityName);

        final String usageCode = data.containsKey(usageName) ? String.valueOf(data.get(usageName)) : null;
        final String featureCode = data.containsKey(featureName) ? String.valueOf(data.get(featureName)) : null;
        final String implCode = data.containsKey(implName) ? String.valueOf(data.get(implName)) : null;

        this.checkClassification(usageCode);
        this.checkClassification(featureCode);
        this.checkClassification(implCode);

        final ManufactureClassification.Classification classification = new ManufactureClassification.Classification(usageCode, featureCode, implCode);


        // 2. 处理 定义的 性能特征，为空 则 补空字符串，并生成 性能特征组合编码
        final String specJsonName = ManufactureClassification.SPECS_MAP.get(entityName);
        final String specCodeName = ManufactureClassification.SPEC.get(entityName);
        final ManufactureClassification fetch = this.classificationService.fetch(NAMES.get(entityName), classification.encode());
        final List<ManufactureSpecValues.SpecVal> specValues = fetch.getSpecs().stream()
                .map(s -> new ManufactureSpecValues.SpecVal(s, data.containsKey(s) ? String.valueOf(data.get(s)) : ""))
                .toList();

        data.put(specJsonName, JSONUtil.toJsonStr(specValues));
        data.put(specCodeName, new ManufactureSpecValues(specValues).genCode());

        // 3. 校验4个编码组合的唯一性
        final List<String> names = List.of(usageName, featureName, implName, specCodeName);

        final Map<String, Object> map = this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entityName)))
                .where(names.stream()
                        .map(a -> {
                            final Object value = data.get(a);
                            if (value == null) {
                                return DSL.field(DSL.name(a)).isNull();
                            } else {
                                return DSL.field(DSL.name(a)).equal(DSL.value(value));
                            }
                        })
                        .reduce(DSL.trueCondition(), DSL::and))
                .limit(1)
                .fetchOneMap();
        if (map != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：【%s】", map.get(Constants.SOURCE_CODE)));
        }
    }

    private void checkClassification(final String classification) {
        if (CharSequenceUtil.isBlank(classification)) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, String.format("分类编码【%s】不能为空", classification));
        }
    }

    @Override
    public void checkModify(final Entity entity, final Map<String, Object> data) {
        // 1. 3个分类不全，不满足过分类器的条件
        final String entityName = entity.getName();
        final String usageName = ManufactureClassification.USAGES.get(entityName);
        final String featureName = ManufactureClassification.FEATURES.get(entityName);
        final String implName = ManufactureClassification.IMPLS.get(entityName);

        final String usageCode = data.containsKey(usageName) ? String.valueOf(data.get(usageName)) : null;
        final String featureCode = data.containsKey(featureName) ? String.valueOf(data.get(featureName)) : null;
        final String implCode = data.containsKey(implName) ? String.valueOf(data.get(implName)) : null;

        this.checkClassification(usageCode);
        this.checkClassification(featureCode);
        this.checkClassification(implCode);

        final ManufactureClassification.Classification classification = new ManufactureClassification.Classification(usageCode, featureCode, implCode);


        // 2. 处理 定义的 性能特征，为空 则 补空字符串，并生成 性能特征组合编码
        final String specJsonName = ManufactureClassification.SPECS_MAP.get(entityName);
        final String specCodeName = ManufactureClassification.SPEC.get(entityName);
        final ManufactureClassification fetch = this.classificationService.fetch(NAMES.get(entityName), classification.encode());
        final List<ManufactureSpecValues.SpecVal> specValues = fetch.getSpecs().stream()
                .map(s -> new ManufactureSpecValues.SpecVal(s, data.containsKey(s) ? String.valueOf(data.get(s)) : ""))
                .toList();

        data.put(specJsonName, JSONUtil.toJsonStr(specValues));
        data.put(specCodeName, new ManufactureSpecValues(specValues).genCode());

        // 3. 校验4个编码组合的唯一性
        final List<String> names = List.of(usageName, featureName, implName, specCodeName);

        final Map<String, Object> map = this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(entityName)))
                .where(DSL.field(DSL.name("id")).ne(DSL.value(data.get("id"))).and(names.stream()
                        .map(a -> {
                            final Object value = data.get(a);
                            if (value == null) {
                                return DSL.field(DSL.name(a)).isNull();
                            } else {
                                return DSL.field(DSL.name(a)).equal(DSL.value(value));
                            }
                        })
                        .reduce(DSL.trueCondition(), DSL::and)))
                .limit(1)
                .fetchOneMap();
        if (map != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("主数据已经存在，源系统编码：【%s】", map.get(Constants.SOURCE_CODE)));
        }
    }
}
