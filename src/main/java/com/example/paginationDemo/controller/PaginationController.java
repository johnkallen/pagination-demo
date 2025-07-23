package com.example.paginationDemo.controller;

import com.example.paginationDemo.repository.UserRepository;
import com.example.paginationDemo.service.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PaginationController {
    @Autowired
    private PaginationService paginationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/health")
    public String getHealth() {
        return "Healthy Connection!";
    }

    @GetMapping("/pagination")
    public ResponseEntity<Map<String, Object>> paginate(
            @RequestParam String method,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Long cursorId
    ) {
        long start = System.currentTimeMillis();
        Map<String, Object> result;

        switch (method.toLowerCase()) {
            case "keyset":
                result = paginationService.keyset(cursorId);
                break;
            case "join":
                result = paginationService.join(page);
                break;
            case "rownum":
                result = paginationService.rownum(page);
                break;
            default:
                result = paginationService.offset(page);
        }
        result = new HashMap<>(result);
        result.put("durationMs", System.currentTimeMillis() - start);
        return ResponseEntity.ok(result);
    }
}
