package com.demo.controller.search;

import com.demo.model.SearchDocument;
import com.demo.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService service;

    @PostMapping("/add")
    public String add(@RequestParam String content) {
        service.addDocument(content);
        return "Document added!";
    }

    @GetMapping("/search")
    public List<SearchDocument> search(@RequestParam String q) {
        return service.search(q);
    }
}