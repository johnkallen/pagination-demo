package com.example.pagination.backend.repository;

import com.example.pagination.backend.entities.UserDetailsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDetailsRepository extends JpaRepository<UserDetailsView, Long> {

    @Query(value = "SELECT user_id, username, created_at, phone_number, street, city, state, zip_code " +
            "FROM user_details_mv ORDER BY user_id LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Object[]> findFromMaterializedView(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT user_id, username, created_at, phone_number, street, city, state, zip_code " +
            "FROM user_details_mv WHERE user_id > :cursorId ORDER BY user_id LIMIT :limit", nativeQuery = true)
    List<Object[]> findFromMaterializedViewKeyset(@Param("limit") int limit, @Param("cursorId") long cursorId);

}
