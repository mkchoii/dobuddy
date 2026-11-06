package com.dobuddy.invite;

import com.dobuddy.user.Channel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Entity
@Table(name = "invites")
@NoArgsConstructor
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime invitedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private Channel channel;

    public Invite(String email, Channel channel) {
        this.email = email;
        this.channel = channel;
    }

    @PrePersist
    public void prePersist() {
        this.invitedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void updateInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }
}
