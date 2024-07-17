package org.rabbit.service.jooq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MTColumnInfo {

    String columnName;
    String dataType;
    Integer length;
    boolean isPrimaryKey;
    boolean isRequired;
    boolean isUnique;

    // related table columns information
    String relationTable;
    String relationField;
    String displayField;

    // Pre-check conditions
    boolean nullRelation;

    public MTColumnInfo(String columnName, String dataType, Integer length, boolean isPrimaryKey, boolean isRequired, boolean isUnique) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.length = length;
        this.isPrimaryKey = isPrimaryKey;
        this.isRequired = isRequired;
        this.isUnique = isUnique;
    }

}
