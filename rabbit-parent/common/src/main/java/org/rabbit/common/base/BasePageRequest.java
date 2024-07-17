package org.rabbit.common.base;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;

/**
 * @author Lyle
 */
@Data
public class BasePageRequest {

    private static final String DEFAULT_SORT_FIELD = "createdDate";

    private Integer pageNum = 0;

    private Integer pageSize = 10;

    private String orderBy;

    private Boolean isDesc;

    public Sort getSort() {
        if (StringUtils.isBlank(getOrderBy())) {
            return Sort.by(DEFAULT_SORT_FIELD);
        }
        Sort sort;
        if (StringUtils.isBlank(getOrderBy())) {
            sort = Sort.by(DEFAULT_SORT_FIELD);
        } else {
            sort = Sort.by(getOrderBy());
        }
        if (null != getIsDesc() && getIsDesc()) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return sort;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        return pageSize;
    }

    public Integer getPageNum() {
        if (null == pageNum) {
            return 0;
        }
        return pageNum;
    }

    public Sort getDescSort() {
        if (StringUtils.isBlank(getOrderBy())) {
            return Sort.by(DEFAULT_SORT_FIELD).descending();
        }
        if (StringUtils.isBlank(getOrderBy())) {
            return Sort.by(DEFAULT_SORT_FIELD).descending();
        } else {
            return Sort.by(getOrderBy()).descending();
        }
    }

}
