package com.eimos.polaris.util;

import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lipengpeng
 */
public final class Constants {

    private Constants() {
    }

    /**
     * 各类系统能表示的太大了，并且不统一，Java到 999_999_999 年，PostgreSQL的Timestamp到294276年；
     * <p>
     * 我们先定到1万年，但愿我们的系统活不到那个时候
     */
    public static final LocalDateTime MAX_DATE_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    /**
     * 为了格式化方便，年份需要4位，定于 1000年到9999年
     */
    public static final LocalDateTime MIN_DATE_TIME = LocalDateTime.of(1000, 1, 1, 0, 0, 0);

    public static final LocalDate MIN_DATE = LocalDate.of(1000, 1, 1);
    public static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);

    public static final List<Attribute> BASIC_DATA_ATTRIBUTES = List.of(new Attribute("code", "编码", DataType.SHORT_TEXT, IndexType.UNIQUE, false),
            new Attribute("name", "名称", DataType.SHORT_TEXT, IndexType.NONE, false),
            new Attribute("create_time", "创建时间", DataType.DATE_TIME, IndexType.NONE, false),
            new Attribute("update_time", "更新时间", DataType.DATE_TIME, IndexType.NONE, false));

    public static final List<Field<Object>> BASIC_DATA_FIELDS = Constants.BASIC_DATA_ATTRIBUTES.stream()
            .map(Attribute::getName)
            .map(DSL::name)
            .map(DSL::field)
            .toList();
    public static final String SOURCE_CODE = "源系统编码";
}
