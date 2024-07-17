package org.rabbit.service.form.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rabbit.common.base.PaginationDTO;
import org.rabbit.entity.form.FormDesign;
import org.rabbit.entity.form.FormFieldMapping;
import org.rabbit.entity.form.FormInfo;
import org.rabbit.service.form.IFormDesignService;
import org.rabbit.service.form.IFormInfoService;
import org.rabbit.service.form.mapper.FormDesignMapper;
import org.rabbit.service.form.models.FormDesignDataDTO;
import org.rabbit.service.form.models.FormDesignRequestDTO;
import org.rabbit.service.form.models.FormDesignResponseDTO;
import org.rabbit.service.form.models.FormInfoDTO;
import org.rabbit.service.jooq.impl.JooqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author nine rabbit
 **/

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FormDesignServiceImpl extends ServiceImpl<FormDesignMapper, FormDesign> implements IFormDesignService {

    private final IFormInfoService tableInfoService;
    private final JooqService jooqService;

    @Override
    public FormDesign findById(String id) {
        FormDesign formDesign = baseMapper.selectById(id);
        if (formDesign == null) {
            throw new IllegalArgumentException("data does not exists");
        }
        return formDesign;
    }

    @Override
    public FormDesignResponseDTO getReleaseById(String id) {
        FormDesign formDesign = findById(id);
        return FormDesignResponseDTO.builder().build().transform(formDesign);
    }

    @Override
    public List<FormDesignResponseDTO> findAll(String name) {
        List<FormDesign> formDesigns = baseMapper.selectList(null);
        if (StringUtils.isNotBlank(name)) {
            formDesigns = formDesigns.stream().filter(item -> item.getName().equals(name)).collect(Collectors.toList());
        }
        return formDesigns.stream().map(formDesign -> FormDesignResponseDTO.builder().build().transform(formDesign)).collect(Collectors.toList());
    }

    /**
     * Submit Form Data
     * <p/>Stored into database table
     * <p>Note that starting a process instance if the form design is bound to a process definition</p>
     *
     * @param formDataDTO the form data
     * @return whether successfully updated
     */
    @Override
    public Boolean submitFormData(FormDesignDataDTO formDataDTO) {
        Map<String, Object> dataMap = formDataDTO.getData();
        if (null == dataMap || dataMap.isEmpty()) {
            log.warn("If the form is to be submitted, form data must not be null");
            return false;
        }
        FormDesign formDesign = findById(formDataDTO.getId());
        FormInfoDTO formInfoDTO = tableInfoService.getReleaseByBizId(formDesign.getId());
        // Save data into database table
        List<String> fieldNames = formInfoDTO.getFieldMappings().stream().map(FormFieldMapping::getFieldName).collect(Collectors.toList());
        // Remove the value of a non-form field
        dataMap.keySet().removeIf(key -> !fieldNames.contains(key));
        if (StringUtils.isBlank(formDataDTO.getBizNo())) {
            formDataDTO.setBizNo(UUID.randomUUID().toString());
        }
        boolean inserted = tableInfoService.insertRecord(formInfoDTO, formDataDTO.getBizNo(), formDataDTO.getBizType(), dataMap);
        // Starting process instance if exist
        if (inserted && StringUtils.isNotBlank(formDesign.getProcessDefinitionKey())) {
//            ProcessDefinitionDTO processDefinition = workflowServiceImpl.getProcessDefinition(formDesign.getProcessDefinitionKey());
//            if (null == processDefinition) {
//                log.warn("User want to start a process instance after submitting form data, but the process definition does not exist.");
//                log.warn("Form design=[{}] process definition=[{}]", formDesign.getName(), formDesign.getProcessDefinitionKey());
//                return true;
//            } else {
//                Map<String, String> properties = new HashMap<>();
//                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
//                    properties.put(entry.getKey(), String.valueOf(entry.getValue()));
//                }
//                WorkflowRequestDTO workflowRequest = new WorkflowRequestDTO();
//                workflowRequest.setProcessKey(formDesign.getProcessDefinitionKey());
//                workflowRequest.setBusinessKey(formDataDTO.getBizNo());
//                workflowRequest.setProperties(properties);
//                workflowServiceImpl.startProcess(workflowRequest);
//            }
        }
        return true;
    }

    @Override
    public PaginationDTO<List<LinkedHashMap<String, Object>>> recordPage(FormDesignRequestDTO request) {
        Assert.notNull(request.getId(), "id can not empty.");
        FormDesign formDesign = findById(request.getId());
        FormInfo formInfo = tableInfoService.findByLabel(formDesign.getName(), IFormInfoService.DEFAULT_TABLE_NAME_PREFIX);
        return tableInfoService.pageQueryRecord(formInfo, request.getPageNum(), request.getPageSize(), request.getWhere());
    }

}
