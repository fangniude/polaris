package com.eimos.polaris.vo;

import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.enums.Namespace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataEntityVo {
    private Long id;
    private Namespace namespace;
    private String name;
    private String comment;
    private List<AttributeVo> attributes;

    public Entity toEntity() {
        return new Entity(this.id, this.namespace, this.name, this.comment, this.attributes.stream()
                .map(AttributeVo::toDomain)
                .toList());
    }

    public List<Reference> toReferences() {
        return this.attributes.stream()
                .filter(AttributeVo::getForeignKey)
                .map(this::getReference)
                .toList();
    }

    private Reference getReference(final AttributeVo r) {
        final AttributeVo.Ref ref = r.getRef();
        return new Reference(this.namespace, this.name, r.getName(),
                ref.getNamespace(), ref.getEntityName(), ref.getAttributeName(), r.getOneToOne());
    }
}
