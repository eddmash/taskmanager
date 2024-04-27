package com.eddmash.app.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private int status;
    private String message;
    private T data;

    /**
     * only included if its not null
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int pageNo;
        private int pageSize;
        private Long totalRecords;
        private int totalPages;

    }
}
