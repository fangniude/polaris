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
import java.util.Objects;

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
    private Long version;
    private Long term;
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;

    public Entity(final EntityEntity e) {
        this.id = e.getId();
        this.namespace = e.getNamespace();
        this.name = e.getName();
        this.comment = e.getComment();
        this.attributes = JSONUtil.toList(e.getAttributes(), Attribute.class);
        this.version = e.getVersion();
        this.term = e.getTerm();
        this.createTime = e.getCreateTime();
        this.expiredTime = e.getExpiredTime();
    }

    public Entity(final Long id, final Namespace namespace, final String name, final String comment, final List<Attribute> attributes) {
        this.id = id;
        this.namespace = namespace;
        this.name = name;
        this.comment = comment;
        this.attributes = attributes;
    }

    public EntityEntity toEntity() {
        return new EntityEntity(this.id, this.namespace, this.name, this.comment,
                JSONUtil.toJsonStr(this.attributes), this.getVersion(), this.getTerm(), this.getCreateTime(), this.getExpiredTime());
    }

    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void deleteAttribute(final String attributeName) {
        this.attributes = this.attributes.stream()
                .filter(a -> !Objects.equals(a.getName(), attributeName))
                .toList();
    }

    public Long getVersion() {
        return Objects.requireNonNullElse(this.version, 0L);
    }

    public Long getTerm() {
        return Objects.requireNonNullElse(this.term, 0L);
    }

    public LocalDateTime getCreateTime() {
        return Objects.requireNonNullElse(this.createTime, LocalDateTime.now());
    }

    public LocalDateTime getExpiredTime() {
        return Objects.requireNonNullElse(this.expiredTime, Constants.MAX_DATE_TIME);
    }
}
