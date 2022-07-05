package com.eimos.polaris.controller;

import com.eimos.polaris.service.BasicDataService;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import com.google.common.base.Preconditions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lipengpeng
 */
@RequestMapping("/bd")
@RestController
public class BasicDataController {
    private final BasicDataService service;

    public BasicDataController(final BasicDataService service) {
        this.service = service;
    }

    /**
     * 1. 所有基础数据表清单
     */
    @GetMapping("/entities")
    public List<EntityVo> entities(@RequestParam(required = false, defaultValue = "") final String queryKey,
                                   @RequestParam(required = false, defaultValue = "10000") final int pageSize,
                                   @RequestParam(required = false, defaultValue = "1") final int pageIndex) {
        Preconditions.checkArgument(pageSize > 0, "pageSize must > 0");
        Preconditions.checkArgument(pageIndex > 0, "pageIndex must > 0");

        return this.service.entities(queryKey, pageIndex, pageSize);
    }

    /**
     * 2. 创建新的实体
     *
     * @param entity 实体
     */
    @PostMapping("/entities")
    public void create(@RequestBody final EntityVo entity) {
        this.service.create(entity);
    }

    /**
     * 3. 删除没有使用的实体
     *
     * @param entityName 实体名称
     */
    @DeleteMapping("/entities/{entityName}")
    public void drop(@PathVariable final String entityName,
                     @RequestParam(required = false, defaultValue = "false") final boolean force) {
        this.service.drop(entityName, force);
    }

    /**
     * 4. 某一基础数据实体 的 所有数据
     *
     * @param entityName 实体名称
     * @return 数据
     */
    @GetMapping("/{entityName}")
    public List<BasicDataVo> list(@PathVariable final String entityName,
                                  @RequestParam(required = false, defaultValue = "") final String queryKey,
                                  @RequestParam(required = false, defaultValue = "10000") final int pageSize,
                                  @RequestParam(required = false, defaultValue = "1") final int pageIndex) {
        Preconditions.checkArgument(pageSize > 0, "pageSize must > 0");
        Preconditions.checkArgument(pageIndex > 0, "pageIndex must > 0");

        return this.service.list(entityName, queryKey, pageIndex, pageSize);
    }

    /**
     * 5. 新增 某一基础数据实体的 一行数据
     *
     * @param basicData 数据
     */
    @PostMapping("/{entityName}")
    public void add(@PathVariable final String entityName,
                    @RequestBody final BasicDataVo basicData) {
        this.service.add(entityName, basicData);
    }

    /**
     * 6. 修改 某一基础数据实体的 一行数据
     *
     * @param basicData 数据
     */
    @PutMapping("/{entityName}")
    public void modify(@PathVariable final String entityName,
                       @RequestBody final BasicDataVo basicData) {
        this.service.modify(entityName, basicData);
    }

    /**
     * 7. 删除 某一基础数据实体的 一行数据
     *
     * @param code 数据编码
     */
    @DeleteMapping("/{entityName}/{code}")
    public void delete(@PathVariable final String entityName,
                       @PathVariable final String code) {
        this.service.delete(entityName, code);
    }
}
