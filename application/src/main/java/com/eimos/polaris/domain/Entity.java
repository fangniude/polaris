package com.eimos.polaris.domain;

import cn.hutool.json.JSONUtil;
import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    private Long id;
    private Namespace namespace;
    private String name;
    private String comment;
    private List<Attribute> attributes;

    public EntityEntity toEntity() {
        return new EntityEntity(this.id, this.namespace, this.name, this.comment,
                JSONUtil.toJsonStr(this.attributes), 0L, 0L, LocalDateTime.now(), Constants.MAX_DATE_TIME);
    }
}
