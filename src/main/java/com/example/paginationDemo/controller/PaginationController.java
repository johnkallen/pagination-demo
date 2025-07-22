package com.example.paginationDemo.controller;

import com.example.paginationDemo.model.User;
import com.example.paginationDemo.service.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagination")
@CrossOrigin(origins = "http://localhost:3000")
public class PaginationController {
    @Autowired
    private PaginationService paginationService;

    @GetMapping
    public List<User> getPaginatedData(
            @RequestParam String method,
            @RequestParam int page
    ) {
        return paginationService.fetchPage(method, page);
    }
}
