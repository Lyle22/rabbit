package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MasterTableRelationDTO  {

    /**
     * really table name of database
     */
    String realSourceTableName;
    /**
     * really table alais of database when query sql
     */
    String sourceTableAlais;

    /**
     * really table name of database
     */
    String realJoinTableName;
    /**
     * table alais of database when query sql
     */
    String joinTableAlais;


}
