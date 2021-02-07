package com.imamba.boot.common.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.imamba.boot.common.exception.IError;
import com.imamba.boot.common.exception.MError;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {

    private static final long serialVersionUID = -6602365878131231511L;
    public static final String DEFAULT_DATA_KEY = "data";
    public static final String DEFAULT_DATAS_KEY = "datas";
    private Response.Status status;
    private String errorCode;
    private String errorMessage;
    private String extMessage;
    private Long pageIndex;
    private Long pageCount;
    private Long pageSize;
    private Long totalCount;
    @JsonIgnore
    private Map<String, Object> any;

    public Response() {
        this.status = Response.Status.SUCCEED;
    }

    public static Response success() {
        Response response = new Response();
        return response;
    }

    public static Response success(Object data) {
        return success("data", data);
    }

    public static Response success(String key, Object data) {
        return success(key, data, (Page)null);
    }

    public static Response success(Object data, Page page) {
        return success("datas", data, page);
    }

    public static Response success(String key, Object data, Page page) {
        Response response = success().put(key, data);
        if (page != null) {
            response.page(page);
        }

        return response;
    }

    public static Response error() {
        return error(MError.SYSTEM_INTERNAL_ERROR);
    }

    public static Response error(IError error) {
        Response response = new Response();
        response.errorCode = error.getErrorCode();
        response.errorMessage = error.getErrorMessage();
        response.status = Response.Status.FAILED;
        return response;
    }

    public Response put(Object any) {
        if (this.any == null) {
            this.any = new HashMap();
        }

        this.any.put("data", any);
        return this;
    }

    public Response put(String key, Object data) {
        if (data == null) {
            return this;
        } else {
            if (this.any == null) {
                this.any = new HashMap();
            }

            this.any.put(key, data);
            return this;
        }
    }

    public Response put(Map<String, Object> any) {
        if (this.any == null) {
            this.any = new HashMap();
        }

        this.any.putAll(any);
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> anyGetter() {
        return this.any;
    }

    @JsonAnySetter
    public void anySetter(String name, Object value) {
        if (this.any == null) {
            this.any = new HashMap();
        }

        this.any.put(name, value);
    }

    private Response page(Page page) {
        this.pageIndex = page.getPageIndex();
        this.pageCount = page.getPageCount();
        this.pageSize = page.getPageSize();
        this.totalCount = page.getTotalCount();
        return this;
    }

    public Response.Status getStatus() {
        return this.status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExtMessage() {
        return this.extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public Long getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public static enum Status {
        SUCCEED,
        FAILED;

        private Status() {
        }
    }
}
