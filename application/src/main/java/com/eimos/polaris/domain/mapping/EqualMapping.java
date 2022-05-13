package com.eimos.polaris.domain.mapping;

import com.eimos.polaris.domain.Mapping;
import com.eimos.polaris.enums.MappingType;
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
public class EqualMapping implements Mapping {
    private String sourceAttributeName;
    private String referenceAttributeName;

    @Override
    public MappingType getMappingType() {
        return MappingType.EQUAL;
    }
}
