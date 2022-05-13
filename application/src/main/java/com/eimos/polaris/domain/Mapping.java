package com.eimos.polaris.domain;

import com.eimos.polaris.domain.mapping.EqualMapping;
import com.eimos.polaris.enums.MappingType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author lipengpeng
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "mappingType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EqualMapping.class, name = MappingType.Names.EQUAL)
})
public interface Mapping {
    /**
     * 映射类型
     *
     * @return 映射类型
     */
    MappingType getMappingType();
}
