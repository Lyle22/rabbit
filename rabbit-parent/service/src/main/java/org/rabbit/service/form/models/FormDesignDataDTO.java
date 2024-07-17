package org.rabbit.service.form.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FormDesignDataDTO {


    private String id;

    Map<String, Object> data = new HashMap<>();


    private String bizNo;


    private String bizType;

}
