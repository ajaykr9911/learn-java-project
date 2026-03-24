package com.demo.service;

import com.demo.model.Store;
import com.demo.repo.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;


    public Store registerStore(Store store) {

        if (store == null) {
            throw new IllegalArgumentException("Store data cannot be null");
        }

        return storeRepository.save(store);
    }

    public Optional<Store> searchStore(String name, double userLat, double userLng) {

        List<Store> stores = storeRepository.findByNameIgnoreCase(name);

        for (Store store : stores) {

            double distance = DistanceUtil.calculateDistance(
                    userLat,
                    userLng,
                    store.getLatitude(),
                    store.getLongitude()
            );

            if (distance <= store.getServiceRadiusKm()) {
                return Optional.of(store);
            }
        }

        return Optional.empty();
    }
}
