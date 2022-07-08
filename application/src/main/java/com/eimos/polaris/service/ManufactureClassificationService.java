package com.eimos.polaris.service;

import com.eimos.polaris.domain.Entity;
import com.eimos.polaris.domain.ManufactureClassification;
import com.eimos.polaris.enums.Namespace;
import com.eimos.polaris.vo.BasicDataVo;
import com.eimos.polaris.vo.EntityVo;
import com.google.common.collect.ImmutableBiMap;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lipengpeng
 */
@Service
public class ManufactureClassificationService {
    public static final ImmutableBiMap<String, String> NAMES = ImmutableBiMap.of("制造产品", "制造产品分类",
            "物料", "物料分类");


    private final MetadataService metadataService;
    private final BasicDataService basicDataService;
    private final MasterDataService masterDataService;
    private final DSLContext dslContext;

    public ManufactureClassificationService(final MetadataService metadataService, final BasicDataService basicDataService, final MasterDataService masterDataService, final DSLContext dslContext) {
        this.metadataService = metadataService;
        this.basicDataService = basicDataService;
        this.masterDataService = masterDataService;
        this.dslContext = dslContext;
    }

    @PostConstruct
    private void init() {
        this.createIfNotExists(ManufactureClassificationService.NAMES.values());
    }

    private void createIfNotExists(final Set<String> names) {
        final List<EntityVo> entities = this.basicDataService.entities("", 1, Integer.MAX_VALUE);
        final Set<String> set = entities.stream().map(EntityVo::getName).collect(Collectors.toSet());
        for (final String name : names) {
            if (!set.contains(name)) {
                this.basicDataService.create(new EntityVo(name, name));
            }
        }
    }

    public List<ManufactureClassification> list(final String entityName) {
        final List<BasicDataVo> list = this.basicDataService.list(entityName, "", 1, Integer.MAX_VALUE);
        return list.stream()
                .map(ManufactureClassification::fromBd)
                .toList();
    }

    public ManufactureClassification fetch(final String entityName, final String code) {
        final Optional<BasicDataVo> bd = this.basicDataService.fetch(entityName, code);
        return bd.map(ManufactureClassification::fromBd)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("实体[%s]的编码[%s]不存在", entityName, code)));
    }

    public void add(final String entityName, final ManufactureClassification classification) {
        this.basicDataService.add(entityName, classification.toBd());
    }

    private void modify(final String entityName, final ManufactureClassification classification) {
        this.basicDataService.modify(entityName, classification.toBd());
        final String mdEntityName = this.mdEntityName(entityName);
        final List<Map<String, Object>> data = this.findByClassificationCode(mdEntityName, classification.getCode());
        for (final Map<String, Object> row : data) {
            final Map<String, Object> newRow = classification.trans(mdEntityName, row);
            this.masterDataService.modify(mdEntityName, newRow);
        }
    }

    private String mdEntityName(final String entityName) {
        return ManufactureClassificationService.NAMES.inverse().get(entityName);
    }

    public List<Map<String, Object>> findByClassificationCode(final String mdEntityName, final String classificationCode) {
        final Entity entity = this.metadataService.findEntityNonNull(Namespace.MD, mdEntityName);

        final ManufactureClassification.Classification classification = ManufactureClassification.Classification.decode(classificationCode);

        return this.dslContext.select(entity.getAttributes().stream()
                        .map(a -> DSL.field(DSL.name(a.getName()), a.getDataType().javaClass))
                        .toList())
                .from(DSL.name(Namespace.MD.tableName(mdEntityName)))
                .where(classification.condition(mdEntityName))
                .fetchMaps();
    }

    public void delete(final String entityName, final String code) {
        this.basicDataService.delete(entityName, code);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void add(final String entityName, final String code, final String spec) {
        final Optional<BasicDataVo> bd = this.basicDataService.fetch(entityName, code);
        if (bd.isPresent()) {
            final ManufactureClassification classification = ManufactureClassification.fromBd(bd.get());
            classification.addSpec(spec);
            this.modify(entityName, classification);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("不存在[%s]的编码[%s]", entityName, code));
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void delete(final String entityName, final String code, final String spec) {
        final Optional<BasicDataVo> bd = this.basicDataService.fetch(entityName, code);
        if (bd.isPresent()) {
            final ManufactureClassification classification = ManufactureClassification.fromBd(bd.get());
            classification.deleteSpec(spec);
            this.modify(entityName, classification);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("不存在[%s]的编码[%s]", entityName, code));
        }
    }
}
