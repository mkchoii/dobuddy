package com.dobuddy.invite;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Integer>{
    boolean existsByEmail(String email);
}
