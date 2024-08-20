package org.rabbit.workflow.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rabbit.common.contains.Result;
import org.rabbit.service.form.IFormDesignService;
import org.rabbit.service.form.models.FormDesignDataDTO;
import org.rabbit.service.form.models.FormDesignRequestDTO;
import org.rabbit.service.form.models.FormDesignResponseDTO;
import org.rabbit.service.jooq.impl.JooqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * form design controller
 *
 * @author nine rabbit
 */
@Slf4j
@RestController
@RequestMapping("${API_VERSION}/docpal/form/design")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FormDesignController {

    private final IFormDesignService formDesignService;
    private final JooqService jooqService;

    @GetMapping(value = {"/{id}/detail"})
    public Result<FormDesignResponseDTO> getReleaseById(@PathVariable String id) {
        return Result.ok(formDesignService.getReleaseById(id));
    }

    @GetMapping
    public Result<List<FormDesignResponseDTO>> findAll(@RequestParam(required = false) String name) {
        return Result.ok(formDesignService.findAll(name));
    }

    @PostMapping(value = "/submit/data")
    public Result<Boolean> submitFormData(@RequestBody FormDesignDataDTO formDataDTO) {
        return Result.ok(formDesignService.submitFormData(formDataDTO));
    }

    @PostMapping(value = {"/record/page"})
    public Result<Object> findRecordPage(@RequestBody FormDesignRequestDTO request) {
        return Result.ok(formDesignService.recordPage(request));
    }

    @PostMapping(value = {"/caseQuery"})
    public Object caseQuery(@RequestParam String tableName) {
        List<String> columnNames = Arrays.asList("User_Name", "description");
        return jooqService.casePage(tableName, "biz_no", columnNames, Map.of("jiojkl","nin"));
    }

}