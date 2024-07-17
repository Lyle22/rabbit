package org.rabbit.service.jooq;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MTDataValidateDTO {

    boolean validate;

    String errorMessage;

    List<String> validateResult = new ArrayList<>();

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.validate = false;
    }

}
