package com.eimos.polaris.controller;

import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author lipengpeng
 */
@SpringBootTest
class BasicDataControllerTest {
    private final BasicDataController controller;

    @Autowired
    BasicDataControllerTest(final BasicDataController controller) {
        this.controller = controller;
    }

    @Test
    void test() {
        // 1. 创建基础数据实体
        final String entityName = "单元测试";
        this.controller.create(new EntityVo(entityName, entityName));

        // 2. 查询基础数据实体
        final List<EntityVo> entities = this.controller.entities(entityName, 10, 1);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(entities.size(), 1);

        // 3. 创建数据，查询验证
        final String code = "01";
        this.controller.add(entityName, new BasicDataVo(code, "水"));
        final List<BasicDataVo> list = this.controller.list(entityName, code, 10, 1);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 1);
        Assertions.assertEquals(list.get(0).getName(), "水");

        // 4. 修改数据，查询验证
        this.controller.modify(entityName, new BasicDataVo(code, "电"));
        final List<BasicDataVo> list1 = this.controller.list(entityName, code, 10, 1);
        Assertions.assertNotNull(list1);
        Assertions.assertEquals(list1.size(), 1);
        Assertions.assertEquals(list1.get(0).getName(), "电");

        // 5. 删除数据，查询验证
        this.controller.delete(entityName, code);
        final List<BasicDataVo> list2 = this.controller.list(entityName, code, 10, 1);
        Assertions.assertNotNull(list2);
        Assertions.assertEquals(list2.size(), 0);

        // 6. 删除基础数据实体，查询验证
        this.controller.drop(entityName, false);
        final List<EntityVo> entities1 = this.controller.entities(entityName, 10, 1);
        Assertions.assertNotNull(entities1);
        Assertions.assertEquals(entities1.size(), 0);
    }
}