package org.rabbit.service.jooq.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rabbit.service.jooq.MTFieldInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableRequestDTO {

    private String table;

    private List<MTFieldInfo> fields;

}
