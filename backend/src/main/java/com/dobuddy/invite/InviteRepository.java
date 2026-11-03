package com.dobuddy.invite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Integer>{
    boolean existsByEmail(String email);
    Optional<Invite> findByEmail(String email);
}
