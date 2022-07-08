package com.eimos.polaris.domain;

import cn.hutool.json.JSONUtil;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lipengpeng
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ManufactureSpecValues {
    /**
     * Key： 性能特征
     * Value： 基础数据编码
     */
    private NavigableMap<String, String> map;

    public ManufactureSpecValues(final List<SpecVal> list) {
        this.map = list.stream()
                .collect(Collectors.toMap(SpecVal::getSpec,
                        SpecVal::getCode,
                        (v1, v2) -> v2,
                        TreeMap::new));
    }

    public static ManufactureSpecValues decode(final String str) {
        final List<SpecVal> list = JSONUtil.toList(str, SpecVal.class);
        final TreeMap<String, String> map = list.stream()
                .collect(Collectors.toMap(SpecVal::getSpec,
                        SpecVal::getCode,
                        (v1, v2) -> v2,
                        TreeMap::new));
        return new ManufactureSpecValues(map);
    }

    public String encode() {
        final List<SpecVal> list = this.map.entrySet().stream()
                .map(e -> new SpecVal(e.getKey(), e.getValue()))
                .toList();
        return JSONUtil.toJsonStr(list);
    }

    public ManufactureSpecValues trans(final NavigableSet<String> specs) {
        final TreeMap<String, String> m = specs.stream().collect(Collectors.toMap(Function.identity(),
                s -> this.map.getOrDefault(s, ""),
                (v1, v2) -> v2,
                TreeMap::new));
        return new ManufactureSpecValues(m);
    }

    public String genCode() {
        return String.join("_", this.map.values());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(of = "spec")
    public static class SpecVal {
        /**
         * 性能特征
         */
        private String spec;
        /**
         * 基础数据编码
         */
        private String code;
    }
}
