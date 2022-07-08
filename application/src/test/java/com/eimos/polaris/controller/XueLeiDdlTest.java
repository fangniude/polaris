package com.eimos.polaris.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.eimos.polaris.enums.DataType;
import com.eimos.polaris.enums.IndexType;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.service.BasicDataService;
import com.eimos.polaris.service.MasterDataService;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.EntityVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lipengpeng
 */
@SpringBootTest(properties = "spring.profiles.active:pg")
class XueLeiDdlTest {
    public static final String DATA = "供应商法人,供应商法人编码,,,唯一索引,,否,\n" +
            "供应商法人,供应商法人名称,,,,,,\n" +
            "供应商法人,供应商法人简称,,,,,,\n" +
            "供应商法人,供应商组织编码,,,有序索引,供应商组织编码,否,\n" +
            "供应商法人,法人类型编码,,是,,,,\n" +
            "供应商法人,司法范围编码,,是,,,,\n" +
            "供应商法人,地址信息,,,,,,\n" +
            "供应商法人,开票信息,,,,,,\n" +
            "供应商法人,交易账户信息,,,,,,\n" +
            "供应商法人,源系统编码,,,有序索引,,否,\n" +
            "供应商物料,供应商组织编码,,,有序索引,供应商组织编码,否,\n" +
            "供应商物料,物料编码,,,有序索引,物料编码,否,\n" +
            "供应商物料,供应商类型编码,,是,,,,\n" +
            "供应商物料,供应商评级编码,,是,,,,\n" +
            "供应商物料,单批供货能力,,,,,,\n" +
            "供应商物料,供货周期编码,,是,,,,\n" +
            "供应商物料,起订量,,,,,,\n" +
            "供应商组织,供应商组织编码,,,唯一索引,,否,\n" +
            "供应商组织,供应商组织名称,,,,,,\n" +
            "供应商组织,供应商组织简称,,,,,,\n" +
            "供应商组织,源系统编码,,,有序索引,,否,\n" +
            "供应商组织行业特征,供应商组织编码,,,,供应商组织编码,否,\n" +
            "供应商组织行业特征,行业序号,整数,,,,否,\n" +
            "供应商组织行业特征,行业编码,,是,,,否,\n" +
            "供应商组织行业特征,供应商组织类型编码,,是,,,,\n" +
            "供应商组织行业特征,当前供应商管理标签编码,,是,,,,供应商管理标签\n" +
            "供应商组织行业特征,预期供应商管理标签编码,,是,,,,供应商管理标签\n" +
            "供应商自然人,供应商自然人编码,,,唯一索引,,否,\n" +
            "供应商自然人,姓名,,,,,,\n" +
            "供应商自然人,签章,,,,,,\n" +
            "供应商自然人,联系方式,,,,,,\n" +
            "供应商自然人,地址信息,,,,,,\n" +
            "供应商自然人,开票信息,,,,,,\n" +
            "供应商自然人,交易账户信息,,,,,,\n" +
            "供应商自然人,供应商组织编码,,,有序索引,供应商组织编码,,\n" +
            "供应商自然人,源系统编码,,,有序索引,,否,\n" +
            "制造产品,制造产品编码,,,唯一索引,,否,\n" +
            "制造产品,制造产品名称,,,,,,\n" +
            "制造产品,制造产品图号,,,,,,\n" +
            "制造产品,产品用途场景编码,,是,,,否,\n" +
            "制造产品,产品功能编码,,是,,,否,\n" +
            "制造产品,产品功能实现方式编码,,是,,,否,\n" +
            "制造产品,制造产品生命周期编码,,是,,,,\n" +
            "制造产品,性能特征集,长文本,,,,,\n" +
            "制造产品,源系统编码,,,有序索引,,否,\n" +
            "制造产品BOM,制造产品BOM编码,,,唯一索引,,否,\n" +
            "制造产品BOM,制造产品BOM名称,,,,,,\n" +
            "制造产品BOM,制造产品工艺路线编码,,,有序索引,制造产品工艺路线编码,否,\n" +
            "制造产品BOM,源系统编码,,,有序索引,,否,\n" +
            "制造产品BOM行,制造产品BOM编码,,,有序索引,制造产品BOM编码,否,\n" +
            "制造产品BOM行,物料编码,,,有序索引,物料编码,否,\n" +
            "制造产品BOM行,数量,小数,,,,否,\n" +
            "制造产品与物料关联,制造产品编码,,,唯一索引,制造产品编码,否,\n" +
            "制造产品与物料关联,物料编码,,,有序索引,物料编码,否,\n" +
            "制造产品工艺路线,制造产品工艺路线编码,,,唯一索引,,否,\n" +
            "制造产品工艺路线,制造产品工艺路线名称,,,,,,\n" +
            "制造产品工艺路线,制造产品编码,,,有序索引,制造产品编码,,\n" +
            "制造产品工艺路线,源系统编码,,,有序索引,,否,\n" +
            "客户法人,客户法人编码,,,唯一索引,,否,\n" +
            "客户法人,客户法人名称,,,,,,\n" +
            "客户法人,客户法人简称,,,,,,\n" +
            "客户法人,客户组织编码,,,有序索引,客户组织编码,否,\n" +
            "客户法人,法人类型编码,,是,,,,\n" +
            "客户法人,司法范围编码,,是,,,,\n" +
            "客户法人,地址信息,,,,,,\n" +
            "客户法人,开票信息,,,,,,\n" +
            "客户法人,交易账户信息,,,,,,\n" +
            "客户法人,源系统编码,,,有序索引,,否,\n" +
            "客户组织,客户组织编码,,,唯一索引,,否,\n" +
            "客户组织,客户组织名称,,,,,,\n" +
            "客户组织,客户组织简称,,,,,,\n" +
            "客户组织,源系统编码,,,有序索引,,否,\n" +
            "客户组织行业特征,客户组织编码,,,有序索引,客户组织编码,否,\n" +
            "客户组织行业特征,行业序号,,,,,否,\n" +
            "客户组织行业特征,行业编码,,是,,,否,\n" +
            "客户组织行业特征,主要销售场景编码,,是,,,,销售场景\n" +
            "客户组织行业特征,销售场景集合,长文本,,,,,\n" +
            "客户组织行业特征,客户组织类型编码,,是,,,,\n" +
            "客户组织行业特征,当前客户价值标签编码,,是,,,,客户价值标签\n" +
            "客户组织行业特征,预期客户价值标签编码,,是,,,,客户价值标签\n" +
            "客户自然人,客户自然人编码,,,唯一索引,,否,\n" +
            "客户自然人,姓名,,,,,,\n" +
            "客户自然人,签章,,,,,,\n" +
            "客户自然人,联系方式,,,,,,\n" +
            "客户自然人,地址信息,,,,,,\n" +
            "客户自然人,开票信息,,,,,,\n" +
            "客户自然人,交易账户信息,,,,,,\n" +
            "客户自然人,客户组织编码,,,有序索引,客户组织编码,否,\n" +
            "客户自然人,源系统编码,,,有序索引,,否,\n" +
            "物料,物料编码,短文本,,唯一索引,,否,\n" +
            "物料,物料名称,短文本,,,,,\n" +
            "物料,物料用途场景编码,短文本,是,,,否,\n" +
            "物料,物料功能编码,短文本,是,,,否,\n" +
            "物料,物料功能实现方式编码,短文本,是,,,否,\n" +
            "物料,物料性能特征集,长文本,,,,,\n" +
            "物料,源系统编码,短文本,,有序索引,,否,\n" +
            "物料BOM,物料BOM编码,,,唯一索引,,否,\n" +
            "物料BOM,物料BOM名称,,,,,,\n" +
            "物料BOM,物料工艺路线编码,,,有序索引,物料工艺路线编码,否,\n" +
            "物料BOM,源系统编码,,,有序索引,,否,\n" +
            "物料BOM行,物料BOM编码,,,唯一索引,物料BOM编码,否,\n" +
            "物料BOM行,子项物料编码,,,有序索引,物料编码,否,\n" +
            "物料BOM行,子项物料数量,小数,,,,否,\n" +
            "物料工艺路线,物料工艺路线编码,,,唯一索引,,否,\n" +
            "物料工艺路线,物料工艺路线名称,,,,,,\n" +
            "物料工艺路线,物料编码,,,有序索引,物料编码,否,\n" +
            "物料工艺路线,源系统编码,,,有序索引,,否,\n" +
            "科目,科目编码,,,唯一索引,,否,\n" +
            "科目,科目名称,,,,,,\n" +
            "科目,科目类型编码,,是,,,,\n" +
            "科目,源数据编码,,,有序索引,,否,\n" +
            "经营组织职能场景,经营组织编码,,,有序索引,经营组织编码,否,\n" +
            "经营组织职能场景,职能场景列表,长文本,,,,,\n" +
            "经营组织职能场景,职能场景匹配规则,长文本,,,,,\n" +
            "经营组织,经营组织编码,,,唯一索引,,否,\n" +
            "经营组织,经营组织名称,,,,,,\n" +
            "经营组织,经营组织简称,,,,,,\n" +
            "经营组织,源系统编码,,,有序索引,,否,\n" +
            "经营组织法人,经营组织法人编码,,,唯一索引,,否,\n" +
            "经营组织法人,经营组织法人名称,,,,,,\n" +
            "经营组织法人,经营组织法人简称,,,,,,\n" +
            "经营组织法人,经营组织编码,,,有序索引,经营组织编码,否,\n" +
            "经营组织法人,法人类型编码,,是,,,,\n" +
            "经营组织法人,司法范围编码,,是,,,,\n" +
            "经营组织法人,地址信息,,,,,,\n" +
            "经营组织法人,开票信息,,,,,,\n" +
            "经营组织法人,交易账户信息,,,,,,\n" +
            "经营组织法人,源系统编码,,,有序索引,,否,\n" +
            "经营组织自然人,经营组织自然人编码,,,唯一索引,,否,\n" +
            "经营组织自然人,姓名,,,,,,\n" +
            "经营组织自然人,签章,,,,,,\n" +
            "经营组织自然人,联系方式,,,,,,\n" +
            "经营组织自然人,地址信息,,,,,,\n" +
            "经营组织自然人,开票信息,,,,,,\n" +
            "经营组织自然人,交易账户信息,,,,,,\n" +
            "经营组织自然人,经营组织编码,,,有序索引,经营组织编码,否,\n" +
            "经营组织自然人,源系统编码,,,有序索引,,否,\n" +
            "经营组织行业特征,经营组织编码,,,唯一索引,经营组织编码,否,\n" +
            "经营组织行业特征,行业编码,,是,,,,\n" +
            "经营组织行业特征,主要销售场景编码,,是,,,,销售场景\n" +
            "经营组织行业特征,经营组织类型编码,,是,,,,\n" +
            "经营组织行业特征,当前组织价值标签编码,,是,,,,组织价值标签\n" +
            "经营组织行业特征,预期组织价值标签编码,,是,,,,组织价值标签\n" +
            "销售产品,销售产品编码,,,唯一索引,,否,\n" +
            "销售产品,销售产品名称,,,,,,\n" +
            "销售产品,销售产品特征集,长文本,,,,,\n" +
            "销售产品,产品价值分类编码,,是,,,,\n" +
            "销售产品,销售产品生命周期编码,,是,,,,\n" +
            "销售产品,源系统编码,,,有序索引,,否,\n" +
            "销售产品子项,销售产品编码,,,有序索引,销售产品编码,否,\n" +
            "销售产品子项,子产品编码,,,有序索引,制造产品编码,否,\n" +
            "销售产品子项,子产品数量,整数,,,,否,";
    public static final List<String> MASTER_DATA_NAMES = List.of("物料", "制造产品", "制造产品与物料关联", "物料工艺路线", "物料BOM",
            "物料BOM行", "制造产品工艺路线", "制造产品BOM", "制造产品BOM行", "销售产品",
            "销售产品子项", "供应商组织", "供应商组织行业特征", "供应商物料", "供应商法人",
            "供应商自然人", "客户组织", "客户组织行业特征", "客户法人", "客户自然人",
            "经营组织", "经营组织行业特征", "经营组织法人", "经营组织自然人", "科目",
            "经营组织职能场景");
    private final BasicDataService basicDataService;
    private final MasterDataService masterDataService;

