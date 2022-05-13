package com.eimos.polaris.repository;

import com.eimos.polaris.entity.RelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author lipengpeng
 */
public interface RelationRepository extends JpaRepository<RelationEntity, Long> {
    /**
     * 根据源实体ID查询
     *
     * @param sourceEntityId 源实体ID
     * @return 所有关系
     */
    List<RelationEntity> findBySourceEntityId(Long sourceEntityId);

    /**
     * 根据 引用实体ID查询
     *
     * @param entityId 引用实体ID
     * @return 所有关系
     */
    List<RelationEntity> findByReferenceEntityId(Long entityId);
}
