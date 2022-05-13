package com.eimos.polaris.domain;

import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
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
public class Attribute {
    private String name;
    private String comment;
    private DataType dataType;
    private IndexType index;
    private Boolean nullable;

    public Attribute(final String name, final String comment, final DataType dataType, final IndexType index, final Boolean nullable) {
        this.name = name;
        this.comment = comment;
        this.dataType = dataType;
        this.index = index;
        this.nullable = nullable;
    }
}