    @Autowired
    XueLeiDdlTest(final BasicDataService basicDataService, final MasterDataService masterDataService) {
        this.basicDataService = basicDataService;
        this.masterDataService = masterDataService;
    }

    @Test
    public void test() {
        this.dropAllBasicData();
        this.dropAllMasterData();

        final List<List<String>> data = Arrays.stream(XueLeiDdlTest.DATA.split("\n"))
                .map(s -> Arrays.stream(s.split(",")).toList())
                .map(l -> {
                    if (l.size() < 8) {
                        final ArrayList<String> r = new ArrayList<>(l);
                        while (r.size() < 8) {
                            r.add("");
                        }
                        return r;
                    } else {
                        return l;
                    }
                })
                .toList();

        final Map<String, Map<String, List<String>>> mapMap = data.stream()
                .collect(Collectors.groupingBy(row -> row.get(0),
                        Collectors.toMap(row -> row.get(1), Function.identity(), (d1, d2) -> d2, LinkedHashMap::new)));

        final Map<String, EntityVo> bdMap = new HashMap<>();
        final Map<String, MasterDataEntityVo> mdMap = new HashMap<>();
        // 主数据先创建出来
        for (final Map.Entry<String, Map<String, List<String>>> entry : mapMap.entrySet()) {
            final String mdTable = entry.getKey();
            mdMap.put(mdTable, new MasterDataEntityVo(IdUtil.getSnowflakeNextId(), Namespace.MD, mdTable, mdTable, new ArrayList<>()));
        }

        // 分析列
        for (final Map.Entry<String, Map<String, List<String>>> entry : mapMap.entrySet()) {
            final String mdTable = entry.getKey();
            final MasterDataEntityVo md = mdMap.get(mdTable);
            final List<AttributeVo> attrs = md.getAttributes();
            attrs.add(AttributeVo.id());
            for (final Map.Entry<String, List<String>> e : entry.getValue().entrySet()) {
                final String mdColumn = e.getKey();
                final List<String> row = e.getValue();
                final String type = row.get(2);
                final String isBd = row.get(3);
                final String indexType = row.get(4);
                final String refMd = row.get(5);
                final String nullable = row.get(6);
                final String bdName = row.get(7);

                // 主数据的列
                final AttributeVo.Ref ref;
                if (this.isBd(isBd)) {
                    final String mdTableName = StrUtil.isNotEmpty(bdName) ? bdName : mdColumn.substring(0, mdColumn.length() - 2);
                    ref = new AttributeVo.Ref(Namespace.BD, mdTableName, "code");

                    // 要创建的基础数据
                    if (!bdMap.containsKey(mdTableName)) {
                        bdMap.put(mdTableName, new EntityVo(mdTableName, mdTableName));
                    }
                } else if (StrUtil.isNotEmpty(refMd)) {
                    ref = new AttributeVo.Ref(Namespace.MD, refMd.substring(0, refMd.length() - 2), refMd);
                } else {
                    ref = null;
                }
                attrs.add(new AttributeVo(mdColumn, mdColumn, this.dataType(type), this.indexType(indexType),
                        this.nullable(nullable), ref != null, false, ref));
            }
            attrs.add(AttributeVo.createTime());
            attrs.add(AttributeVo.updateTime());
        }

        // 创建基础数据
        for (final EntityVo vo : bdMap.values()) {
            this.basicDataService.create(vo);
        }

        // 创建主数据
        for (final String s : XueLeiDdlTest.MASTER_DATA_NAMES) {
            final MasterDataEntityVo vo = mdMap.get(s);
            this.masterDataService.createEntity(vo);
        }

    }

