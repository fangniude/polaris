package com.eimos.polaris.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.eimos.polaris.enums.MasterDataType;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lipengpeng
 */
@SpringBootTest(properties = "spring.profiles.active:pg")
class XueLeiDataTest {
    private final BasicDataController basicDataController;
    private final MasterDataController masterDataController;

    @Autowired
    XueLeiDataTest(final BasicDataController basicDataController, final MasterDataController masterDataController) {
        this.basicDataController = basicDataController;
        this.masterDataController = masterDataController;
    }


    //    @Test
    public void test() {
        // 删除所有基础数据表，重新导入
        this.dropAllBasicData();

        // 产品物料的 基础数据
        this.productMaterial();

        // 客户、供应商 基础数据
        for (final String s : List.of("行业", "交易界面", "典型货期")) {
            this.basicDataController.create(new EntityVo(s, s));
        }

        // 主数据
        this.masterData();
    }

    private void dropAllBasicData() {
        final List<EntityVo> entityVos = this.basicDataController.entities("", 1000, 1);
        for (final EntityVo entityVo : entityVos) {
            this.basicDataController.drop(entityVo.getName(), true);
        }
    }

    private void productMaterial() {
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
                "香水调型\t美食\n" +
                "香水浓度等级\t香精（P）\n" +
                "香水浓度等级\t淡香精（EDP）\n" +
                "香水浓度等级\t淡香水（EDT）\n" +
                "香水浓度等级\t古龙水（EDC）\n" +
                "香水浓度等级\t清单水（EDF）\n" +
                "香水瓶体与包装风格\t时尚简约\n" +
                "香水瓶体与包装风格\t环保自然\n" +
                "香水瓶体与包装风格\t青春可爱\n" +
                "香水瓶体与包装风格\t花果元素\n" +
                "香水瓶体与包装风格\t线条结构\n" +
                "香水瓶体与包装风格\t抽象艺术\n" +
                "香水瓶体与包装风格\t建筑风景\n" +
                "香水瓶体与包装风格\t东方国潮\n" +
                "香水瓶体与包装风格\t节庆活动\n" +
                "香水容量\t1.8ml\n" +
                "香水容量\t2.0ml\n" +
                "香水容量\t2.5ml\n" +
                "香水容量\t5.0ml\n" +
                "香水容量\t6.0ml\n" +
                "香水容量\t9.0ml\n" +
                "香水容量\t10.0ml\n" +
                "香水容量\t12.0ml\n" +
                "香水容量\t15.0ml\n" +
                "香水容量\t20.0ml\n" +
                "香水容量\t25.0ml\n" +
                "香水容量\t30.0ml\n" +
                "香水容量\t35.0ml\n" +
                "香水容量\t40.0ml\n" +
                "香水容量\t45.0ml\n" +
                "香水容量\t50.0ml\n" +
                "香水容量\t60.0ml\n" +
                "香水容量\t90.0ml\n" +
                "香水容量\t100.0ml\n" +
                "香水容量\t125.0ml\n" +
                "香水容量\t150.0ml";

        final String materials = "物料用途场景\t包装\n" +
                "物料用途场景\t容器\n" +
                "物料用途场景\t封口\n" +
                "物料用途场景\t装饰\n" +
                "物料用途场景\t出料\n" +
                "物料用途场景\t料体\n" +
                "物料功能\t单品包装\n" +
                "物料功能\t礼盒包装\n" +
                "物料功能\t物流包装\n" +
                "物料功能\t水剂容器\n" +
                "物料功能\t油剂容器\n" +
                "物料功能\t固体容器\n" +
                "物料功能\t胶体容器\n" +
                "物料功能\t半流体容器\n" +
                "物料功能\t密封闭锁封口\n" +
                "物料功能\t非密封封闭封口\n" +
                "物料功能\t直通封口\n" +
                "物料功能\t容器装饰\n" +
                "物料功能\t功能装饰\n" +
                "物料功能\t附件装饰\n" +
                "物料功能\t包装装饰\n" +
                "物料功能\t液体喷雾\n" +
                "物料功能\t液体挥发\n" +
                "物料功能\t液体发泡\n" +
                "物料功能\t油体定量\n" +
                "物料功能\t油体涂抹\n" +
                "物料功能\t持续燃烧\n" +
                "物料功能\t固体升华\n" +
                "物料功能\t胶体蒸发\n" +
                "物料功能\t溶剂\n" +
                "物料功能\t皮肤调理剂\n" +
                "物料功能\t表面活性剂\n" +
                "物料功能\t增稠剂\n" +
                "物料功能\t螯合剂\n" +
                "物料功能\t防腐剂\n" +
                "物料功能\t芳香剂\n" +
                "物料功能\tpH调节剂\n" +
                "物料功能\t着色剂\n" +
                "物料功能\t乳化剂\n" +
                "物料功能\t发用调理剂\n" +
                "物料功能\t保湿剂\n" +
                "物料功能实现方式\t单体\n" +
                "物料功能实现方式\t混合物\n" +
                "物料功能实现方式\t香水泵\n" +
                "物料功能实现方式\t压力阀\n" +
                "物料功能实现方式\t藤条\n" +
                "物料功能实现方式\t挥发石\n" +
                "物料功能实现方式\t发泡泵\n" +
                "物料功能实现方式\t黏稠流体泵\n" +
                "物料功能实现方式\t油珠\n" +
                "物料功能实现方式\t蜡烛燃芯\n" +
                "物料功能实现方式\t基膜\n" +
                "物料功能实现方式\t电热\n" +
                "物料功能实现方式\t加湿器添加剂\n" +
                "物料功能实现方式\t内衬\n" +
                "物料功能实现方式\t挂饰\n" +
                "物料功能实现方式\t套饰\n" +
                "物料功能实现方式\t卡紧封盖\n" +
                "物料功能实现方式\t旋紧封盖\n" +
                "物料功能实现方式\t中套&下座\n" +
                "物料功能实现方式\t弹性密合\n" +
                "物料功能实现方式\t接触密合\n" +
                "物料功能实现方式\t瓶装\n" +
                "物料功能实现方式\t桶装\n" +
                "物料功能实现方式\t盒装\n" +
                "物料功能实现方式\t纸箱\n" +
                "物料功能实现方式\t筒装\n" +
                "物料功能实现方式\t袋装\n" +
                "物料材质\t卡板纸\n" +
                "物料材质\t锡铁\n" +
                "物料材质\t木材\n" +
                "物料材质\t塑料\n" +
                "物料材质\t铝膜\n" +
                "物料材质\t坑纸\n" +
                "物料材质\t牛皮纸\n" +
                "物料材质\t铜板纸\n" +
                "物料材质\t锡卡纸\n" +
                "物料材质\t荷兰白卡\n" +
                "物料材质\t特种纸\n" +
                "物料材质\t玻璃\n" +
                "物料材质\t金属\n" +
                "物料材质\t陶瓷\n" +
                "物料材质\t橡胶\n" +
                "物料材质\t软木\n" +
                "物料材质\t硅胶\n" +
                "物料材质\t布料\n" +
                "物料材质\t石膏\n" +
                "物料材质\t矿石\n" +
                "物料材质\t纤维\n" +
                "物料材质\t香料\n" +
                "物料材质\t木炭\n" +
                "颜色\t透明\n" +
                "颜色\t红\n" +
                "颜色\t黄\n" +
                "颜色\t绿\n" +
                "颜色\t青\n" +
                "颜色\t蓝\n" +
                "颜色\t橙\n" +
                "颜色\t紫\n" +
                "颜色\t灰\n" +
                "颜色\t粉\n" +
                "颜色\t黑\n" +
                "颜色\t白\n" +
                "颜色\t棕\n" +
                "颜色\t银色\n" +
                "颜色\t金色\n" +
                "颜色\t亮黑色\n" +
                "颜色\t枪黑\n" +
                "颜色\t哑黑\n" +
                "颜色\t哑金\n" +
                "颜色\t哑银\n" +
                "颜色\t浅金\n" +
                "颜色\t浅金A\n" +
                "颜色\t玫瑰金\n" +
                "颜色\t土黄色\n" +
                "颜色\t原木\n" +
                "包材工艺\t纸折盒\n" +
                "包材工艺\t方形金属盒\n" +
                "包材工艺\t圆形金属罐\n" +
                "包材工艺\t方形木盒\n" +
                "包材工艺\t圆形木盒\n" +
                "包材工艺\t塑料折盒\n" +
                "包材工艺\t塑料模盒\n" +
                "包材工艺\t折叠信封\n" +
                "包材工艺\t透明真空封袋\n" +
                "包材工艺\t覆铝真空封袋\n" +
                "包材工艺\t精装白卡盒\n" +
                "包材工艺\t圆筒裱纸\n" +
                "包材工艺\t内卷边\n" +
                "包材工艺\t外卷边\n" +
                "包材工艺\t铰链\n" +
                "包材工艺\t拉丝\n" +
                "包材工艺\t刨平\n" +
                "包材工艺\t亮面\n" +
                "包材工艺\t哑面\n" +
                "包材工艺\t半透\n" +
                "包材工艺\t实色\n" +
                "容器工艺\t螺口\n" +
                "容器工艺\t广口\n" +
                "容器工艺\t卡口\n" +
                "容器工艺\t喷涂-单色\n" +
                "容器工艺\t喷涂-双色\n" +
                "容器工艺\t喷涂-橡胶漆\n" +
                "容器工艺\t喷涂-闪粉\n" +
                "容器工艺\t喷涂-半透\n" +
                "容器工艺\t喷涂-渐变\n" +
                "容器工艺\t抛光\n" +
                "容器工艺\t火抛\n" +
                "容器工艺\t丝印-文字\n" +
                "容器工艺\t丝印-光油\n" +
                "容器工艺\t丝印-真金银\n" +
                "容器工艺\t烫-金银\n" +
                "容器工艺\t电镀\n" +
                "容器工艺\t幻彩镀\n" +
                "容器工艺\t蒙砂\n" +
                "容器工艺\t贴花-高温\n" +
                "容器工艺\t贴花-低温\n" +
                "容器工艺\t贴皮\n" +
                "容器工艺\tUV打印\n" +
                "容器工艺\t镭雕\n" +
                "容器工艺\t阳极氧化\n" +
                "容器工艺\t喷砂\n" +
                "容器工艺\t拉管\n" +
                "容器形状\t圆形\n" +
                "容器形状\t方形\n" +
                "容器形状\t六边形\n" +
                "容器形状\t三角形\n" +
                "容器形状\t梯形\n" +
                "容器形状\t异形\n" +
                "语言区域\t中国大陆\n" +
                "语言区域\t欧盟\n" +
                "语言区域\t北美\n" +
                "语言区域\t中东\n" +
                "语言区域\t中亚\n" +
                "语言区域\t印巴\n" +
                "语言区域\t东盟\n" +
                "语言区域\t澳洲\n" +
                "语言区域\t俄联邦\n" +
                "语言区域\t日韩\n" +
                "语言区域\t非盟\n" +
                "语言区域\t拉美";

        final String[] rows = products.split("\n");
        final String[] rows1 = materials.split("\n");
        final List<String> all = CollUtil.unionAll(Arrays.stream(rows).toList(), Arrays.stream(rows1).toList());
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


    void masterData() {
        for (final MasterDataType type : MasterDataType.values()) {
            this.masterDataController.dropEntity(type.getName());
            this.masterDataController.createEntity(type.basicModel());
        }
    }

    //    @Test
    void dropEntity() {
        this.masterDataController.dropEntity("产品");
    }
}