package com.eimos.polaris.controller;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.eimos.polaris.vo.AttributeVo.*;

/**
 * @author lipengpeng
 */
@SpringBootTest
class MasterDataControllerTest {
    private final MasterDataController controller;
    private final BasicDataController basicDataController;

    @Autowired
    MasterDataControllerTest(final MasterDataController controller, final BasicDataController basicDataController) {
        this.controller = controller;
        this.basicDataController = basicDataController;
    }

    @Test
    void test() {
        this.prepareBasicData();

        // 0. 产品不存在，抛出异常
        final String entityName = "产品";
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> this.controller.fetch(entityName));

        // 1. 创建 产品表
        this.controller.createEntity(new MasterDataEntityVo(IdUtil.getSnowflakeNextId(), Namespace.MD, entityName, entityName, List.of(id(),
                createTime(),
                updateTime(),
                new AttributeVo("产品名称", "产品名称", DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                new AttributeVo("源系统编码", "源系统编码", DataType.SHORT_TEXT, IndexType.UNIQUE, false),
                fk2bd("产品用途场景"),
                fk2bd("产品功能"),
                fk2bd("产品功能实现方式"))));
        Assertions.assertNotNull(this.controller.fetch(entityName));

        // 2. 新增列
        this.controller.create(entityName,
                new AttributeVo("香水调型", "香水调型", DataType.SHORT_TEXT, IndexType.NONE, true,
                        true, false, new AttributeVo.Ref(Namespace.BD, "香水调型", "code")));
        Assertions.assertTrue(this.controller.fetch(entityName).getAttributes().stream().anyMatch(a -> Objects.equals(a.getName(), "香水调型")));

        // 3. 新增数据，并查询
        final long id = this.controller.add(entityName, Map.of("产品名称", "abc",
                "源系统编码", "01",
                "产品用途场景", "01",
                "产品功能", "01",
                "产品功能实现方式", "01",
                "香水调型", "01"));
        final Map<String, Object> map = this.controller.fetch(entityName, id);
        Assertions.assertEquals(map.get("产品用途场景"), "01");
        Assertions.assertEquals(map.get("产品功能"), "01");
        Assertions.assertEquals(map.get("产品功能实现方式"), "01");
        Assertions.assertEquals(map.get("香水调型"), "01");

        // 4. 修改数据，并查询
        this.controller.modify(entityName, Map.of("id", id,
                "产品名称", "abc",
                "源系统编码", "02",
                "产品用途场景", "02",
                "产品功能", "02",
                "产品功能实现方式", "02",
                "香水调型", "02"));
        final Map<String, Object> map1 = this.controller.fetch(entityName, id);
        Assertions.assertEquals(map1.get("产品用途场景"), "02");
        Assertions.assertEquals(map1.get("产品功能"), "02");
        Assertions.assertEquals(map1.get("产品功能实现方式"), "02");
        Assertions.assertEquals(map1.get("香水调型"), "02");

        // 5. 删除数据，并查询
        this.controller.delete(entityName, id);
        Assertions.assertThrows(ResponseStatusException.class, () -> this.controller.fetch(entityName, id));

        // 6. 修改列
        this.controller.alter(entityName, new AttributeVo("香水调型", "香水调型", DataType.SHORT_TEXT, IndexType.NONE, true,
                true, false, new AttributeVo.Ref(Namespace.BD, "香水场景细分", "code")));

        // 7. 删除列
        this.controller.drop(entityName, "香水调型", false);
        Assertions.assertFalse(this.controller.fetch(entityName).getAttributes().stream().anyMatch(a -> Objects.equals(a.getName(), "香水调型")));

        // 8. 删除实体
        this.controller.dropEntity(entityName);
    }

    private void prepareBasicData() {
        final String products = "产品用途场景\t消杀\n" +
                "产品用途场景\t美妆\n" +
                "产品用途场景\t日化 \n" +
                "产品用途场景\t同业\n" +
                "产品功能\t皮肤消杀\n" +
                "产品功能\t环境消杀\n" +
                "产品功能\t美妆香氛\n" +
                "产品功能\t日化香氛\n" +
                "产品功能实现方式\t人体消毒喷雾\n" +
                "产品功能实现方式\t免洗消毒液\n" +
                "产品功能实现方式\t消毒啫喱（凝胶）\n" +
                "产品功能实现方式\t消毒湿巾\n" +
                "产品功能实现方式\t环境消毒喷雾\n" +
                "产品功能实现方式\t消毒熏蒸剂\n" +
                "产品功能实现方式\t香水（喷雾）\n" +
                "产品功能实现方式\t香水（油珠）\n" +
                "产品功能实现方式\t身体乳\n" +
                "产品功能实现方式\t护手霜\n" +
                "产品功能实现方式\t香体膏\n" +
                "产品功能实现方式\t发油\n" +
                "产品功能实现方式\t慕斯（免洗）\n" +
                "产品功能实现方式\t沐浴露\n" +
                "产品功能实现方式\t洗发水\n" +
                "产品功能实现方式\t香皂\n" +
                "产品功能实现方式\t慕斯（洗护）\n" +
                "产品功能实现方式\t香蜡\n" +
                "产品功能实现方式\t燃香\n" +
                "产品功能实现方式\t电热香\n" +
                "产品功能实现方式\t藤条氛围香\n" +
                "产品功能实现方式\t香石\n" +
                "产品功能实现方式\t香座\n" +
                "产品功能实现方式\t空气清新剂\n" +
                "产品功能实现方式\t空气加湿香剂\n" +
                "产品功能实现方式\t贴膜\n" +
                "香水场景细分\t男性\n" +
                "香水场景细分\t女性\n" +
                "香水场景细分\t中性\n" +
                "香水调型\t柔和花香\n" +
                "香水调型\t柑橘\n" +
                "香水调型\t果香\n" +
                "香水调型\t青香\n" +
                "香水调型\t素兰心\n" +
                "香水调型\t水生\n" +
                "香水调型\t馥奇\n" +
                "香水调型\t皮革\n" +
                "香水调型\t木质\n" +
                "香水调型\t东方香\n" +
                "香水调型\t芳香\n" +
                "香水调型\t美食";

        final String[] rows = products.split("\n");
        final List<String> all = List.of(rows);
        final Map<String, List<Pair<String, String>>> entities = all.stream().map(r -> r.split("\t"))
                .map(s -> Pair.of(s[0], s[1]))
                .collect(Collectors.groupingBy(Pair::getKey));

        for (final Map.Entry<String, List<Pair<String, String>>> entry : entities.entrySet()) {
            final String entityName = entry.getKey();
            this.basicDataController.create(new EntityVo(entityName, entityName));

            final List<Pair<String, String>> items = entry.getValue();
            for (int i = 0; i < items.size(); i++) {
                final String v = items.get(i).getValue();
                this.basicDataController.add(entityName, new BasicDataVo(String.format("%02d", i + 1), v));
            }
        }
    }
}