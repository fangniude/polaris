package com.eimos.polaris;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.eimos.polaris.domain.Attribute;
import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.Reference;
import com.eimos.polaris.domain.mapping.EqualMapping;
import com.eimos.polaris.entity.EntityEntity;
import com.eimos.polaris.entity.RelationEntity;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author lipengpeng
 */
public class TestPojo {
    private final List<Class<?>> vos = List.of(AttributeVo.class, BasicDataVo.class, EntityVo.class, MasterDataEntityVo.class);
    private final List<Class<?>> entities = List.of(EntityEntity.class, RelationEntity.class);
    private final List<Class<?>> domains = List.of(Attribute.class, Entity.class, Reference.class, EqualMapping.class);

    @Test
    public void test() {
        this.test(this.vos);
        this.test(this.entities);
        this.test(this.domains);
    }

    private void test(final List<Class<?>> os) {
        os.stream().map(ReflectUtil::newInstance).forEach(Assertions::assertNotNull);
        for (final Class<?> oc : os) {
            final Object o = ReflectUtil.newInstance(oc);
            final String json = JSONUtil.toJsonStr(o);
            final Object o1 = JSONUtil.toBean(json, oc);
            Assertions.assertNotNull(o1);
        }
    }
}
