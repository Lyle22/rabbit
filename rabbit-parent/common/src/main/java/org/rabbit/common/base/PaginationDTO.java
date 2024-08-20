package org.rabbit.common.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nine rabbit
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDTO<E> implements Serializable {

    private List<E> entryList;

    private Integer totalSize;

    private Integer currentPageSize;

    private Integer pageNum;

    private Integer pageCount;

    @JsonProperty("isNextPageAvailable")
    private Boolean isNextPageAvailable;

    public static <E> PaginationDTO<E> EMPTY() {
        PaginationDTO<E> paginationDTO = new PaginationDTO<>();
        paginationDTO.setEntryList(new ArrayList<>());
        paginationDTO.setTotalSize(0);
        return paginationDTO;
    }

    public PaginationDTO(List<E> eneityList, long totalCount) {
        this.totalSize = Long.valueOf(totalCount).intValue();
        this.entryList = eneityList;
    }

    public PaginationDTO(Page<E> page) {
        this.totalSize = Long.valueOf(page.getTotalElements()).intValue();
        this.entryList = page.getContent();
        this.pageNum = page.getNumber();
        this.pageCount = page.getTotalPages();
        this.currentPageSize = page.getSize();
        this.isNextPageAvailable = pageCount > this.currentPageSize;
    }

    /**
     * History Usage
     * 对list进行分页截取
     * 从左往右,第一个T表示参数包括泛型参数,第二个T表示返回T类型的数据,第三个T限制参数是类型为T
     *
     * @param pageNum  当前页
     * @param pageSize 分页长度
     * @param list     数据集
     * @param <T>      泛型
     * @return 分页后的数据
     */
    public static <T> List<T> subListPage(int pageNum, int pageSize, List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        int i = list.size() % pageSize;
        int totalPaqe = list.size() / pageSize;
        if (i != 0) {
            totalPaqe = totalPaqe + 1;
        }
        if (pageNum > totalPaqe) {
            pageNum = totalPaqe;
        }
        int startIndex = (pageNum) * pageSize;
        int endindex = startIndex + pageSize;
        int totalNum = list.size();
        if (endindex > totalNum) {
            endindex = totalNum;
        }
        if (startIndex >= totalNum) {
            return Lists.newArrayList();
        }
        return list.subList(startIndex, endindex);
    }

}