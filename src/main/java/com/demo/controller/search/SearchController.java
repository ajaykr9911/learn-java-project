package com.demo.controller.search;

import com.demo.model.dto.SearchResponse;
import com.demo.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService service;

    @PostMapping("/add")
    public String add(@RequestParam String content) {
        service.addDocument(content);
        return "Added!";
    }

    @GetMapping
    public SearchResponse search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long start = System.currentTimeMillis();

        SearchResponse response = service.search(q, page, size);

        long end = System.currentTimeMillis();

        return SearchResponse.builder()
                .results(response.getResults())
                .total(response.getTotal())
                .timeTaken(end - start)
                .build();
    }

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam String q) {
        return service.autocomplete(q);
    }
}