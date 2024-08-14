package org.rabbit.workflow.models;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * The type of base page request
 */
@Data
public class BasePageRequest {

    @Schema(description = "Page num")
    private Integer pageNum = 0;

    @Schema(description = "Page Size")
    private Integer pageSize = 10;

    public Integer getPageCount() {
        if (getPageSize() == -1) {
          return Integer.MAX_VALUE - 1;
        } else {
            return getPageSize();
        }
    }

}

