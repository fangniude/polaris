package com.eimos.polaris.enums;

import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.eimos.polaris.vo.AttributeVo.*;

/**
 * @author lipengpeng
 */
public enum MasterDataType {

    /**
     * 产品
     */
    PRODUCT(Constants.PRODUCT, Constants.PRODUCT,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.PRODUCT_NAME, Constants.PRODUCT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SOURCE_CODE, Constants.SOURCE_CODE, DataType.SHORT_TEXT, IndexType.UNIQUE, false),
                    fk2bd(Constants.PRODUCT_USAGE),
                    fk2bd(Constants.PRODUCT_FEATURE),
                    fk2bd(Constants.PRODUCT_IMPLEMENTS)),
            List.of(Constants.PRODUCT_NAME)),

    /**
     * 物料
     */
    MATERIAL(Constants.MATERIAL, Constants.MATERIAL,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.MATERIAL_NAME, Constants.MATERIAL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SOURCE_CODE, Constants.SOURCE_CODE, DataType.SHORT_TEXT, IndexType.UNIQUE, false),
                    fk2bd(Constants.MATERIAL_USAGE),
                    fk2bd(Constants.MATERIAL_FEATURE),
                    fk2bd(Constants.MATERIAL_IMPLEMENTS)),
            List.of(Constants.MATERIAL_NAME)),

    /**
     * 客户
     */
    CUSTOMER(Constants.CUSTOMER, Constants.CUSTOMER,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.CUSTOMER_FULL_NAME, Constants.CUSTOMER_FULL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_SHORT_NAME, Constants.CUSTOMER_SHORT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_INDUSTRY, Constants.CUSTOMER_INDUSTRY, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.INDUSTRY, Constants.CODE))),
            List.of(Constants.CUSTOMER_FULL_NAME, Constants.CUSTOMER_SHORT_NAME)),

    /**
     * 客户法人
     */
    CUSTOMER_LEGAL(Constants.CUSTOMER_LEGAL, Constants.CUSTOMER_LEGAL,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.CUSTOMER_LEGAL_FULL_NAME, Constants.CUSTOMER_LEGAL_FULL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_LEGAL_SHORT_NAME, Constants.CUSTOMER_LEGAL_SHORT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_ID, Constants.CUSTOMER_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.CUSTOMER, Constants.ID))),
            List.of(Constants.CUSTOMER_LEGAL_FULL_NAME, Constants.CUSTOMER_LEGAL_SHORT_NAME)),

    /**
     * 客户产品
     */
    CUSTOMER_PRODUCT(Constants.CUSTOMER_PRODUCT, Constants.CUSTOMER_PRODUCT,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.CUSTOMER_PRODUCT_NAME, Constants.CUSTOMER_PRODUCT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_ID, Constants.CUSTOMER_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.CUSTOMER, Constants.ID)),
                    new AttributeVo(Constants.PRODUCT_ID, Constants.PRODUCT_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.PRODUCT, Constants.ID)),
                    new AttributeVo(Constants.TRADE_TYPE, Constants.TRADE_TYPE, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.TRADE_TYPE, Constants.CODE)),
                    new AttributeVo(Constants.ORDER_CYCLE, Constants.ORDER_CYCLE, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.GOODS_CYCLE, Constants.CODE))),
            List.of(Constants.CUSTOMER_PRODUCT_NAME)),

    /**
     * 供应商
     */
    SUPPLIER(Constants.SUPPLIER, Constants.SUPPLIER,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.SUPPLIER_FULL_NAME, Constants.SUPPLIER_FULL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SUPPLIER_SHORT_NAME, Constants.SUPPLIER_SHORT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SUPPLIER_INDUSTRY, Constants.SUPPLIER_INDUSTRY, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.INDUSTRY, Constants.CODE))),
            List.of(Constants.SUPPLIER_FULL_NAME, Constants.SUPPLIER_SHORT_NAME)),

    /**
     * 供应商法人
     */
    SUPPLIER_LEGAL(Constants.SUPPLIER_LEGAL, Constants.SUPPLIER_LEGAL,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.SUPPLIER_LEGAL_FULL_NAME, Constants.SUPPLIER_LEGAL_FULL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SUPPLIER_LEGAL_SHORT_NAME, Constants.SUPPLIER_LEGAL_SHORT_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.CUSTOMER_ID, Constants.CUSTOMER_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.CUSTOMER, Constants.ID))),
            List.of(Constants.SUPPLIER_LEGAL_FULL_NAME, Constants.SUPPLIER_LEGAL_SHORT_NAME)),

    /**
     * 供应商物料
     */
    SUPPLIER_MATERIAL(Constants.SUPPLIER_MATERIAL, Constants.SUPPLIER_MATERIAL,
            List.of(id(),
                    createTime(),
                    updateTime(),
                    new AttributeVo(Constants.SUPPLIER_MATERIAL_NAME, Constants.SUPPLIER_MATERIAL_NAME, DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                    new AttributeVo(Constants.SUPPLIER_ID, Constants.SUPPLIER_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.SUPPLIER, Constants.ID)),
                    new AttributeVo(Constants.MATERIAL_ID, Constants.MATERIAL_ID, DataType.INTEGER, IndexType.NONE, false,
                            true, false, new AttributeVo.Ref(Namespace.MD, Constants.MATERIAL, Constants.ID)),
                    new AttributeVo(Constants.TRADE_TYPE, Constants.TRADE_TYPE, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.TRADE_TYPE, Constants.CODE)),
                    new AttributeVo(Constants.SUPPLY_CYCLE, Constants.SUPPLY_CYCLE, DataType.SHORT_TEXT, IndexType.NONE, true,
                            true, false, new AttributeVo.Ref(Namespace.BD, Constants.GOODS_CYCLE, Constants.CODE))),
            List.of(Constants.SUPPLIER_MATERIAL_NAME));

    private static final Map<String, MasterDataType> MAP = Arrays.stream(MasterDataType.values()).collect(Collectors.toMap(MasterDataType::getName, Function.identity()));

    @Getter
    private final String name;
    private final String comment;
    private final List<AttributeVo> attributes;
    @Getter
    private final List<String> searchAttributes;

    MasterDataType(final String name, final String comment, final List<AttributeVo> attributes, final List<String> searchAttributes) {
        this.name = name;
        this.comment = comment;
        this.attributes = attributes;
        this.searchAttributes = searchAttributes;
    }

    public static MasterDataType of(final String masterData) {
        final MasterDataType masterDataType = MasterDataType.MAP.get(masterData);
        if (masterDataType != null) {
            return masterDataType;
        } else {
            throw new IllegalArgumentException(String.format("主数据：%s 不存在", masterData));
        }
    }

    public MasterDataEntityVo basicModel() {
        return new MasterDataEntityVo(IdUtil.getSnowflakeNextId(), Namespace.MD, this.name, this.comment, this.attributes);
    }

    public static final class Constants {
        public static final String ID = "id";
        public static final String CODE = "code";
        public static final String SOURCE_CODE = "源系统编码";

        public static final String PRODUCT = "产品";
        public static final String PRODUCT_NAME = "产品名称";
        public static final String PRODUCT_USAGE = "产品用途场景";
        public static final String PRODUCT_FEATURE = "产品功能";
        public static final String PRODUCT_IMPLEMENTS = "产品功能实现方式";

        public static final String MATERIAL = "物料";
        public static final String MATERIAL_NAME = "物料名称";
        public static final String MATERIAL_USAGE = "物料用途场景";
        public static final String MATERIAL_FEATURE = "物料功能";
        public static final String MATERIAL_IMPLEMENTS = "物料功能实现方式";

        public static final String CUSTOMER = "客户";
        public static final String CUSTOMER_FULL_NAME = "客户全称";
        public static final String CUSTOMER_SHORT_NAME = "客户简称";
        public static final String CUSTOMER_INDUSTRY = "客户行业";
        public static final String ORDER_CYCLE = "订货周期";

        public static final String CUSTOMER_LEGAL = "客户法人";
        public static final String CUSTOMER_LEGAL_FULL_NAME = "客户法人全称";
        public static final String CUSTOMER_LEGAL_SHORT_NAME = "客户法人简称";

        public static final String CUSTOMER_PRODUCT = "客户产品";
        public static final String CUSTOMER_PRODUCT_NAME = "客户产品名称";
        public static final String CUSTOMER_ID = "客户ID";
        public static final String PRODUCT_ID = "产品ID";

        public static final String SUPPLIER = "供应商";
        public static final String SUPPLIER_FULL_NAME = "供应商全称";
        public static final String SUPPLIER_SHORT_NAME = "供应商简称";
        public static final String SUPPLIER_INDUSTRY = "供应商行业";
        public static final String SUPPLY_CYCLE = "供货周期";

        public static final String SUPPLIER_LEGAL = "供应商法人";
        public static final String SUPPLIER_LEGAL_FULL_NAME = "供应商法人全称";
        public static final String SUPPLIER_LEGAL_SHORT_NAME = "供应商法人简称";

        public static final String SUPPLIER_MATERIAL = "供应商物料";
        public static final String SUPPLIER_MATERIAL_NAME = "供应商物料名称";
        public static final String SUPPLIER_ID = "供应商ID";
        public static final String MATERIAL_ID = "物料ID";

        public static final String INDUSTRY = "行业";
        public static final String TRADE_TYPE = "交易界面";
        public static final String GOODS_CYCLE = "典型货期";

        private Constants() {
        }
    }
}
