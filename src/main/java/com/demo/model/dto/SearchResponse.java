package com.demo.model.dto;

import com.demo.model.SearchDocument;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponse {

    private List<SearchDocument> results;
    private long total;
    private long timeTaken;
}