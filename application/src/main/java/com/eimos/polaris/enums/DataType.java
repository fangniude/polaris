package com.eimos.polaris.enums;

import org.jooq.impl.SQLDataType;

/**
 * @author lipengpeng
 */
public enum DataType {
    /**
     * 短文本，100个字符(含100)以内，认为是短文本
     */
    SHORT_TEXT(SQLDataType.VARCHAR.length(100)),
    /**
     * 长文本
     */
    LONG_TEXT(SQLDataType.LONGNVARCHAR),
    /**
     * 整数
     */
    INTEGER(SQLDataType.BIGINT),
    /**
     * 小数，小数点前20位，小数点后10位
     */
    DECIMAL(SQLDataType.DECIMAL.precision(30, 10)),
    /**
     * 日期
     */
    DATE(SQLDataType.LOCALDATE),
    /**
     * 时间
     */
    DATE_TIME(SQLDataType.LOCALDATETIME),
    /**
     * 空，数据库不用，返回数据的时候需要
     */
    NONE(null);

    @SuppressWarnings("rawtypes")
    public final org.jooq.DataType dbDataType;

    @SuppressWarnings("rawtypes")
    DataType(final org.jooq.DataType dbDataType) {
        this.dbDataType = dbDataType;
    }
}
