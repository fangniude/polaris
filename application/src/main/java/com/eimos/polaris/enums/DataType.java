package com.eimos.polaris.enums;

import org.jooq.impl.SQLDataType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author lipengpeng
 */
public enum DataType {
    /**
     * 短文本，255个字符(含255)以内，认为是短文本
     */
    SHORT_TEXT(SQLDataType.VARCHAR.length(255), String.class),
    /**
     * 长文本
     */
    LONG_TEXT(SQLDataType.LONGNVARCHAR, String.class),
    /**
     * 整数
     */
    INTEGER(SQLDataType.BIGINT, Long.class),
    /**
     * 小数，小数点前20位，小数点后10位
     */
    DECIMAL(SQLDataType.DECIMAL.precision(30, 10), BigDecimal.class),
    /**
     * 日期
     */
    DATE(SQLDataType.LOCALDATE, LocalDate.class),
    /**
     * 时间
     */
    DATE_TIME(SQLDataType.LOCALDATETIME, LocalDateTime.class),
    /**
     * 空，数据库不用，返回数据的时候需要
     */
    NONE(null, void.class);

    @SuppressWarnings("rawtypes")
    public final org.jooq.DataType dbDataType;

    public final Class<?> javaClass;

    @SuppressWarnings("rawtypes")
    DataType(final org.jooq.DataType dbDataType, final Class<?> javaClass) {
        this.dbDataType = dbDataType;
        this.javaClass = javaClass;
    }
}
