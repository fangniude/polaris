package com.eimos.polaris.repository;

import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.enums.Namespace;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author lipengpeng
 */
public interface EntityRepository extends JpaRepository<EntityEntity, Long> {
    /**
     * 根据 实体编码 和 实体名称查找，带分页
     *
     * @param name     实体名称
     * @param comment  实体注释
     * @param pageable 分页
     * @return 所有列表
     */
    List<EntityEntity> findByNameContainsOrCommentContains(String name, String comment, Pageable pageable);

    /**
     * 根据实体编码，查找实体
     *
     * @param namespace 命名空间
     * @param name      实体名称
     * @return 实体
     */
    Optional<EntityEntity> findOneByNamespaceAndName(Namespace namespace, String name);

    /**
     * 根据 实体编码 和 实体名称查找，带分页
     *
     * @param namespace  命名空间
     * @param name       实体名称
     * @param namespace1 命名空间
     * @param comment    实体注释
     * @param pageable   分页
     * @return 所有列表
     */
    List<EntityEntity> findByNamespaceAndNameContainsOrNamespaceAndCommentContains(Namespace namespace, String name, Namespace namespace1, String comment, Pageable pageable);
}
