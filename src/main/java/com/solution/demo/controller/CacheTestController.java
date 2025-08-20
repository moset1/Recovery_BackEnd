package com.solution.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CacheTestController {
    @GetMapping("/api/cache-test")
    public ResponseEntity<Map<String, String>> getCachableData() {
        // 이 내용은 정적이므로 ETag가 항상 동일하게 생성됩니다.
        return ResponseEntity.ok(Map.of("data", "this is some cachable content"));
    }
}