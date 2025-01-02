package com.hexalab.silverplus.common.email.controller;

import com.hexalab.silverplus.common.email.model.dto.Email;
import com.hexalab.silverplus.common.email.model.service.EmailService;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/api/email")
    public ResponseEntity sendEmail(@RequestBody Email email) {
        log.info("전달온 이메일객체 확인(sendEmail) : {}", email);
        log.info("전달온 이메일 확인(sendEmail) : {}", email.getEmail());
        try {
            emailService.sendEmail(email.getEmail());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/api/email/verify")
    public ResponseEntity emailVerify(@RequestBody Email email) {
        log.info("전달온 이메일객체 확인(emailVerify) : {}", email);
        if(emailService.verifyEmailCode(email.getVerifyCode(), email.getEmail())) {
            return ResponseEntity.ok().header("Verify", "true").body("인증 성공");
        } else {
            return ResponseEntity.ok().header("Verify", "false").body("인증 실패");
        }
    }
}
