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

    public Attribute toDomain() {
        return new Attribute(this.name, this.comment, this.dataType, this.index, this.nullable);
    }


    public Reference toRef(final Namespace namespace, final String entityName) {
        return new Reference(namespace, entityName, this.name,
                this.ref.getNamespace(), this.ref.getEntityName(), this.ref.getAttributeName(),
                this.oneToOne);
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
