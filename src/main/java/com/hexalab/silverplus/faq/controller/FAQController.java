package com.hexalab.silverplus.faq.controller;

import com.hexalab.silverplus.faq.model.dto.FAQ;
import com.hexalab.silverplus.faq.model.service.FAQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/faq")
@CrossOrigin
public class FAQController {
    private final FAQService faqService;

    @PostMapping
    public void insertFAQ(@ModelAttribute FAQ faq) {
        log.info("Insert FAQ : " + faq);
        faqService.insertFAQ(faq);
    }
}
