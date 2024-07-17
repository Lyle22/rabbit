package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MTInsertRecord {

    String table;

    List<Map<String, Object>> data;

    String userId;

    Map<String, Object> where;

}
