package com.eimos.polaris.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(schema = "metadata", name = "relation")
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class RelationEntity {
    @Id
    private Long id;
    private Long sourceEntityId;
    private Long referenceEntityId;
    private Boolean oneToOne;
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String mapping;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;
    private String sourcePoint;
    private String referencePoint;
}
