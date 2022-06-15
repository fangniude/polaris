package com.eimos.polaris.controller;

import com.eimos.polaris.enums.MasterDataType;
import com.eimos.polaris.service.MasterDataService;
import com.eimos.polaris.vo.AttributeVo;
import com.eimos.polaris.vo.MasterDataEntityVo;
import com.google.common.base.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * 0. 删除实体
     *
     * @param masterData 实体名称
     */
    public void dropEntity(final String masterData) {
        this.service.dropEntity(MasterDataType.of(masterData));
    }

    /**
     * 1. 查询主数据实体
     *
     * @return 实体，含外键
     */
    @GetMapping("/entities/{masterData}")
    public MasterDataEntityVo fetch(@PathVariable final String masterData) {
        return this.service.fetchEntity(MasterDataType.of(masterData));
    }

    /**
     * 2. 创建新的属性
     *
     * @param attribute 属性
     */
    @PostMapping("/entities/{masterData}/attribute")
    public void create(@PathVariable final String masterData, @RequestBody final AttributeVo attribute) {
        this.service.createAttribute(MasterDataType.of(masterData), attribute);
    }

    /**
     * 3. 修改属性
     *
     * @param attribute 属性
     */
    @PutMapping("/entities/{masterData}/attribute")
    public void alter(@PathVariable final String masterData, @RequestBody final AttributeVo attribute) {
        this.service.alterAttribute(MasterDataType.of(masterData), attribute);
    }

    /**
     * 4. 删除没有使用的属性，系统自带的不能删除
     *
     * @param masterData    实体编码
     * @param attributeName 属性名称
     */
    @DeleteMapping("/entities/{masterData}/attribute/{attributeName}")
    public void drop(@PathVariable final String masterData,
                     @PathVariable final String attributeName,
                     @RequestParam(required = false, defaultValue = "false") final boolean force) {
        this.service.dropAttribute(MasterDataType.of(masterData), attributeName, force);
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
     * @return id
     */
    @PostMapping("/{masterData}")
    public long add(@PathVariable final String masterData,
                    @RequestBody final Map<String, Object> data) {
        final MasterDataType md = MasterDataType.of(masterData);

        for (final AttributeVo attr : md.requiredAttributes()) {
            this.requireNonNull(data, attr.getName());
        }

        return this.service.add(md, data);
    }

    private void requireNonNull(final Map<String, Object> data, final String attrName) {
        if (data.get(attrName) == null) {
            throw new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED, "【%s】不能为空".formatted(attrName));
        }
    }

    /**
     * 7. 修改 某一主数据实体的 一行数据
     *
     * @param data 数据
     */
    @PutMapping("/{masterData}")
    public void modify(@PathVariable final String masterData,
                       @RequestBody final Map<String, Object> data) {
        final MasterDataType md = MasterDataType.of(masterData);

        for (final AttributeVo attr : md.requiredAttributesWithId()) {
            this.requireNonNull(data, attr.getName());
        }
        this.service.modify(md, data);
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

    /**
     * 9. 获取 某一主数据实体的 一行数据
     *
     * @param masterData 主数据
     * @return 行数据
     */
    @GetMapping("/{masterData}/{id}")
    public Map<String, Object> fetch(@PathVariable final String masterData,
                                     @PathVariable final long id) {
        final Map<String, Object> map = this.service.fetch(MasterDataType.of(masterData), id);
        if (map == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "ID Not Found");
        } else {
            return map;
        }
    }
}
