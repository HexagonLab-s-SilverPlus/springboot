package com.hexalab.silverplus.common.sms.controller;

import com.hexalab.silverplus.common.sms.model.dto.SMS;
import com.hexalab.silverplus.common.sms.model.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;

@Slf4j
@RestController
public class SMSController {

    final DefaultMessageService messageService;
    final SMSService smsService;

    public SMSController(SMSService smsService) {
        this.messageService = NurigoApp.INSTANCE.initialize("NCS9M1JZE0NOLKYY", "CCODXLCCICYXL9DMHFHEBVZSYMFLVPUF", "https://api.coolsms.co.kr");
        this.smsService = smsService;
    }

    @PostMapping("/api/sms")
    public ResponseEntity sendSMS(@RequestBody SMS sms) {
        Message message = smsService.sendVerificationCode(sms.getMemCellphone());
        log.info("sms 전달값 확인 : {}",sms.getMemCellphone());

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("SMS response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/sms/verify")
    public ResponseEntity verifyCode(@RequestBody SMS sms) {
        log.info("전달온 인증번호 확인 : {}", sms.getCode());

//        if(smsService.verifyCode(sms.getMemCellphone(), sms.getCode())) {
//            String verifycode = "true";
//            response.setHeader("Access-Control-Expose-Headers", "Verify");
//            response.setHeader("Verify", verifycode);
//            log.info(response.getHeader("Verify"));
//        } else {
//            String verifycode = "false";
//            response.setHeader("Access-Control-Expose-Headers", "Verify");
//            response.setHeader("Verify", verifycode);
//            log.info(response.getHeader("Verify"));
//        }
//        return ResponseEntity.ok(response);

        if (smsService.verifyCode(sms.getMemCellphone(), sms.getCode())) {
            return ResponseEntity.ok().header("Verify", "true").body("인증 성공");
        } else {
            return ResponseEntity.ok().header("Verify", "false").body("인증 실패");
        }
    }
}
