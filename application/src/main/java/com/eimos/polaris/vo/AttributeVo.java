package com.eimos.polaris.vo;

import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import com.eimos.polaris.enums.Namespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AttributeVo {
    private String name;
    private String comment;
    private DataType dataType;
    private IndexType index;
    private Boolean nullable;

    private Boolean foreignKey;
    private Boolean oneToOne;
    private Ref ref;

    public AttributeVo(final String name, final String comment, final DataType dataType, final IndexType index, final boolean nullable) {
        this.name = name;
        this.comment = comment;
        this.dataType = dataType;
        this.index = index;
        this.nullable = nullable;
        this.foreignKey = false;
        this.oneToOne = false;
        this.ref = null;
    }

    public static AttributeVo fromDomain(final Attribute attribute, final Reference reference) {
        if (reference != null) {
            return new AttributeVo(attribute.getName(), attribute.getComment(), attribute.getDataType(), attribute.getIndex(), attribute.getNullable(),
                    true, reference.getOneToOne(), new Ref(reference.getRefNamespace(), reference.getRefEntity(), reference.getRefAttribute()));
        } else {
            return new AttributeVo(attribute.getName(), attribute.getComment(), attribute.getDataType(), attribute.getIndex(), attribute.getNullable());
        }
    }

    public Attribute toDomain() {
        return new Attribute(this.name, this.comment, this.dataType, this.index, this.nullable);
    }


    public Reference toRef(final Namespace namespace, final String entityName) {
        return new Reference(namespace, entityName, this.name,
                this.ref.getNamespace(), this.ref.getEntityName(), this.ref.getAttributeName(),
                this.oneToOne);
    }

    public static AttributeVo id() {
        return new AttributeVo("id", "ID", DataType.INTEGER, IndexType.UNIQUE, false);
    }

    public static AttributeVo createTime() {
        return new AttributeVo("create_time", "创建时间", DataType.DATE_TIME, IndexType.NONE, false);
    }

    public static AttributeVo updateTime() {
        return new AttributeVo("update_time", "更新时间", DataType.DATE_TIME, IndexType.NONE, false);
    }

    public static AttributeVo fk2bd(final String name) {
        return new AttributeVo(name + "编码", name + "编码", DataType.SHORT_TEXT, IndexType.NONE, true,
                true, false, new AttributeVo.Ref(Namespace.BD, name, "code"));
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ref {
        private Namespace namespace;
        private String entityName;
        private String attributeName;
    }
}
