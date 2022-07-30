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
    public static final String DATA = "供应商法人,sup_legal,供应商法人编码,sup_legal_code,,,唯一索引,,否,\n" +
            "供应商法人,sup_legal,供应商法人名称,sup_legal_name,,,,,,\n" +
            "供应商法人,sup_legal,供应商法人简称,sup_legal_sname,,,,,,\n" +
            "供应商法人,sup_legal,供应商组织编码,sup_code,,,有序索引,供应商组织编码,否,\n" +
            "供应商法人,sup_legal,法人类型编码,legal_type_code,,是,,,,\n" +
            "供应商法人,sup_legal,司法范围编码,judicial_scope_code,,是,,,,\n" +
            "供应商法人,sup_legal,地址信息,address,,,,,,\n" +
            "供应商法人,sup_legal,开票信息,billing_info,,,,,,\n" +
            "供应商法人,sup_legal,交易账户信息,account,,,,,,\n" +
            "供应商法人,sup_legal,源系统编码,source_code,,,有序索引,,否,\n" +
            "供应商物料,sup_mat,供应商组织编码,sup_code,,,有序索引,供应商组织编码,否,\n" +
            "供应商物料,sup_mat,物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "供应商物料,sup_mat,供应商类型编码,sup_type_code,,是,,,,\n" +
            "供应商物料,sup_mat,供应商评级编码,sup_rating_code,,是,,,,\n" +
            "供应商物料,sup_mat,单批供货能力,supply_ability_code,,,,,,\n" +
            "供应商物料,sup_mat,供货周期编码,supply_cycle_code,,是,,,,\n" +
            "供应商物料,sup_mat,起订量,min_order_qty,,,,,,\n" +
            "供应商组织,supplier,供应商组织编码,sup_code,,,唯一索引,,否,\n" +
            "供应商组织,supplier,供应商组织名称,sup_name,,,,,,\n" +
            "供应商组织,supplier,供应商组织简称,sup_sname,,,,,,\n" +
            "供应商组织,supplier,源系统编码,source_code,,,有序索引,,否,\n" +
            "供应商组织行业特征,sup_industry_spec,供应商组织编码,sup_code,,,,供应商组织编码,否,\n" +
            "供应商组织行业特征,sup_industry_spec,行业序号,industry_seq,整数,,,,否,\n" +
            "供应商组织行业特征,sup_industry_spec,行业编码,industry_code,,是,,,否,\n" +
            "供应商组织行业特征,sup_industry_spec,供应商组织类型编码,sup_type_code,,是,,,,\n" +
            "供应商组织行业特征,sup_industry_spec,当前供应商管理标签编码,cur_sup_label_code,,是,,,,供应商管理标签|sup_label\n" +
            "供应商组织行业特征,sup_industry_spec,预期供应商管理标签编码,exp_sup_label_code,,是,,,,供应商管理标签|sup_label\n" +
            "供应商自然人,sup_natural,供应商自然人编码,sup_natural_code,,,唯一索引,,否,\n" +
            "供应商自然人,sup_natural,姓名,name,,,,,,\n" +
            "供应商自然人,sup_natural,签章,signature,,,,,,\n" +
            "供应商自然人,sup_natural,联系方式,phone,,,,,,\n" +
            "供应商自然人,sup_natural,地址信息,address,,,,,,\n" +
            "供应商自然人,sup_natural,开票信息,billing_info,,,,,,\n" +
            "供应商自然人,sup_natural,交易账户信息,account,,,,,,\n" +
            "供应商自然人,sup_natural,供应商组织编码,sup_code,,,有序索引,供应商组织编码,,\n" +
            "供应商自然人,sup_natural,源系统编码,source_code,,,有序索引,,否,\n" +
            "制造产品,pro_bpart,制造产品编码,pro_bpart_code,,,唯一索引,,否,\n" +
            "制造产品,pro_bpart,制造产品名称,pro_bpart_name,,,,,,\n" +
            "制造产品,pro_bpart,制造产品设计BOM编码,pro_bpart_ebom_code,,,,制造产品设计BOM编码,,\n" +
            "制造产品,pro_bpart,产品大类编码,pro_class_l_code,,是,,,否,\n" +
            "制造产品,pro_bpart,产品中类编码,pro_class_m_code,,是,,,否,\n" +
            "制造产品,pro_bpart,产品小类编码,pro_class_s_code,,是,,,否,\n" +
            "制造产品,pro_bpart,制造产品性能特征组合编码,pro_bpart_specs_code,,,,,,\n" +
            "制造产品,pro_bpart,性能特征集,pro_bpart_specs,长文本,,,,,\n" +
            "制造产品,pro_bpart,制造产品生命周期编码,pro_bpart_lifecycle_code,,是,,,,\n" +
            "制造产品,pro_bpart,源系统编码,source_code,,,有序索引,,否,\n" +
            "制造产品BOM,pro_bpart_mbom,制造产品BOM编码,pro_bpart_mbom_code,,,唯一索引,,否,\n" +
            "制造产品BOM,pro_bpart_mbom,制造产品BOM名称,pro_bpart_mbom_name,,,,,,\n" +
            "制造产品BOM,pro_bpart_mbom,制造产品工艺路线编码,pro_bpart_routing_code,,,有序索引,制造产品工艺路线编码,否,\n" +
            "制造产品BOM,pro_bpart_mbom,源系统编码,source_code,,,有序索引,,否,\n" +
            "制造产品BOM行,pro_bpart_mbom_row,制造产品BOM编码,pro_bpart_mbom_code,,,有序索引,制造产品BOM编码,否,\n" +
            "制造产品BOM行,pro_bpart_mbom_row,物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "制造产品BOM行,pro_bpart_mbom_row,物料数量,mat_qty,小数,,,,否,\n" +
            "制造产品BOM行,pro_bpart_mbom_row,物料单位,mat_units,,,,,,\n" +
            "制造产品与物料关联,pro_bpart_mat,制造产品编码,pro_bpart_code,,,唯一索引,制造产品编码,否,\n" +
            "制造产品与物料关联,pro_bpart_mat,物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "制造产品工艺路线,pro_bpart_routing,制造产品工艺路线编码,pro_bpart_routing_code,,,唯一索引,,否,\n" +
            "制造产品工艺路线,pro_bpart_routing,制造产品工艺路线名称,pro_bpart_routing_name,,,,,,\n" +
            "制造产品工艺路线,pro_bpart_routing,制造产品编码,pro_bpart_code,,,有序索引,制造产品编码,,\n" +
            "制造产品工艺路线,pro_bpart_routing,源系统编码,source_code,,,有序索引,,否,\n" +
            "制造产品设计BOM,pro_bpart_ebom,制造产品设计BOM编码,pro_bpart_ebom_code,,,唯一索引,,否,\n" +
            "制造产品设计BOM,pro_bpart_ebom,制造产品设计BOM名称,pro_bpart_ebom_name,,,,,,\n" +
            "制造产品设计BOM,pro_bpart_ebom,源系统编码,source_code,,,有序索引,,否,\n" +
            "制造产品设计BOM行,pro_bpart_ebom_row,制造产品设计BOM编码,pro_bpart_ebom_code,,,有序索引,制造产品设计BOM编码,否,\n" +
            "制造产品设计BOM行,pro_bpart_ebom_row,物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "制造产品设计BOM行,pro_bpart_ebom_row,物料数量,mat_qty,小数,,,,,\n" +
            "制造产品设计BOM行,pro_bpart_ebom_row,物料单位,mat_units,,,,,,\n" +
            "客户法人,cus_legal,客户法人编码,cus_legal_code,,,唯一索引,,否,\n" +
            "客户法人,cus_legal,客户法人名称,cus_legal_name,,,,,,\n" +
            "客户法人,cus_legal,客户法人简称,cus_legal_sname,,,,,,\n" +
            "客户法人,cus_legal,客户组织编码,cus_code,,,有序索引,客户组织编码,否,\n" +
            "客户法人,cus_legal,法人类型编码,legal_type_code,,是,,,,\n" +
            "客户法人,cus_legal,司法范围编码,judicial_scope_code,,是,,,,\n" +
            "客户法人,cus_legal,地址信息,address,,,,,,\n" +
            "客户法人,cus_legal,开票信息,billing_info,,,,,,\n" +
            "客户法人,cus_legal,交易账户信息,account,,,,,,\n" +
            "客户法人,cus_legal,源系统编码,source_code,,,有序索引,,否,\n" +
            "客户组织,customer,客户组织编码,cus_code,,,唯一索引,,否,\n" +
            "客户组织,customer,客户组织名称,cus_name,,,,,,\n" +
            "客户组织,customer,客户组织简称,cus_sname,,,,,,\n" +
            "客户组织,customer,源系统编码,source_code,,,有序索引,,否,\n" +
            "客户组织行业特征,cus_industry_spec,客户组织编码,cus_code,,,有序索引,客户组织编码,否,\n" +
            "客户组织行业特征,cus_industry_spec,行业序号,industry_seq,,,,,否,\n" +
            "客户组织行业特征,cus_industry_spec,行业编码,industry_code,,是,,,否,\n" +
            "客户组织行业特征,cus_industry_spec,主要销售场景编码,pri_sale_scene_code,,是,,,,销售场景|sale_scene\n" +
            "客户组织行业特征,cus_industry_spec,销售场景集合,sale_scenes,长文本,,,,,\n" +
            "客户组织行业特征,cus_industry_spec,客户组织类型编码,cus_type_code,,是,,,,\n" +
            "客户组织行业特征,cus_industry_spec,当前客户价值标签编码,cur_cus_val_code,,是,,,,客户价值标签|cus_val\n" +
            "客户组织行业特征,cus_industry_spec,预期客户价值标签编码,exp_cus_val_code,,是,,,,客户价值标签|cus_val\n" +
            "客户自然人,cus_natural,客户自然人编码,cus_natural_code,,,唯一索引,,否,\n" +
            "客户自然人,cus_natural,姓名,name,,,,,,\n" +
            "客户自然人,cus_natural,签章,signature,,,,,,\n" +
            "客户自然人,cus_natural,联系方式,phone,,,,,,\n" +
            "客户自然人,cus_natural,地址信息,address,,,,,,\n" +
            "客户自然人,cus_natural,开票信息,billing_info,,,,,,\n" +
            "客户自然人,cus_natural,交易账户信息,account,,,,,,\n" +
            "客户自然人,cus_natural,客户组织编码,cus_code,,,有序索引,客户组织编码,否,\n" +
            "客户自然人,cus_natural,源系统编码,source_code,,,有序索引,,否,\n" +
            "物料,material,物料编码,mat_code,短文本,,唯一索引,,否,\n" +
            "物料,material,物料名称,mat_name,短文本,,,,,\n" +
            "物料,material,物料大类编码,mat_class_l_code,短文本,是,,,否,\n" +
            "物料,material,物料中类编码,mat_class_m_code,短文本,是,,,否,\n" +
            "物料,material,物料小类编码,mat_class_s_code,短文本,是,,,否,\n" +
            "物料,material,物料性能特征组合编码,mat_specs_code,短文本,,,,,\n" +
            "物料,material,物料性能特征集,mat_specs,长文本,,,,,\n" +
            "物料,material,源系统编码,source_code,短文本,,有序索引,,否,\n" +
            "物料BOM,mat_mbom,物料BOM编码,mat_mbom_code,,,唯一索引,,否,\n" +
            "物料BOM,mat_mbom,物料BOM名称,mat_mbom_name,,,,,,\n" +
            "物料BOM,mat_mbom,物料工艺路线编码,mat_routing_code,,,有序索引,物料工艺路线编码,否,\n" +
            "物料BOM,mat_mbom,源系统编码,source_code,,,有序索引,,否,\n" +
            "物料BOM行,mat_mbom_row,物料BOM编码,mat_mbom_code,,,唯一索引,物料BOM编码,否,\n" +
            "物料BOM行,mat_mbom_row,子项物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "物料BOM行,mat_mbom_row,子项物料数量,mat_qty,小数,,,,否,\n" +
            "物料BOM行,mat_mbom_row,子项物料单位,mat_units,,,,,,\n" +
            "物料工艺路线,mat_routing,物料工艺路线编码,mat_routing_code,,,唯一索引,,否,\n" +
            "物料工艺路线,mat_routing,物料工艺路线名称,mat_routing_name,,,,,,\n" +
            "物料工艺路线,mat_routing,物料编码,mat_code,,,有序索引,物料编码,否,\n" +
            "物料工艺路线,mat_routing,源系统编码,source_code,,,有序索引,,否,\n" +
            "科目,accounting,科目编码,acc_code,,,唯一索引,,否,\n" +
            "科目,accounting,科目名称,acc_name,,,,,,\n" +
            "科目,accounting,科目类型编码,acc_type_code,,是,,,,\n" +
            "科目,accounting,源数据编码,source_code,,,有序索引,,否,\n" +
            "经营组织,organization,经营组织编码,org_code,,,唯一索引,,否,\n" +
            "经营组织,organization,经营组织名称,org_name,,,,,,\n" +
            "经营组织,organization,经营组织简称,org_sname,,,,,,\n" +
            "经营组织,organization,源系统编码,source_code,,,有序索引,,否,\n" +
            "经营组织法人,org_legal,经营组织法人编码,org_legal_code,,,唯一索引,,否,\n" +
            "经营组织法人,org_legal,经营组织法人名称,org_legal_name,,,,,,\n" +
            "经营组织法人,org_legal,经营组织法人简称,org_legal_sname,,,,,,\n" +
            "经营组织法人,org_legal,经营组织编码,org_code,,,有序索引,经营组织编码,否,\n" +
            "经营组织法人,org_legal,法人类型编码,legal_type_code,,是,,,,\n" +
            "经营组织法人,org_legal,司法范围编码,judicial_scope_code,,是,,,,\n" +
            "经营组织法人,org_legal,地址信息,address,,,,,,\n" +
            "经营组织法人,org_legal,开票信息,billing_info,,,,,,\n" +
            "经营组织法人,org_legal,交易账户信息,account,,,,,,\n" +
            "经营组织法人,org_legal,源系统编码,source_code,,,有序索引,,否,\n" +
            "经营组织职能场景,org_functional_scene,经营组织编码,org_code,,,有序索引,经营组织编码,否,\n" +
            "经营组织职能场景,org_functional_scene,职能场景列表,functional_scenes,长文本,,,,,\n" +
            "经营组织职能场景,org_functional_scene,职能场景匹配规则,rules,长文本,,,,,\n" +
            "经营组织自然人,org_natural,经营组织自然人编码,org_natural_code,,,唯一索引,,否,\n" +
            "经营组织自然人,org_natural,姓名,name,,,,,,\n" +
            "经营组织自然人,org_natural,签章,signature,,,,,,\n" +
            "经营组织自然人,org_natural,联系方式,phone,,,,,,\n" +
            "经营组织自然人,org_natural,地址信息,address,,,,,,\n" +
            "经营组织自然人,org_natural,开票信息,billing_info,,,,,,\n" +
            "经营组织自然人,org_natural,交易账户信息,account,,,,,,\n" +
            "经营组织自然人,org_natural,经营组织编码,org_code,,,有序索引,经营组织编码,否,\n" +
            "经营组织自然人,org_natural,源系统编码,source_code,,,有序索引,,否,\n" +
            "经营组织行业特征,org_industry_spec,经营组织编码,org_code,,,唯一索引,经营组织编码,否,\n" +
            "经营组织行业特征,org_industry_spec,行业编码,industry_code,,是,,,,\n" +
            "经营组织行业特征,org_industry_spec,主要销售场景编码,pri_sale_scene_code,,是,,,,销售场景|sale_scene\n" +
            "经营组织行业特征,org_industry_spec,经营组织类型编码,cus_type_code,,是,,,,\n" +
            "经营组织行业特征,org_industry_spec,当前组织价值标签编码,cur_org_val_code,,是,,,,组织价值标签|org_val\n" +
            "经营组织行业特征,org_industry_spec,预期组织价值标签编码,exp_org_val_code,,是,,,,组织价值标签|org_val\n" +
            "销售产品,pro_spart,销售产品编码,pro_spart_code,,,唯一索引,,否,\n" +
            "销售产品,pro_spart,销售产品名称,pro_spart_name,,,,,,\n" +
            "销售产品,pro_spart,销售产品特征集,pro_spart_specs,长文本,,,,,\n" +
            "销售产品,pro_spart,产品价值分类编码,pro_spart_val_code,,是,,,,\n" +
            "销售产品,pro_spart,销售产品生命周期编码,pro_spart_lifecycle_code,,是,,,,\n" +
            "销售产品,pro_spart,源系统编码,source_code,,,有序索引,,否,\n" +
            "销售产品子项,pro_spart_mem,销售产品编码,pro_spart_code,,,有序索引,销售产品编码,否,\n" +
            "销售产品子项,pro_spart_mem,子产品编码,mem_code,,,有序索引,制造产品编码,否,\n" +
            "销售产品子项,pro_spart_mem,子产品数量,mem_qty,整数,,,,否,";
    public static final List<String> MASTER_DATA_NAMES = List.of("物料", "制造产品设计BOM", "制造产品设计BOM行", "制造产品", "制造产品与物料关联", "物料工艺路线", "物料BOM",
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

        final List<List<String>> data = Arrays.stream(XueLeiDdlTest.DATA.split("\n"))
                .map(s -> Arrays.stream(s.split(",")).toList())
                .map(l -> {
                    if (l.size() < 10) {
                        final ArrayList<String> r = new ArrayList<>(l);
                        while (r.size() < 10) {
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
                        Collectors.toMap(row -> row.get(2), Function.identity(), (d1, d2) -> d2, LinkedHashMap::new)));

        final Map<String, EntityVo> bdMap = new HashMap<>();
        final Map<String, MasterDataEntityVo> mdMap = new HashMap<>();
        // 主数据先创建出来
        for (final Map.Entry<String, Map<String, List<String>>> entry : mapMap.entrySet()) {
            final String mdTable = entry.getKey();
            final List<String> list = entry.getValue().values().iterator().next();
            mdMap.put(mdTable, new MasterDataEntityVo(IdUtil.getSnowflakeNextId(), Namespace.MD, list.get(1), mdTable, new ArrayList<>()));
        }

        // 分析列
        for (final Map.Entry<String, Map<String, List<String>>> entry : mapMap.entrySet()) {
            final String mdTable = entry.getKey();
            final MasterDataEntityVo md = mdMap.get(mdTable);
            final List<AttributeVo> attrs = md.getAttributes();
            attrs.add(AttributeVo.id());
            for (final Map.Entry<String, List<String>> e : entry.getValue().entrySet()) {
                final String colComment = e.getKey();
                final List<String> row = e.getValue();
                final String colName = row.get(3);
                final String type = row.get(4);
                final String isBd = row.get(5);
                final String indexType = row.get(6);
                final String refMd = row.get(7);
                final String nullable = row.get(8);
                final String bdName = row.get(9);

                // 主数据的列
                final AttributeVo.Ref ref;
                if (this.isBd(isBd)) {
                    final String mdTableName;
                    final String mdTableComment;
                    if (StrUtil.isNotEmpty(bdName)) {
                        final String[] split = bdName.split("\\|");
                        mdTableComment = split[0];
                        mdTableName = split[1];
                    } else {
                        mdTableComment = colComment.substring(0, colComment.length() - 2);
                        mdTableName = colName.substring(0, colName.length() - 5);
                    }
                    ref = new AttributeVo.Ref(Namespace.BD, mdTableName, "code");

                    // 要创建的基础数据
                    if (!bdMap.containsKey(mdTableName)) {
                        bdMap.put(mdTableName, new EntityVo(mdTableName, mdTableComment));
                    }
//                } else if (StrUtil.isNotEmpty(refMd)) {
//                    final MasterDataEntityVo m = mdMap.get(refMd.substring(0, refMd.length() - 2));
//                    ref = new AttributeVo.Ref(Namespace.MD, m.getName(), m.getName() + "_code");
                } else {
                    ref = null;
                }
                attrs.add(new AttributeVo(colName, colComment, this.dataType(type), this.indexType(indexType),
                        this.nullable(nullable), ref != null, false, ref));
            }
            attrs.add(AttributeVo.createTime());
            attrs.add(AttributeVo.updateTime());
        }

        // 创建基础数据
        for (final EntityVo vo : bdMap.values()) {
            this.basicDataService.create(vo);
        }

        this.dropAllMasterData(mdMap);
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

    private void dropAllMasterData(final Map<String, MasterDataEntityVo> mdMap) {
        for (final String s : XueLeiDdlTest.MASTER_DATA_NAMES) {
            final MasterDataEntityVo vo = mdMap.get(s);
            this.masterDataService.dropEntity(vo.getName(), true);
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