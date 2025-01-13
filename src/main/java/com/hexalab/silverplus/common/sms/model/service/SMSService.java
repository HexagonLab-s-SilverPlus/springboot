package com.hexalab.silverplus.common.sms.model.service;

import com.hexalab.silverplus.common.sms.jpa.repository.SMSRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SMSService {

    private final SMSRepository smsRepository;

    private String generateSMSCode(){
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public Message  sendVerificationCode(String memCellphone) {
        String code = generateSMSCode();
        Message message = new Message();
        smsRepository.createSMSCertification(memCellphone, code);

        message.setFrom("01028258920");
        message.setTo(memCellphone);
        message.setText("[SilverPlus]" + "\n" + "인증번호는 [" + code + "] 입니다.");

        return message;
    }

    public boolean verifyCode(String memCellphone, String code) {
        String storedCode = smsRepository.getSMSCertification(memCellphone);
        log.info("redis에 저장된 인증번호정보 불러오기 : {}",storedCode);
        return storedCode != null && storedCode.equals(code);
    }

}
