package com.domain.backend.controller;

import com.domain.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> globalSearch(@RequestParam String keyword) {
        return searchService.globalSearch(keyword)
                .map(ResponseEntity::ok);
    }
}
