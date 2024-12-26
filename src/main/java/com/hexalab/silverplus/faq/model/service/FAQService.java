package com.hexalab.silverplus.faq.model.service;

import com.hexalab.silverplus.faq.jpa.repository.FAQRepository;
import com.hexalab.silverplus.faq.model.dto.FAQ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class FAQService {
    private final FAQRepository faqRepository;

    public void insertFAQ(FAQ faq) {
        try {
            faqRepository.save(faq.toEntity());
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
