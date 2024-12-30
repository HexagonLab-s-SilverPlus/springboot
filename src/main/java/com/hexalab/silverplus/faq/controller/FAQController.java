package com.hexalab.silverplus.faq.controller;

import com.hexalab.silverplus.faq.model.dto.FAQ;
import com.hexalab.silverplus.faq.model.service.FAQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/faq")
@CrossOrigin
public class FAQController {
    private final FAQService faqService;

    @GetMapping
    public ResponseEntity<List<FAQ>> selectAll() {
        List<FAQ> faq = faqService.selectAll();
        log.info(faq.toString());
        return ResponseEntity.ok(faq);
    }

    @PostMapping
    public void insertFAQ(@RequestBody FAQ faq) {
        faq.setFaqCreatedAt(new Timestamp(System.currentTimeMillis()));
        faq.setFaqUpdatedAt(new Timestamp(System.currentTimeMillis()));
        faqService.insertFAQ(faq);
    }

    @PutMapping
    public void updateFAQ(@RequestBody FAQ faq) {
        log.info("faq : {}", faq);
        FAQ updateFaq = faqService.selectOne(faq.getFaqId());
        updateFaq.setFaqTitle(faq.getFaqTitle());
        updateFaq.setFaqContent(faq.getFaqContent());
        updateFaq.setFaqUpdatedBy(faq.getFaqUpdatedBy());
        updateFaq.setFaqUpdatedAt(new Timestamp(System.currentTimeMillis()));
        log.info("updateFaq : {}", updateFaq);
        faqService.updateFAQ(updateFaq);
    }

    @DeleteMapping("/{faqId}")
    public void deleteFAQ(@PathVariable String faqId) {
        faqService.deleteFAQ(faqId);
    }
}
