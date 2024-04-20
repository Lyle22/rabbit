package org.rabbit.common.utils;

import lombok.Data;

@Data
public class PageHelper {
    /**
     * 页码，从0开始 --主要适配
     */
    private int pageNum;
    /**
     * 页面大小，默认50
     */
    private int pageSize;
    /**
     * 起始行
     */
    private long startRow;
    /**
     * 末行
     */
    private long endRow;

    public PageHelper(Integer pageNum, Integer pageSize) {
        if (null == pageNum || pageNum <= 0) {
            this.pageNum = 0;
        } else {
            this.pageNum = pageNum;//workflow内部分页查询 从0开始
        }
        if (null == pageSize || pageSize < 0) {
            this.pageSize = 50;//默认50
        } else {
            this.pageSize = pageSize;
        }
        calculateStartAndEndRow();
    }

    private void calculateStartAndEndRow() {//workflow内部分页查询 从0开始
        this.startRow = this.pageNum > 0 ? (this.pageNum) * this.pageSize : 0;
        this.endRow = this.startRow + this.pageSize * (this.pageNum > 0 ? 1 : 0);
    }
}
