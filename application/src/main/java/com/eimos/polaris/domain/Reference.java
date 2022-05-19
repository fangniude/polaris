package com.eimos.polaris.domain;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.eimos.polaris.domain.mapping.EqualMapping;
import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.entity.RelationEntity;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.repository.EntityRepository;
import com.eimos.polaris.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reference {
    private Namespace sourceNamespace;
    private String sourceEntity;
    private String sourceAttribute;

    private Namespace refNamespace;
    private String refEntity;
    private String refAttribute;

    private Boolean oneToOne;

    public RelationEntity toRelation() {
        return new RelationEntity(IdUtil.getSnowflakeNextId(), this.getSourceEntityId(), this.getReferenceEntityId(), this.oneToOne, this.toMapping(), LocalDateTime.now(), Constants.MAX_DATE_TIME, "", "");
    }

    public static Reference fromEntityRelation() {
        return new Reference();
    }

    private Long getSourceEntityId() {
        return this.getEntityId(this.sourceNamespace, this.sourceEntity);
    }

    private Long getReferenceEntityId() {
        return this.getEntityId(this.refNamespace, this.refEntity);
    }

    /**
     * 这是临时方案，后面做前端时，会从前端直接传回来 ID，不需要再根据名字从数据库中查询
     */
    private Long getEntityId(final Namespace namespace, final String entityName) {
        final Optional<EntityEntity> optional = SpringUtil.getBean(EntityRepository.class).findOneByNamespaceAndName(namespace, entityName);
        if (optional.isPresent()) {
            return optional.get().getId();
        } else {
            throw new IllegalArgumentException(String.format("entity not exists, namespace: %s, entity: %s", namespace, entityName));
        }
    }

    private String toMapping() {
        return JSONUtil.toJsonStr(new EqualMapping(this.sourceAttribute, this.refAttribute));
    }

    public static EqualMapping fromMapping(final String mapping) {
        return JSONUtil.toBean(mapping, EqualMapping.class);
    }

}
