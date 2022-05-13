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
    @PostMapping("/entity")
    public void create(@RequestBody final EntityVo entity) {
        this.service.create(entity);
    }

    /**
     * 3. 删除没有使用的实体
     *
     * @param code 实体编码
     */
    @DeleteMapping("/entity/{code}")
    public void drop(@PathVariable final String code,
                     @RequestParam(required = false, defaultValue = "false") final boolean force) {
        this.service.drop(code, force);
    }

    /**
     * 4. 某一基础数据实体 的 所有数据
     *
     * @param entityCode 实体编码
     * @return 数据
     */
    @GetMapping("/entity/{entityCode}")
    public List<BasicDataVo> list(@PathVariable final String entityCode,
                                  @RequestParam(required = false, defaultValue = "") final String queryKey,
                                  @RequestParam(required = false, defaultValue = "10000") final int pageSize,
                                  @RequestParam(required = false, defaultValue = "1") final int pageIndex) {
        Preconditions.checkArgument(pageSize > 0, "pageSize must > 0");
        Preconditions.checkArgument(pageIndex > 0, "pageIndex must > 0");
        
        return this.service.list(entityCode, queryKey, pageIndex, pageSize);
    }

    /**
     * 5. 新增 某一基础数据实体的 一行数据
     *
     * @param basicData 数据
     */
    @PostMapping("/entity/{entityCode}")
    public void add(@PathVariable final String entityCode,
                    @RequestBody final BasicDataVo basicData) {
        this.service.add(entityCode, basicData);
    }

    /**
     * 6. 修改 某一基础数据实体的 一行数据
     *
     * @param basicData 数据
     */
    @PutMapping("/entity/{entityCode}")
    public void modify(@PathVariable final String entityCode,
                       @RequestBody final BasicDataVo basicData) {
        this.service.modify(entityCode, basicData);
    }

    /**
     * 7. 删除 某一基础数据实体的 一行数据
     *
     * @param code 数据编码
     */
    @DeleteMapping("/entity/{entityCode}/{code}")
    public void delete(@PathVariable final String entityCode,
                       @PathVariable final String code) {
        this.service.delete(entityCode, code);
    }
}
