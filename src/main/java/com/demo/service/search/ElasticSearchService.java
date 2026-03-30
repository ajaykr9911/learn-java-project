//package com.demo.service.search;
//
//import com.demo.model.ESDocument;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.client.elc.NativeQuery;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ElasticSearchService {
//
//    private final ElasticsearchTemplate template;
//
//    public List<ESDocument> search(String query) {
//
//        NativeQuery searchQuery = NativeQuery.builder()
//                .withQuery(q -> q
//                        .match(m -> m
//                                .field("content")
//                                .query(query)
//                                .fuzziness("AUTO")
//                        )
//                )
//                .build();
//
//        return template.search(searchQuery, ESDocument.class)
//                .stream()
//                .map(hit -> hit.getContent())
//                .toList();
//    }
//}