package com.eimos.polaris.controller;

import com.eimos.polaris.enums.MasterDataType;
import com.eimos.polaris.service.MasterDataService;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import com.google.common.base.Preconditions;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lipengpeng
 */
@RequestMapping("/md")
@RestController
public class MasterDataController {
    private final MasterDataService service;

    public MasterDataController(final MasterDataService service) {
        this.service = service;
    }

    /**
     * 0. 创建主数据基本表，内部使用，不提供对外接口
     */
    public void createEntity(final MasterDataEntityVo entity) {
        this.service.createEntity(entity);
    }

    public void dropEntity(final String entityName) {
        this.service.dropEntity(entityName);
    }

    /**
     * 2. 创建新的属性
     *
     * @param attribute 属性
     */
    @PostMapping("/entity/{entityName}/attribute")
    public void create(@PathVariable final String entityName, @RequestBody final AttributeVo attribute) {
        this.service.createAttribute(entityName, attribute);
    }

    /**
     * 3. 修改属性
     *
     * @param attribute 属性
     */
    @PutMapping("/entity/{entityName}/attribute")
    public void alter(@PathVariable final String entityName, @RequestBody final AttributeVo attribute) {
        this.service.alterAttribute(entityName, attribute);
    }

    /**
     * 4. 删除没有使用的属性，系统自带的不能删除
     *
     * @param entityName    实体编码
     * @param attributeName 属性名称
     */
    @DeleteMapping("/entity/{entityName}/attribute/{attributeName}")
    public void drop(@PathVariable final String entityName,
                     @PathVariable final String attributeName,
                     @RequestParam(required = false, defaultValue = "false") final boolean force) {
        this.service.dropAttribute(entityName, attributeName, force);
    }


    /**
     * 5. 某一主数据实体 的 所有数据
     *
     * @param masterData 实体编码
     * @return 数据
     */
    @GetMapping("/{masterData}")
    public List<Map<String, Object>> list(@PathVariable final String masterData,
                                          @RequestParam(required = false, defaultValue = "") final String queryKey,
                                          @RequestParam(required = false, defaultValue = "10000") final int pageSize,
                                          @RequestParam(required = false, defaultValue = "1") final int pageIndex) {
        Preconditions.checkArgument(pageSize > 0, "pageSize must > 0");
        Preconditions.checkArgument(pageIndex > 0, "pageIndex must > 0");

        return this.service.list(MasterDataType.of(masterData), queryKey, pageIndex, pageSize);
    }

    /**
     * 6. 新增 某一主数据实体的 一行数据
     *
     * @param data 数据
     */
    @PostMapping("/{masterData}")
    public void add(@PathVariable final String masterData,
                    @RequestBody final Map<String, Object> data) {
        this.service.add(MasterDataType.of(masterData), data);
    }

    /**
     * 7. 修改 某一主数据实体的 一行数据
     *
     * @param data 数据
     */
    @PutMapping("/{masterData}")
    public void modify(@PathVariable final String masterData,
                       @RequestBody final Map<String, Object> data) {
        this.service.modify(MasterDataType.of(masterData), data);
    }

    /**
     * 8. 删除 某一主数据实体的 一行数据
     *
     * @param masterData 主数据
     */
    @DeleteMapping("/{masterData}/{id}")
    public void delete(@PathVariable final String masterData,
                       @PathVariable final long id) {
        this.service.delete(MasterDataType.of(masterData), id);
    }
}
