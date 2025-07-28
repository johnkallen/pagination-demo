package com.example.pagination.backend.service;

import com.example.pagination.backend.repository.UserDetailsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class PaginationService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    public Map<String, Object> offset(Integer page, Integer size) {

        int offset = (page != null ? page : 1) - 1;

        List<?> users = em.createNativeQuery("SELECT id, username, created_at FROM users ORDER BY id LIMIT :size OFFSET :offset")
                .setParameter("size", size)
                .setParameter("offset", offset * size)
                .getResultList();

        int total = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", users.stream().map(this::mapUser).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }

    public Map<String, Object> keyset(Long cursorId, Integer size) {

        String sql = "SELECT id, username, created_at FROM users WHERE id > :cursor ORDER BY id LIMIT :size";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cursor", cursorId != null ? cursorId : 0L);
        q.setParameter("size", size);
        List<?> users = q.getResultList();

        return Map.of("data", users.stream().map(this::mapUser).toList());
    }

    public Map<String, Object> keysetpages(Long cursorId, Integer size) {

        String sql = "SELECT id, username, created_at FROM users WHERE id > :cursor ORDER BY id LIMIT :size";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cursor", cursorId != null ? cursorId : 0L);
        q.setParameter("size", size);
        List<?> users = q.getResultList();

        int total = 0;
        if (cursorId == null || cursorId == 0) total = ((Number) em.createNativeQuery
                ("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", users.stream().map(this::mapUser).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }


    public Map<String, Object> join(Integer page, Integer size) {

        int offset = (page != null ? page : 1) - 1;

        String sql = """
            SELECT u.id, u.username, u.created_at, p.phone_number, a.city
            FROM users u
            JOIN phones p ON u.id = p.user_id
            JOIN addresses a ON u.id = a.user_id
            ORDER BY u.id LIMIT :size OFFSET :offset
        """;

        Query q = em.createNativeQuery(sql);
        q.setParameter("size", size);
        q.setParameter("offset", offset * size);
        List<?> rows = q.getResultList();

        int total = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", rows.stream().map(this::mapUserWithJoin).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }

    public Map<String, Object> rownum(Integer page, Integer size) {

        int offset = (page != null ? page : 1) - 1;

        String sql = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY id) as rn FROM users) sub WHERE rn > :start AND rn <= :end";
        Query q = em.createNativeQuery(sql);
        q.setParameter("start", offset * size);
        q.setParameter("end", offset * size + size);

        List<?> rows = q.getResultList();

        int total = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", rows.stream().map(this::mapUser).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }

    public Map<String, Object> mv(Integer page, Integer size) {

        int offset = ((page != null ? page : 1) - 1) * size;

        List<Object[]> data = userDetailsRepository.findFromMaterializedView(size, offset);

        int total = ((Number) em.createNativeQuery("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", data.stream().map(this::mapUserWithMaterializedView).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }

    public Map<String, Object> keysetmv(Long cursorId, Integer size) {

        List<Object[]> data = userDetailsRepository.findFromMaterializedViewKeyset(size, cursorId != null ? cursorId : 0L);

        int total = 0;
        if (cursorId == null || cursorId == 0) total = ((Number) em.createNativeQuery
                ("SELECT COUNT(*) FROM users").getSingleResult()).intValue();

        return Map.of(
                "data", data.stream().map(this::mapUserWithMaterializedView).toList(),
                "totalPages", (int) Math.ceil(total / (double) size)
        );
    }

    private Map<String, Object> mapUser(Object row) {
        Object[] arr = (Object[]) row;
        return Map.of(
                "id", arr[0],
                "username", arr[1],
                "createdAt", arr[2]
        );
    }

    private Map<String, Object> mapUserWithJoin(Object row) {
        Object[] arr = (Object[]) row;
        return Map.of(
                "id", arr[0],
                "username", arr[1],
                "createdAt", arr[2],
                "phone", arr[3],
                "city", arr[4]
        );
    }

    private Map<String, Object> mapUserWithMaterializedView(Object row) {
        Object[] arr = (Object[]) row;
        return Map.of(
                "id", arr[0],
                "username", arr[1],
                "createdAt", arr[2],
                "phone", arr[3],
                "street", arr[4],
                "city", arr[5],
                "state", arr[6],
                "zipCode", arr[7]
        );
    }
}
