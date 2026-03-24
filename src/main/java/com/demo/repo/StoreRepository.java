package com.demo.repo;

import com.demo.model.Store;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StoreRepository extends MongoRepository<Store, String> {

    List<Store> findByNameIgnoreCase(String name);
}