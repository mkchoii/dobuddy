package com.dobuddy.invite;

import com.dobuddy.BusinessException;
import com.dobuddy.invite.dto.InviteCreateRequest;
import com.dobuddy.invite.dto.InviteSentRequest;
import com.dobuddy.user.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public void createInvite(InviteCreateRequest request) {

        String email = request.getEmail();
        Channel channel = request.getChannel();

        if (channel == null || email == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "이메일 또는 채널 값이 없습니다."
            );
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "올바른 이메일 형식이 아닙니다."
            );
        }

        if (inviteRepository.existsByEmail(email)) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "이미 등록된 이메일입니다."
            );
        }

        Invite invite = new Invite(email, channel);
        inviteRepository.save(invite);
    }

    @Transactional
    public void updateInvitedAt(InviteSentRequest request) {

        String email = request.getEmail();

        if (email == null || request.getSentAt() == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "이메일 또는 발송 시각이 없습니다."
            );
        }

        Invite invite = inviteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "등록되지 않은 이메일입니다."
                ));

        LocalDateTime sentAt = request.getSentAt()
                .atZoneSameInstant(KST)
                .toLocalDateTime();

        invite.updateInvitedAt(sentAt);
    }
}
