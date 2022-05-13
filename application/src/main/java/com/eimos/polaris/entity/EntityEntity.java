package com.eimos.polaris.entity;

import com.eimos.polaris.enums.Namespace;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(schema = "metadata", name = "entity")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class EntityEntity {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Namespace namespace;
    private String name;
    private String comment;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String attributes;
    private Long version;
    private Long term;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;


}
