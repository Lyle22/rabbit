package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MastTableLogDTO {

    String masterTableId;

    Boolean success;

    String tableName;

    String oldRecord;

    String record;

}
