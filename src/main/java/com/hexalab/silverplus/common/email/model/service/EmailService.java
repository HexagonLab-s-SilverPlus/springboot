package com.hexalab.silverplus.common.email.model.service;

import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.Duration;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EmailService {
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private static final String senderEmail = "silverplus@hexalab.com";
    private final MemberRepository memberRepository;

    private String createCode() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >=97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String setContext(String code) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("code", code);
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("email", context);
    }

    private MimeMessage createEmailForm(String email) {
        log.info("전달온 이메일 확인(createEmailForm) : {}", email);
        try {
            String authCode = createCode();
            redisTemplate.opsForValue()
                    .set("EMAIL" + email, authCode, Duration.ofSeconds(60 * 3));
            log.info("redis에 저장되었는지 확인(createEmailForm) : {}", redisTemplate.opsForValue()
                    .get("EMAIL" + email));

            MimeMessage message = mailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[SilverPlus] 인증메일입니다.");
            message.setFrom(senderEmail);
            String body = "";
            body += "<h3>[SilverPlus] 인증번호입니다.</h3>";
            body += "<h4>아래 인증번호를 인증번호 입력칸에 입력해주세요.</h4>";
            body += "<br>";
            body += "<p>인증번호 : [<span style='font-weight: bold;'> " + authCode + " </span>]</p>";
            message.setText(body, "UTF-8", "html");

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }

    @Async
    public void sendEmail(String email) {
//        if (memberRepository.findByEmail(email))
        MimeMessage emailForm = createEmailForm(email);
        try {
            if(emailForm != null) {
                mailSender.send(emailForm);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    public Boolean verifyEmailCode(String code, String email) {
        String codeFoundEmail = redisTemplate.opsForValue().get("EMAIL" + email);
        log.info("저장된 이메일 코드 확인(EmailService.verifyEmailCode) : {}", codeFoundEmail);
        if (codeFoundEmail == null) {
            return false;
        }
        return codeFoundEmail.equals(code);
    }
}
