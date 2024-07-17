package org.rabbit.service.form;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.service.jooq.MTFieldInfo;
import org.rabbit.service.jooq.MTFieldTypeMapping;
import org.rabbit.service.jooq.impl.AbstractJooqService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "docpal.case-type")
public class CaseTableConfig {

    private static final Logger logger = LoggerFactory.getLogger(CaseTableConfig.class);
    public static final String TABLE_NAME_PREFIX = "ct_";
    public static final String CASE_ID = "case_id";

    /**
     * Case Table column mapping list, you can customize config it for override.
     * <p>
     * For example:
     * docpal.caseType.column-mapping[0].value=varchar:64
     * docpal.caseType.column-mapping[0].label=Document
     * docpal.caseType.column-mapping[0].unique=false
     * docpal.caseType.column-mapping[0].key=document
     */
    private List<MTFieldTypeMapping> columnMapping;

    public List<MTFieldTypeMapping> getColumnMapping() {
        if (columnMapping == null || CollectionUtils.isEmpty(columnMapping)) {
            columnMapping = new ArrayList<>();
            // initialize column mapping
            columnMapping.addAll(AbstractJooqService.getFieldTypeMapping());
            columnMapping.add(new MTFieldTypeMapping("varchar:64", "Document", false, "document"));
            columnMapping.add(new MTFieldTypeMapping("varchar:255", "Master Table", false, "master_table"));
            columnMapping.add(new MTFieldTypeMapping("varchar:255", "Volcaboury", false, "volcaboury"));
            columnMapping.add(new MTFieldTypeMapping("varchar:255", "User Group", false, "user_group"));
        }
        return columnMapping.stream().sorted(Comparator.comparing(MTFieldTypeMapping::getLabel).reversed()).collect(Collectors.toList());
    }

    public static List<MTFieldInfo> getFixedFields() {
        List<MTFieldInfo> fixedFields = AbstractJooqService.getFixedFields();
        MTFieldInfo fixedField = new MTFieldInfo();
        fixedField.setFieldName(CASE_ID);
        fixedField.setDataType("varchar:255");
        fixedField.setUnique(false);
        fixedField.setRequired(false);
        fixedField.setPrimaryKey(false);
        fixedFields.add(fixedField);
        return fixedFields;
    }

    public String matchValue(String typeLabel) {
        Map<String, String> labelMap = getColumnMapping().stream().collect(Collectors.toMap(MTFieldTypeMapping::getLabel, MTFieldTypeMapping::getValue));
        if (StringUtils.isNotBlank(labelMap.get(typeLabel))) {
            return labelMap.get(typeLabel);
        }
        Map<String, String> keyMap = getColumnMapping().stream().collect(Collectors.toMap(MTFieldTypeMapping::getKey, MTFieldTypeMapping::getValue));
        if (StringUtils.isNotBlank(keyMap.get(typeLabel))) {
            return keyMap.get(typeLabel);
        }
        Set<String> valueSets = getColumnMapping().stream().map(MTFieldTypeMapping::getValue).collect(Collectors.toSet());
        if (valueSets.contains(typeLabel)) {
            return typeLabel;
        }
        throw new IllegalArgumentException( "Illegal dataType [" + typeLabel + "]");
    }

    public MTFieldTypeMapping match(String typeLabel) {
        Map<String, MTFieldTypeMapping> labelMap = getColumnMapping().stream().collect(Collectors.toMap(MTFieldTypeMapping::getLabel, a -> a, (k1, k2) -> k1));
        if (null != labelMap.get(typeLabel)) {
            return labelMap.get(typeLabel);
        }
        Map<String, MTFieldTypeMapping> keyMap = getColumnMapping().stream().collect(Collectors.toMap(MTFieldTypeMapping::getKey, a -> a, (k1, k2) -> k1));
        if (null != keyMap.get(typeLabel)) {
            return keyMap.get(typeLabel);
        }
        throw new IllegalArgumentException( "Illegal dataType [" + typeLabel + "]");
    }

}
