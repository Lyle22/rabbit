package org.rabbit.workflow.service.bpmn;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.rabbit.service.form.models.CmmnPlanFormDTO;

import java.util.*;

/**
 * The parse class of process definition
 *
 * @author nine rabbit
 */
@UtilityClass
public class ProcessDefinitionParser {

    /**
     * <extensionElements>
     * <flowable:permissions>
     * <field name="members" accesstype="view"></field>
     * <field name="ITSales" accesstype="view"></field>
     * <field name="members" accesstype="start"></field>
     * </flowable:permissions>
     * </extensionElements>
     *
     * @param model the model of the process definition
     * @return List<Map < String, String>>
     */
    public static List<Map<String, String>> extractPermissions(BpmnModel model) {
        List<Process> processes = model.getProcesses();
        if (CollectionUtils.isEmpty(processes)) {
            return Collections.emptyList();
        }
        Map<String, List<ExtensionElement>> extensions = model.getMainProcess().getExtensionElements();
        List<Map<String, String>> permissions = new ArrayList<>();
        if (!extensions.isEmpty() && null != extensions.get("permissions") && !extensions.get("permissions").isEmpty()) {
            List<ExtensionElement> formElements = extensions.get("permissions");
            for (ExtensionElement formElement : formElements) {
                if (formElement.getChildElements().size() == 0) {
                    continue;
                }
                List<ExtensionElement> elementList = formElement.getChildElements().get("field");
                permissions = extractExtensionFields(elementList);
            }
        }
        return permissions;
    }

    private static List<Map<String, String>> extractExtensionFields(List<ExtensionElement> extensionElements) {
        List<Map<String, String>> fields = new ArrayList<>();
        for (ExtensionElement extensionElement : extensionElements) {
            Map<String, String> fieldMap = new HashMap<>();
            for (Map.Entry<String, List<ExtensionAttribute>> entry : extensionElement.getAttributes().entrySet()) {
                for (ExtensionAttribute attribute : entry.getValue()) {
                    fieldMap.put(attribute.getName(), attribute.getValue());
                }
            }
            if (!fieldMap.isEmpty()) {
                fields.add(fieldMap);
            }
        }
        fields.stream().distinct();
        return fields;
    }

    /**
     * <flowable:folderCabinetMapping id="f086f926-793e-48bc-adcb-a7d300470374" name="P-File">
     * <field formProperty="UserName" metadata="FileName"></field>
     * <field formProperty="Subject" metadata="Title"></field>
     * </flowable:folderCabinetMapping>
     *
     * @param model the bpmn model
     * @return List<Map < String, String>>
     */
    public static List<Map<String, String>> extractFolderCabinetDataMapping(BpmnModel model) {
        List<Process> processes = model.getProcesses();
        if (CollectionUtils.isEmpty(processes)) {
            return Lists.newArrayList();
        }
        Map<String, List<ExtensionElement>> extensions = model.getMainProcess().getExtensionElements();
        List<Map<String, String>> fields = new ArrayList<>();
        if (!extensions.isEmpty() && null != extensions.get("folderCabinetMapping") && !extensions.get("folderCabinetMapping").isEmpty()) {
            List<ExtensionElement> formElements = extensions.get("folderCabinetMapping");
            for (ExtensionElement formElement : formElements) {
                if (formElement.getChildElements().size() == 0) {
                    continue;
                }
                Map<String, List<ExtensionAttribute>> attributes = formElement.getAttributes();
                // Parse <flowable:folderCabinetMapping> tag :: <flowable:folderCabinetMapping id="f086f926-793e-48bc-adcb-a7d300470374" name="P-File">
                Map<String, String> attributeMap = new HashMap<>();
                for (Map.Entry<String, List<ExtensionAttribute>> entry : attributes.entrySet()) {
                    for (ExtensionAttribute attribute : entry.getValue()) {
                        attributeMap.put(attribute.getName(), attribute.getValue());
                    }
                }
                CmmnPlanFormDTO formDTO = BeanUtil.mapToBean(attributeMap, CmmnPlanFormDTO.class, false, new CopyOptions());
                Map<String, String> mappingMap = new HashMap<>();
                mappingMap.put("folderCabinetId", formDTO.getId());
                mappingMap.put("folderCabinetName", formDTO.getName());
                // Parse field element :: <field formProperty="Subject" metadata="Title"></field>
                List<ExtensionElement> extensionElements = formElement.getChildElements().get("field");
                for (ExtensionElement extensionElement : extensionElements) {
                    for (Map.Entry<String, List<ExtensionAttribute>> entry : extensionElement.getAttributes().entrySet()) {
                        for (ExtensionAttribute attribute : entry.getValue()) {
                            mappingMap.put(attribute.getName(), attribute.getValue());
                        }
                    }
                    if (!mappingMap.isEmpty()) {
                        fields.add(mappingMap);
                    }
                }
                fields.stream().distinct();
            }
        }
        return fields;
    }


    public static List<Map<String, String>> extractFilingDataMapping(Map<String, List<ExtensionElement>> extensions) {
        List<Map<String, String>> fields = new ArrayList<>();
        if (!extensions.isEmpty() && null != extensions.get("folderCabinetMapping") && !extensions.get("folderCabinetMapping").isEmpty()) {
            List<ExtensionElement> formElements = extensions.get("folderCabinetMapping");
            for (ExtensionElement formElement : formElements) {
                if (formElement.getChildElements().size() == 0) {
                    continue;
                }
                Map<String, List<ExtensionAttribute>> attributes = formElement.getAttributes();
                // Parse <flowable:folderCabinetMapping> tag :: <flowable:folderCabinetMapping id="f086f926-793e-48bc-adcb-a7d300470374" name="P-File">
                Map<String, String> attributeMap = new HashMap<>();
                for (Map.Entry<String, List<ExtensionAttribute>> entry : attributes.entrySet()) {
                    for (ExtensionAttribute attribute : entry.getValue()) {
                        attributeMap.put(attribute.getName(), attribute.getValue());
                    }
                }
//                CmmnPlanFormDTO formDTO = BeanUtil.mapToBean(attributeMap, CmmnPlanFormDTO.class, false, new CopyOptions());
                Map<String, String> mappingMap = new HashMap<>();
                // Parse field element :: <field formProperty="Subject" metadata="Title"></field>
                List<ExtensionElement> extensionElements = formElement.getChildElements().get("field");
                for (ExtensionElement extensionElement : extensionElements) {
                    for (Map.Entry<String, List<ExtensionAttribute>> entry : extensionElement.getAttributes().entrySet()) {
                        for (ExtensionAttribute attribute : entry.getValue()) {
                            mappingMap.put(attribute.getName(), attribute.getValue());
                        }
                    }
                    if (!mappingMap.isEmpty()) {
                        fields.add(mappingMap);
                    }
                }
                fields.stream().distinct();
            }
        }
        return fields;
    }

    public static void extractGenerateDocumentConfig(Map<String, List<ExtensionElement>> extensions) {
        List<Map<String, String>> fields = new ArrayList<>();

        if (!extensions.isEmpty() && null != extensions.get("folderCabinetMapping") && !extensions.get("folderCabinetMapping").isEmpty()) {
            List<ExtensionElement> formElements = extensions.get("folderCabinetMapping");
            for (ExtensionElement formElement : formElements) {
                if (formElement.getChildElements().size() == 0) {
                    continue;
                }
                Map<String, List<ExtensionAttribute>> attributes = formElement.getAttributes();
            }
        }
    }
}
