package com.example.paginationDemo.controller;

import com.example.paginationDemo.model.User;
import com.example.paginationDemo.repository.UserRepository;
import com.example.paginationDemo.service.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping("/health")
    public String getHealth() {
        return "Healthy Connection!";
    }

    @GetMapping("/paginationorig")
    public Map<String, Object> getPaginatedData(
            @RequestParam String method,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        long startTime = System.currentTimeMillis();
        List<User> users;
        long totalRecords = 0;
        int totalPages = 0;
        users = paginationService.fetchPage(method, page);
        long endTime = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        response.put("durationMs", endTime - startTime);
        return response;
    }

    @GetMapping("/pagination")
    public ResponseEntity<Map<String, Object>> getPaginatedUsers(
            @RequestParam String method,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") long lastId
    ) {
        long start = System.currentTimeMillis();

        List<User> users = new ArrayList<>();
        long totalRecords = 0;
        int totalPages = 0;

        switch (method.toLowerCase()) {
            case "offset":
                int offset = (page - 1) * size;
                users = userRepository.findUsersPaginated(size, offset);
                totalRecords = userRepository.countAllUsers();
                totalPages = (int) Math.ceil((double) totalRecords / size);
                break;

            case "keyset":
                users = userRepository.findUsersKeyset(lastId, size);
                // Total pages not known in keyset mode, return placeholder
                totalPages = -1;
                break;

            case "join":
                int offsetJ = (page - 1) * size;
                users = userRepository.findUsersJoinPaginated(size, offsetJ);
                totalRecords = userRepository.countAllUsers();
                totalPages = (int) Math.ceil((double) totalRecords / size);
                break;

            case "rownum":
                int startRow = (page - 1) * size + 1;
                int endRow = startRow + size - 1;
                users = userRepository.findUsersByRowNum(startRow, endRow);
                totalRecords = userRepository.countAllUsers();
                totalPages = (int) Math.ceil((double) totalRecords / size);
                break;

            default:
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid pagination method"));
        }

        long duration = System.currentTimeMillis() - start;

        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        response.put("durationMs", duration);
        response.put("totalPages", totalPages);
        response.put("page", page);

        return ResponseEntity.ok(response);
    }
}
