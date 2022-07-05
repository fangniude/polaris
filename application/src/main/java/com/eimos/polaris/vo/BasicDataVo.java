package com.eimos.polaris.vo;

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
public class BasicDataVo {
    private String code;
    private String name;

    public BasicDataVo(final String code, final String name) {
        this.code = code;
        this.name = name;
    }
}
