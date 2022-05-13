package com.eimos.polaris.vo;

import com.eimos.polaris.entity.EntityEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lipengpeng
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class EntityVo {
    private String name;
    private String comment;

    public static EntityVo valueOf(final EntityEntity entity) {
        return new EntityVo(entity.getName(), entity.getComment());
    }
}
