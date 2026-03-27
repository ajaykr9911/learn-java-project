package com.demo.repo;

import com.demo.model.UrlShorter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UrlRepository extends MongoRepository<UrlShorter, String> {
    Optional<UrlShorter> findByShortId(String shortId);
}