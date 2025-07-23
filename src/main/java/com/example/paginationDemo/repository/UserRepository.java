package com.example.paginationDemo.repository;

import com.example.paginationDemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Offset pagination
    @Query(value = "SELECT * FROM users ORDER BY id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findUsersPaginated(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    long countAllUsers();

    // Keyset pagination (assumes ascending order by id)
    @Query(value = "SELECT * FROM users WHERE id > :lastId ORDER BY id LIMIT :limit", nativeQuery = true)
    List<User> findUsersKeyset(@Param("lastId") long lastId, @Param("limit") int limit);

    // Join pagination (example based on id)
    @Query(value = "SELECT u.* FROM users u JOIN (SELECT id FROM users ORDER BY id LIMIT :limit OFFSET :offset) sub ON u.id = sub.id", nativeQuery = true)
    List<User> findUsersJoinPaginated(@Param("limit") int limit, @Param("offset") int offset);

    // Rownum pagination using PostgreSQL window function
    @Query(value = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY id) as rn FROM users) t WHERE rn BETWEEN :start AND :end", nativeQuery = true)
    List<User> findUsersByRowNum(@Param("start") int start, @Param("end") int end);
}
