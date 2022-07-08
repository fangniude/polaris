package com.eimos.polaris.controller;

import com.eimos.polaris.domain.ManufactureClassification;
import com.eimos.polaris.service.ManufactureClassificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

/**
 * @author lipengpeng
 */
@RequestMapping("/manufacture_classification")
@RestController
public class ManufactureClassificationController {
    private static final Set<String> NAMES = ManufactureClassificationService.NAMES.values();

    private final ManufactureClassificationService service;

    public ManufactureClassificationController(final ManufactureClassificationService service) {
        this.service = service;
    }

    /**
     * 1. 查询某一分类实体所有分类
     *
     * @param entityName 实体名称
     * @return 数据
     */
    @GetMapping("/{entityName}")
    public List<ManufactureClassification> list(@PathVariable final String entityName) {
        this.check(entityName);
        return this.service.list(entityName);
    }

    /**
     * 2. 查询某一分类实体 的一个分类
     *
     * @param entityName 实体名称
     * @return 数据
     */
    @GetMapping("/{entityName}/{code}")
    public ManufactureClassification fetch(@PathVariable final String entityName,
                                           @PathVariable final String code) {
        this.check(entityName);
        return this.service.fetch(entityName, code);
    }

    /**
     * 3. 新增 某一分类
     *
     * @param entityName     实体名称
     * @param classification 分类
     */
    @PostMapping("/{entityName}")
    public void add(@PathVariable final String entityName,
                    @RequestBody final ManufactureClassification classification) {
        this.check(entityName);
        this.service.add(entityName, classification);
    }

    /**
     * 4. 删除 某一分类
     *
     * @param entityName 实体名称
     * @param code       分类编码
     */
    @DeleteMapping("/{entityName}/{code}")
    public void delete(@PathVariable final String entityName,
                       @PathVariable final String code) {
        this.check(entityName);
        this.service.delete(entityName, code);
    }

    /**
     * 5. 新增 某一分类的 一个性能特征
     *
     * @param entityName 实体名称
     * @param code       分类编码
     * @param spec       性能特征
     */
    @PostMapping("/{entityName}/{code}")
    public void add(@PathVariable final String entityName,
                    @PathVariable final String code,
                    @RequestBody final String spec) {
        this.check(entityName);
        this.service.add(entityName, code, spec);
    }

    /**
     * 6. 删除 某一分类的 一个性能特征
     *
     * @param entityName 实体名称
     * @param code       分类编码
     * @param spec       性能特征
     */
    @DeleteMapping("/{entityName}/{code}/{spec}")
    public void delete(@PathVariable final String entityName,
                       @PathVariable final String code,
                       @PathVariable final String spec) {
        this.check(entityName);
        this.service.delete(entityName, code, spec);
    }

    private void check(final String entityName) {
        if (!ManufactureClassificationController.NAMES.contains(entityName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("不存在该分类：%s", entityName));
        }
    }
}
