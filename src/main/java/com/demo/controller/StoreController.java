package com.demo.controller;

import com.demo.exception.CustomException;
import com.demo.model.Store;
import com.demo.model.dto.BaseResponse;
import com.demo.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/register")
    public BaseResponse<Store> registerStore(@RequestBody Store store) {

        Store savedStore = storeService.registerStore(store);

        return BaseResponse.success(savedStore);
    }

    @GetMapping("/search")
    public BaseResponse<Store> searchStore(
            @RequestParam String name,
            @RequestParam double lat,
            @RequestParam double lng) {

        Optional<Store> store = storeService.searchStore(name, lat, lng);

        if (store.isEmpty()) {
            throw new CustomException("Store not available in your area");
        }

        return BaseResponse.success(store.get());
    }
}