package com.eimos.polaris.controller;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.IdUtil;
import com.eimos.polaris.domain.ManufactureClassification;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.service.ManufactureClassificationService;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.eimos.polaris.vo.AttributeVo.*;

/**
 * @author lipengpeng
 */
@SpringBootTest
class ManufactureClassificationControllerTest {

    private final ManufactureClassificationController controller;
    private final MasterDataController masterDataController;
    private final BasicDataController basicDataController;
    private final ManufactureClassificationService service;

    @Autowired
    public ManufactureClassificationControllerTest(final ManufactureClassificationController controller, final MasterDataController masterDataController, final BasicDataController basicDataController, final ManufactureClassificationService service) {
        this.controller = controller;
        this.masterDataController = masterDataController;
        this.basicDataController = basicDataController;
        this.service = service;
    }

    @BeforeEach
    void pre() {
        this.service.init();
    }

    @Test
    void test() {
        // 1. list is empty
        final String bdEntityName = "物料分类";
        final List<ManufactureClassification> list = this.controller.list(bdEntityName);
        Assertions.assertTrue(list.isEmpty());

        // 2. add one classification
        final String classificationCode = "01_01_01";
        this.controller.add(bdEntityName, new ManufactureClassification(classificationCode, new TreeSet<>(List.of("香水场景细分"))));

        // 3. fetch not null
        final ManufactureClassification classification = this.controller.fetch(bdEntityName, classificationCode);
        Assertions.assertNotNull(classification);
        Assertions.assertEquals(1, classification.getSpecs().size());

        // 4. add one md twice
        this.prepareBasicData();
        final String entityName = "物料";
        this.masterDataController.createEntity(new MasterDataEntityVo(IdUtil.getSnowflakeNextId(), Namespace.MD, entityName, entityName, List.of(id(),
                createTime(),
                updateTime(),
                new AttributeVo("物料名称", "物料名称", DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                new AttributeVo("源系统编码", "源系统编码", DataType.SHORT_TEXT, IndexType.NAVIGABLE, false),
                new AttributeVo("物料性能特征集", "物料性能特征集", DataType.LONG_TEXT, IndexType.NONE, true),
                new AttributeVo("物料性能特征组合编码", "物料性能特征组合编码", DataType.SHORT_TEXT, IndexType.NONE, true),
                fk2bd("物料用途场景"),
                fk2bd("物料功能"),
                fk2bd("物料功能实现方式"))));
        final long id = this.masterDataController.add(entityName, Map.of("物料名称", "abc",
                "源系统编码", "01",
                "物料用途场景编码", "01",
                "物料功能编码", "01",
                "物料功能实现方式编码", "01",
                "香水场景细分", "02"));
        Assertions.assertEquals(this.masterDataController.fetch(entityName, id).get("物料性能特征组合编码"), "02");

        this.masterDataController.modify(entityName, Map.of("物料名称", "abc",
                "id", id,
                "源系统编码", "01",
                "物料用途场景编码", "01",
                "物料功能编码", "01",
                "物料功能实现方式编码", "01",
                "香水场景细分", "02"));

        final long id2 = this.masterDataController.add(entityName, Map.of("物料名称", "abc",
                "源系统编码", "01",
                "物料用途场景编码", "01",
                "物料功能编码", "01",
                "物料功能实现方式编码", "01",
                "香水场景细分", "03"));
        Assertions.assertEquals(this.masterDataController.fetch(entityName, id2).get("物料性能特征组合编码"), "03");
        Assertions.assertThrows(ResponseStatusException.class, () -> this.masterDataController.add(entityName, Map.of("物料名称", "abc",
                "源系统编码", "01",
                "物料用途场景编码", "01",
                "物料功能编码", "01",
                "物料功能实现方式编码", "01",
                "香水场景细分", "02")));

        // 5. add one spec
        this.controller.add(bdEntityName, classificationCode, "香水调型");
        Assertions.assertTrue(this.controller.fetch(bdEntityName, classificationCode).getSpecs().contains("香水调型"));

        Assertions.assertEquals(this.masterDataController.fetch(entityName, id).get("物料性能特征组合编码"), "02_");
        Assertions.assertEquals(this.masterDataController.fetch(entityName, id2).get("物料性能特征组合编码"), "03_");

        // 6. delete spec
        Assertions.assertTrue(this.controller.fetch(bdEntityName, classificationCode).getSpecs().contains("香水场景细分"));
        this.controller.delete(bdEntityName, classificationCode, "香水场景细分");
        Assertions.assertFalse(this.controller.fetch(bdEntityName, classificationCode).getSpecs().contains("香水场景细分"));

        // 7. delete classification
        this.controller.delete(bdEntityName, classificationCode);
        Assertions.assertNull(this.controller.fetch(bdEntityName, classificationCode));

        // 8. drop all basic data
        this.dropAllBasicData();
    }

    private void dropAllBasicData() {
        final List<EntityVo> entityVos = this.basicDataController.entities("", 1000, 1);
        for (final EntityVo entityVo : entityVos) {
            this.basicDataController.drop(entityVo.getName(), true);
        }
    }

    private void prepareBasicData() {
        final String products = "物料用途场景\t消杀\n" +
                "物料用途场景\t美妆\n" +
                "物料用途场景\t日化 \n" +
                "物料用途场景\t同业\n" +
                "物料功能\t皮肤消杀\n" +
                "物料功能\t环境消杀\n" +
                "物料功能\t美妆香氛\n" +
                "物料功能\t日化香氛\n" +
                "物料功能实现方式\t人体消毒喷雾\n" +
                "物料功能实现方式\t免洗消毒液\n" +
                "物料功能实现方式\t消毒啫喱（凝胶）\n" +
                "物料功能实现方式\t消毒湿巾\n" +
                "物料功能实现方式\t环境消毒喷雾\n" +
                "物料功能实现方式\t消毒熏蒸剂\n" +
                "物料功能实现方式\t香水（喷雾）\n" +
                "物料功能实现方式\t香水（油珠）\n" +
                "物料功能实现方式\t身体乳\n" +
                "物料功能实现方式\t护手霜\n" +
                "物料功能实现方式\t香体膏\n" +
                "物料功能实现方式\t发油\n" +
                "物料功能实现方式\t慕斯（免洗）\n" +
                "物料功能实现方式\t沐浴露\n" +
                "物料功能实现方式\t洗发水\n" +
                "物料功能实现方式\t香皂\n" +
                "物料功能实现方式\t慕斯（洗护）\n" +
                "物料功能实现方式\t香蜡\n" +
                "物料功能实现方式\t燃香\n" +
                "物料功能实现方式\t电热香\n" +
                "物料功能实现方式\t藤条氛围香\n" +
                "物料功能实现方式\t香石\n" +
                "物料功能实现方式\t香座\n" +
                "物料功能实现方式\t空气清新剂\n" +
                "物料功能实现方式\t空气加湿香剂\n" +
                "物料功能实现方式\t贴膜\n" +
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