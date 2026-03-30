package com.demo.repo;


import com.demo.model.SearchDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<SearchDocument, String> {
}