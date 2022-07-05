package com.eimos.polaris.validator;

import com.eimos.polaris.domain.Entity;

import java.util.Map;

/**
 * @author lipengpeng
 */
public interface MdValidator {
    /**
     * 校验 主数据增加
     *
     * @param entity 实体
     * @param data   数据
     */
    default void checkAdd(final Entity entity, final Map<String, Object> data) {
    }

    /**
     * 校验 主数据修改
     *
     * @param entity 实体
     * @param data   数据
     */
    default void checkModify(final Entity entity, final Map<String, Object> data) {
    }

    /**
     * 校验 主数据删除
     *
     * @param entity 实体
     * @param id     ID
     */
    default void checkDelete(final Entity entity, final Long id) {
    }
}
