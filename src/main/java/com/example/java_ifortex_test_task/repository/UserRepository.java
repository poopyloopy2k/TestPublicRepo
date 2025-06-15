package com.example.java_ifortex_test_task.repository;

import com.example.java_ifortex_test_task.entity.DeviceType;
import com.example.java_ifortex_test_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
    SELECT u.*
    FROM users u
    INNER JOIN (
        SELECT user_id, MAX(started_at_utc) as latest_mobile_session_start
        FROM sessions
        WHERE device_type = :#{#deviceType.code}
        GROUP BY user_id
         ) s ON u.id = s.user_id
    WHERE u.deleted = false
    ORDER BY s.latest_mobile_session_start DESC
""", nativeQuery = true)
    List<User> getUsersWithAtLeastOneMobileSession(DeviceType deviceType);

    @Query(value = """
    SELECT u.* FROM users u 
    INNER JOIN sessions s ON u.id = s.user_id
    GROUP BY u.id ORDER BY COUNT(s.id) DESC 
    LIMIT 1
""", nativeQuery = true)
    User getUserWithMostSessions();
}
