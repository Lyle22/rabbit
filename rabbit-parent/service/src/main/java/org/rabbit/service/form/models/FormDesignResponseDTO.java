package org.rabbit.service.form.models;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rabbit.common.utils.JsonHelper;
import org.rabbit.entity.form.FormDesign;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDesignResponseDTO {


    private String id;


    private String name;


    private String publishStatus;


    private String processDefinitionKey;


    private String permission;


    private Boolean enable;


    private List<PlanTableFieldDTO> information = new ArrayList<>();


    private String previewStyle;


    private List<ParamMappingDTO> formResult = new ArrayList<>();

    private Instant createdDate;

    private Instant modifiedDate;

    public FormDesignResponseDTO transform(FormDesign formDesign) {
        if (formDesign == null) {
            return null;
        }
        FormDesignResponseDTO responseDTO = FormDesignResponseDTO.builder()
                .id(formDesign.getId())
                .name(formDesign.getName())
                .enable(formDesign.isEnable())
                .publishStatus(formDesign.getPublishStatus())
                .processDefinitionKey(formDesign.getProcessDefinitionKey())
                .permission(formDesign.getPermission())
                .previewStyle(formDesign.getPreviewStyle())
                .createdDate(formDesign.getCreatedDate())
                .modifiedDate(formDesign.getModifiedDate())
                .build();
        if (formDesign.getInfoJson().equals("{}")) {
            responseDTO.setInformation(new ArrayList<>());
        } else {
            responseDTO.setInformation(JsonHelper.read(formDesign.getInfoJson(), new TypeReference<List<PlanTableFieldDTO>>() {}));
        }
        if (formDesign.getFormResult().equals("{}")) {
            responseDTO.setFormResult(new ArrayList<>());
        } else {
            responseDTO.setFormResult(JsonHelper.read(formDesign.getFormResult(), new TypeReference<List<ParamMappingDTO>>() {
            }));
        }
        return responseDTO;
    }

}