    private void dropAllBasicData() {
        final List<EntityVo> entityVos = this.basicDataService.entities("", 1, 1000);
        for (final EntityVo entityVo : entityVos) {
            this.basicDataService.drop(entityVo.getName(), true);
        }
    }

    private void dropAllMasterData() {
        for (final String s : XueLeiDdlTest.MASTER_DATA_NAMES) {
            this.masterDataService.dropEntity(s, true);
        }
    }

    private DataType dataType(final String type) {
        return switch (type) {
            case "长文本" -> DataType.LONG_TEXT;
            case "整数" -> DataType.INTEGER;
            case "小数" -> DataType.DECIMAL;
            case "日期" -> DataType.DATE;
            case "时间" -> DataType.DATE_TIME;
            default -> DataType.SHORT_TEXT;
        };
    }

    private IndexType indexType(final String indexType) {
        return switch (indexType) {
            case "唯一索引" -> IndexType.UNIQUE;
            case "有序索引" -> IndexType.NAVIGABLE;
            default -> IndexType.NONE;
        };
    }

    private Boolean nullable(final String nullable) {
        return switch (nullable) {
            case "否" -> false;
            default -> true;
        };
    }

    private boolean isBd(final String isBd) {
        return switch (isBd) {
            case "是" -> true;
            default -> false;
        };
    }
}