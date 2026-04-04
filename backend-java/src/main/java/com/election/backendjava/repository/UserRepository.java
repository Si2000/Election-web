package com.election.backendjava.repository;

import com.election.backendjava.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByBanedTrue();

    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);

    // Change token added
    Optional<User> findByEmailChangeToken(String token);
}
