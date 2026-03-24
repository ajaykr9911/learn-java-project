//package com.demo.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class LocationService {
//
//    private final WebClient webClient;
//
//    public LocationService(WebClient.Builder builder) {
//        this.webClient = builder.baseUrl("https://countriesnow.space/api/v0.1").build();
//    }
//
//    public Mono<String> getIndianStates() {
//        return webClient.post()
//                .uri("/countries/states")
//                .bodyValue("{\"country\":\"India\"}")
//                .retrieve()
//                .bodyToMono(String.class);
//    }
//}
