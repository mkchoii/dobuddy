package com.dobuddy;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendTemporaryPassword(String email, String password) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject("[DOBUDDY] 임시 비밀번호 발급");

            String html = """
                    <div style="font-family: Arial, sans-serif; padding: 24px;">
                        <h2 style="font-size: 22px;">
                            임시 비밀번호 발급
                        </h2>
                        
                        <p style="font-size: 14px;">
                            요청하신 임시 비밀번호가 발급되었습니다.
                        </p>
                        
                        <p style="font-size: 28px; font-weight: 700; color: #393433;">
                            <strong>%s</strong>
                        </p>
                        
                        <p style="font-size: 13px; color: #777;">
                            로그인 후 마이페이지에서 비밀번호를 변경해주세요.
                        </p>
                    </div>
                    """.formatted(password);

            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("이메일 생성에 실패했습니다.", e);
        }
    }
}
