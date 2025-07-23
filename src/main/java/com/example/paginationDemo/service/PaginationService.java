package com.example.paginationDemo.service;

import com.example.paginationDemo.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaginationService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final int PAGE_SIZE = 10;

    public List<User> fetchPage(String method, int page) {
        int offset = (page - 1) * PAGE_SIZE;
        String sql;

        switch (method) {
            case "offset":
                sql = "SELECT * FROM users ORDER BY id OFFSET :offset LIMIT :limit";
                break;
            case "keyset":
                int keysetStartId = offset + 1;
                sql = "SELECT * FROM users WHERE id >= " + keysetStartId + " ORDER BY id LIMIT :limit";
                break;
            case "join":
                sql = "SELECT u.* FROM users u JOIN (SELECT id FROM users ORDER BY id OFFSET :offset LIMIT :limit) sub ON u.id = sub.id";
                break;
            case "rownum":
                sql = "WITH numbered AS (SELECT *, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM users) SELECT * FROM numbered WHERE rn BETWEEN :start AND :end";
                break;
            default:
                throw new IllegalArgumentException("Unknown method: " + method);
        }

        Query query = entityManager.createNativeQuery(sql, User.class);

        if (method.equals("rownum")) {
            query.setParameter("start", offset + 1);
            query.setParameter("end", offset + PAGE_SIZE);
        } else if (!method.equals("keyset")) {
            query.setParameter("offset", offset);
            query.setParameter("limit", PAGE_SIZE);
        } else {
            query.setParameter("limit", PAGE_SIZE);
        }

        return query.getResultList();
    }


}
