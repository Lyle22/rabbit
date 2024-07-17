package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MTFieldInfo {

    String fieldName;
    String dataType;
    Boolean required;
    Boolean unique;
    Boolean primaryKey;

    // related table columns information
    String relationTable;
    String relationField;
    String displayField;

    public MTFieldInfo(String fieldName, String dataType, Boolean required, Boolean unique, Boolean primaryKey) {
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.required = required;
        this.unique = unique;
        this.primaryKey = primaryKey;
    }

}
