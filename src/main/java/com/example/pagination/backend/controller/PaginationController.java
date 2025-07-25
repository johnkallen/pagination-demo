package com.example.pagination.backend.controller;

import com.example.pagination.backend.repository.UserDetailsDto;
import com.example.pagination.backend.repository.UserDetailsRepository;
import com.example.pagination.backend.repository.UserRepository;
import com.example.pagination.backend.service.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class PaginationController {
    @Autowired
    private PaginationService paginationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @GetMapping("/health")
    public String getHealth() {
        return "Healthy Connection!";
    }

    @GetMapping("/pagination")
    public ResponseEntity<Map<String, Object>> paginate(
            @RequestParam String method,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false) Integer pageSize
    ) {
        long start = System.currentTimeMillis();
        if (pageSize == null) pageSize = 10; // Default top pageSize
        Map<String, Object> result;

        switch (method.toLowerCase()) {
            case "keyset":
                result = paginationService.keyset(cursorId, pageSize);
                break;
            case "join":
                result = paginationService.join(page, pageSize);
                break;
            case "rownum":
                result = paginationService.rownum(page, pageSize);
                break;
            case "materialized":
                result = paginationService.mv(page, pageSize);
                break;
            default:
                result = paginationService.offset(page, pageSize);
        }
        result = new HashMap<>(result);
        result.put("durationMs", System.currentTimeMillis() - start);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pagination/materialized")
    public ResponseEntity<Map<String, Object>> getFromMaterializedView(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long start = System.currentTimeMillis();
        int offset = page * size;

        List<Object[]> rows = userDetailsRepository.findFromMaterializedView(size, offset);

        List<UserDetailsDto> data = rows.stream().map(row -> new UserDetailsDto(
                ((Number) row[0]).longValue(),  // userId
                (String) row[1],               // username
                (Timestamp) row[2],            // createdAt
                (String) row[3],               // phoneNumber
                (String) row[4],               // street
                (String) row[5],               // city
                (String) row[6],               // state
                (String) row[7]                // zipCode
        )).toList();

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("durationMs", System.currentTimeMillis() - start);
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(result);
    }
}
