package com.eimos.polaris.enums;

/**
 * @author lipengpeng
 */
public enum Namespace {
    /**
     * 基础数据
     */
    BD,
    /**
     * 主数据
     */
    MD,
    /**
     * 事务数据
     */
    TD;

    public String tableName(final String str) {
        return String.format("%s_%s", this.name().toLowerCase(), str);
    }
}
