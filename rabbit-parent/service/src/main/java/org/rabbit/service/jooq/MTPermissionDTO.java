package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Access control permissions of master table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MTPermissionDTO {

    String aces;

    String masterTableId;

    String masterTableName;

    String userId;

    String userType;

    boolean isRead;
    boolean isEdit;
    boolean isCreate;
    boolean isEnable;

}
