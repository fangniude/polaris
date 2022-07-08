package com.eimos.polaris.domain;

import cn.hutool.core.util.SerializeUtil;
import cn.hutool.json.JSONUtil;
import com.eimos.polaris.vo.BasicDataVo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ManufactureClassification {
    public static final Map<String, String> USAGES = Map.of(
            "物料", "物料用途场景编码",
            "制造产品", "产品用途场景编码"
    );
    public static final Map<String, String> FEATURES = Map.of(
            "物料", "物料功能编码",
            "制造产品", "产品功能编码"
    );
    public static final Map<String, String> IMPLS = Map.of(
            "物料", "物料功能实现方式编码",
            "制造产品", "产品功能实现方式编码"
    );
    public static final Map<String, String> SPECS_MAP = Map.of(
            "物料", "物料性能特征集",
            "制造产品", "性能特征集"
    );
    public static final Map<String, String> SPEC = Map.of(
            "物料", "物料性能特征组合编码",
            "制造产品", "产品性能特征组合编码"
    );
    /**
     * 分类编码
     */
    private String code;
    /**
     * 性能特征集
     */
    private NavigableSet<String> specs;

    public static ManufactureClassification fromBd(final BasicDataVo bd) {
        final List<String> specs = JSONUtil.toList(bd.getName(), String.class);
        return new ManufactureClassification(bd.getCode(), new TreeSet<>(specs));
    }

    public BasicDataVo toBd() {
        return new BasicDataVo(this.code, JSONUtil.toJsonStr(this.specs));
    }

    public void addSpec(final String spec) {
        if (this.specs.contains(spec)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("性能特征[%s]已经存在", spec));
        } else {
            this.specs.add(spec);
        }
    }

    public void deleteSpec(final String spec) {
        if (this.specs.contains(spec)) {
            this.specs.remove(spec);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("性能特征[%s]不存在", spec));
        }
    }

    public Map<String, Object> trans(final String mdEntityName, final Map<String, Object> row) {
        final String key = ManufactureClassification.SPECS_MAP.get(mdEntityName);
        final String specValStr = String.valueOf(row.get(key));

        final Map<String, Object> result = SerializeUtil.clone(row);
        final ManufactureSpecValues trans = ManufactureSpecValues.decode(specValStr).trans(this.specs);
        result.put(key, trans.encode());
        result.put(ManufactureClassification.SPEC.get(mdEntityName), trans.genCode());
        return result;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Classification {
        private String usage;
        private String feature;
        private String impl;

        public static Classification decode(final String code) {
            final String[] split = code.split("_");
            if (split.length == 3) {
                return new Classification(split[0], split[1], split[2]);
            } else {
                throw new IllegalArgumentException(String.format("分类编码[%s]错误", code));
            }
        }

        public String encode() {
            return String.format("%s_%s_%s", this.usage, this.feature, this.impl);
        }

        public Condition condition(final String mdEntityName) {
            return DSL.and(DSL.field(DSL.name(ManufactureClassification.USAGES.get(mdEntityName)))
                            .equal(DSL.value(this.usage)),
                    DSL.field(DSL.name(ManufactureClassification.FEATURES.get(mdEntityName)))
                            .equal(DSL.value(this.feature)),
                    DSL.field(DSL.name(ManufactureClassification.IMPLS.get(mdEntityName)))
                            .equal(DSL.value(this.impl)));
        }
    }
}
