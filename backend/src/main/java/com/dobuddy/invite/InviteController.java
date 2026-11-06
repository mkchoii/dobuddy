package com.dobuddy.invite;

import com.dobuddy.BusinessException;
import com.dobuddy.invite.dto.InviteCreateRequest;
import com.dobuddy.invite.dto.InviteSentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;

    @Value("${invite.api-key}")
    private String apiKey;

    private void validateApiKey(String providedKey) {
        if (providedKey == null ||
                !MessageDigest.isEqual(
                        apiKey.getBytes(StandardCharsets.UTF_8),
                        providedKey.getBytes(StandardCharsets.UTF_8))) {
            throw new BusinessException(
                    HttpStatus.UNAUTHORIZED,
                    "유효하지 않은 API Key입니다."
            );
        }
    }

    @PostMapping("/invites")
    public ResponseEntity<Void> createInvite(
            @RequestHeader(value = "X_API_KEY", required = false) String key,
            @RequestBody InviteCreateRequest request
    ) {

        validateApiKey(key);
        inviteService.createInvite(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/invites/sent")
    public ResponseEntity<Void> updateInvitedAt(
            @RequestHeader(value = "X_API_KEY", required = false) String key,
            @RequestBody InviteSentRequest request
    ) {

        validateApiKey(key);
        inviteService.updateInvitedAt(request);

        return ResponseEntity.ok().build();
    }
}
