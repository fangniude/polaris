package com.eimos.polaris.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author lipengpeng
 */
@Entity
public class RelationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sourceEntityId;
    private Long referenceEntityId;
    private Boolean oneToOne;
    private String mapping;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;
    private String sourcePoint;
    private String referencePoint;
}
