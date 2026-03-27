package com.demo.controller.url;

import com.demo.model.UrlShorter;
import com.demo.service.urlservice.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
@RequestMapping("/api/v1/urls")
@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService service;

    @PostMapping("/shorten")
    public String shortenUrl(@RequestParam String url) {
        String shortId = service.createShortUrl(url);
        return "http://localhost:8080/api/v1/urls/" + shortId;
    }

    @GetMapping("/{shortId}")
    public void redirect(@PathVariable String shortId, HttpServletResponse response) throws IOException {
        Optional<UrlShorter> url = service.getOriginalUrl(shortId);

        if (url.isPresent()) {
            String originalUrl = url.get().getOriginalUrl();
            // Ensure the URL has a protocol, otherwise redirect fails
            if (!originalUrl.startsWith("http")) {
                originalUrl = "https://" + originalUrl;
            }
            response.sendRedirect(originalUrl);
        } else {
            response.sendError(404, "URL not found");
        }
    }
}