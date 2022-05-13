package com.eimos.polaris.enums;

/**
 * @author lipengpeng
 */
public enum IndexType {
    /**
     * 无索引
     */
    NONE,
    /**
     * 普通索引
     */
    NAVIGABLE,
    /**
     * 唯一索引
     */
    UNIQUE,
    /**
     * Hash索引
     */
    HASH,
    /**
     * Bitmap索引
     */
    BITMAP
}
