package com.eimos.polaris.validator;

import cn.hutool.extra.spring.SpringUtil;
import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.domain.Entity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * @author lipengpeng
 */
public class MdValidators {

    /**
     * 校验 主数据增加
     *
     * @param entity 实体
     * @param data   数据
     */
    public static void checkAdd(final Entity entity, final Map<String, Object> data) {
        // 必填项
        for (final Attribute attribute : entity.requiredAttributes()) {
            MdValidators.requireNonNull(data, attribute.getName());
        }

        final MdValidator validator = MdValidators.validator(entity.getName());
        if (validator != null) {
            validator.checkAdd(entity, data);
        }
    }

    /**
     * 校验 主数据修改
     *
     * @param entity 实体
     * @param data   数据
     */
    public static void checkModify(final Entity entity, final Map<String, Object> data) {
        // 必填项
        for (final Attribute attribute : entity.requiredAttributesWithId()) {
            MdValidators.requireNonNull(data, attribute.getName());
        }

        final MdValidator validator = MdValidators.validator(entity.getName());
        if (validator != null) {
            validator.checkModify(entity, data);
        }
    }

    /**
     * 校验 主数据删除
     *
     * @param entity 实体
     * @param id     ID
     */
    public static void checkDelete(final Entity entity, final Long id) {
        final MdValidator validator = MdValidators.validator(entity.getName());
        if (validator != null) {
            validator.checkDelete(entity, id);
        }
    }

    private static MdValidator validator(final String name) {
        return switch (name) {
            case "制造产品", "物料" -> SpringUtil.getBean(Classifier.class);
            default -> null;
        };
    }

    private static void requireNonNull(final Map<String, Object> data, final String attrName) {
        if (data.get(attrName) == null) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "【%s】不能为空".formatted(attrName));
        }
    }
}
