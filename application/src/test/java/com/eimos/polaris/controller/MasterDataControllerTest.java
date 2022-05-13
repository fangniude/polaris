package com.eimos.polaris.controller;

import cn.hutool.core.lang.Pair;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import com.eimos.polaris.enums.MasterDataType;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        // 1. 创建 产品表
        this.controller.createEntity(MasterDataType.PRODUCT.basicModel());

        // 2. 新增列
        final String entityName = MasterDataType.PRODUCT.getName();
        this.controller.create(entityName,
                new AttributeVo("香水调型", "香水调型", DataType.SHORT_TEXT, IndexType.NONE, true,
                        true, false, new AttributeVo.Ref(Namespace.BD, "香水调型", "code")));

        // 3. 新增数据，并查询
        this.controller.add(entityName, Map.of("产品名称", "abc",
                "产品用途场景", "01",
                "产品功能", "01",
                "产品功能实现方式", "01",
                "香水调型", "01"));
        final List<Map<String, Object>> list = this.controller.list(entityName, "", 100, 1);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(list.size(), 1);
        final Map<String, Object> map = list.get(0);
        final Object id = map.get("id");
        Assertions.assertNotNull(id);
        Assertions.assertEquals(map.get("产品用途场景"), "01");
        Assertions.assertEquals(map.get("产品功能"), "01");
        Assertions.assertEquals(map.get("产品功能实现方式"), "01");
        Assertions.assertEquals(map.get("香水调型"), "01");

        // 4. 修改数据，并查询
        this.controller.modify(entityName, Map.of("id", id,
                "产品名称", "abc",
                "产品用途场景", "02",
                "产品功能", "02",
                "产品功能实现方式", "02",
                "香水调型", "02"));
        final List<Map<String, Object>> list1 = this.controller.list(entityName, "", 100, 1);

        Assertions.assertNotNull(list1);
        Assertions.assertEquals(list1.size(), 1);
        final Map<String, Object> map1 = list1.get(0);
        final Object id1 = map1.get("id");
        Assertions.assertNotNull(id1);
        Assertions.assertEquals(map1.get("产品用途场景"), "02");
        Assertions.assertEquals(map1.get("产品功能"), "02");
        Assertions.assertEquals(map1.get("产品功能实现方式"), "02");
        Assertions.assertEquals(map1.get("香水调型"), "02");

        // 5. 删除数据，并查询
        this.controller.delete(entityName, Long.parseLong(id1.toString()));
        final List<Map<String, Object>> list2 = this.controller.list(entityName, "", 100, 1);
        Assertions.assertEquals(list2.size(), 0);

        // 6. 修改列
        this.controller.alter(entityName, new AttributeVo("香水调型", "香水调型", DataType.SHORT_TEXT, IndexType.NONE, true,
                true, false, new AttributeVo.Ref(Namespace.BD, "香水场景细分", "code")));

        // 7. 删除列
        this.controller.drop(entityName, "香水调型", false);

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