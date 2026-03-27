package com.demo.service.urlservice;

import com.demo.model.UrlShorter;
import com.demo.repo.UrlRepository;
import com.demo.util.ShortIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {


    private final UrlRepository repository;

    public String createShortUrl(String originalUrl) {
        String shortId = ShortIdGenerator.generateShortId();

        UrlShorter url = new UrlShorter();
        url.setOriginalUrl(originalUrl);
        url.setShortId(shortId);
        url.setCreatedAt(System.currentTimeMillis());

        repository.save(url);

        return shortId;
    }

    public Optional<UrlShorter> getOriginalUrl(String shortId) {
        return repository.findByShortId(shortId);
    }
}