package org.rabbit.service.jooq;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * The type of master table type mapping
 */
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MTFieldTypeMapping {

    String value;

    String label;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String key;

    boolean unique;

    public MTFieldTypeMapping(String value, String label, boolean unique) {
        this.value = value;
        this.label = label;
        this.unique = unique;
    }

    public MTFieldTypeMapping(String value, String label, boolean unique, String key) {
        this.value = value;
        this.label = label;
        this.unique = unique;
        this.key = key;
    }

}
