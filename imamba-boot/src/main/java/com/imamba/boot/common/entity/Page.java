package com.imamba.boot.common.entity;

import com.imamba.boot.common.exception.MError;
import com.imamba.boot.common.exception.MException;

public class Page {

    private Long totalCount;
    private Long pageIndex;
    private Long pageSize;

    public Page() {
        this.totalCount = 0L;
        this.pageIndex = 0L;
        this.pageSize = 0L;
        this.pageIndex = 1L;
        this.pageSize = 10L;
    }

    public Page(Long pageIndex) {
        this();
        pageIndex = pageIndex == null ? 1L : pageIndex;
        this.pageSize = 10L;
        this.pageIndex = pageIndex;
    }

    public Page(Long pageIndex, Long pageSize) {
        this.totalCount = 0L;
        this.pageIndex = 0L;
        this.pageSize = 0L;
        pageIndex = pageIndex == null ? 1L : pageIndex;
        pageSize = pageSize == null ? 1L : pageSize;
        if (pageIndex > 0L && pageSize > 0L) {
            if (pageSize > 1000L) {
                throw new MException(MError.PAGER_PARAMETER_IS_NOT_CORRECT, "Page Size too large, limit:1000");
            } else {
                this.pageIndex = pageIndex;
                this.pageSize = pageSize;
            }
        } else {
            throw new MException(MError.PAGER_PARAMETER_IS_NOT_CORRECT);
        }
    }

    public Long getPageSize() {
        return this.pageSize = this.pageSize < 1L ? 1L : this.pageSize;
    }

    public void setPageSize(Long pageSize) {
        pageSize = pageSize == null ? 1L : pageSize;
        if (pageSize <= 0L) {
            throw new MException(MError.PAGER_PARAMETER_IS_NOT_CORRECT);
        } else if (pageSize > 1000L) {
            throw new MException(MError.PAGER_PARAMETER_IS_NOT_CORRECT, "Page Size too large");
        } else {
            this.pageSize = pageSize;
        }
    }

    public Long getPageIndex() {
        return this.pageIndex < 1L ? 1L : this.pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        pageIndex = pageIndex == null ? 1L : pageIndex;
        if (pageIndex <= 0L) {
            throw new MException(MError.PAGER_PARAMETER_IS_NOT_CORRECT);
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getPageCount() {
        return this.totalCount % this.pageSize > 0L ? this.totalCount / this.pageSize + 1L : this.totalCount / this.pageSize;
    }

    public String toString() {
        return "Page{totalCount=" + this.totalCount + ", pageIndex=" + this.pageIndex + ", pageSize=" + this.pageSize + '}';
    }
}
