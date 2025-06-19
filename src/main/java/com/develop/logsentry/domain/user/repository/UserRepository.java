package com.develop.logsentry.domain.user.repository;

import com.develop.logsentry.common.exception.CustomException;
import com.develop.logsentry.common.exception.ErrorCode;
import com.develop.logsentry.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmailAndIsActiveTrue(String email);

    default void throwIfEmailExists(String email) {
        if (existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS, null, email);
        }
    }

    default void throwIfUsernameExists(String username) {
        if (existsByUsername(username)) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS, null, username);
        }
    }

    default User findByEmailAndIsActiveTrueOrThrow(String email) {
        return findByEmailAndIsActiveTrue(email).orElseThrow(() -> new CustomException(ErrorCode.USER_INACTIVE, null, "USER"));
    }
}
