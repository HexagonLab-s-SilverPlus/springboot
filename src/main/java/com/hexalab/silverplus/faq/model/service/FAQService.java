package com.hexalab.silverplus.faq.model.service;

import com.hexalab.silverplus.faq.jpa.entity.FAQEntity;
import com.hexalab.silverplus.faq.jpa.repository.FAQRepository;
import com.hexalab.silverplus.faq.model.dto.FAQ;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public List<FAQ> selectAll(Pageable pageable) {
        log.info("selectAll"+ pageable.toString());
        Page<FAQEntity> faqEntities = faqRepository.findAll(pageable);
        log.info("selectAll22222222222222222"+faqEntities);
        List<FAQ> faq = new ArrayList<>();
        for (FAQEntity faqEntity : faqEntities) {
            faq.add(faqEntity.toDto());
        }

        return faq;
    }

    public void deleteFAQ(String faqId) {
        faqRepository.deleteById(faqId);
    }

    public FAQ selectOne(String faqId) {
        return faqRepository.findById(faqId).get().toDto();
    }

    public void updateFAQ(FAQ faq) {
        faqRepository.save(faq.toEntity());
    }

    public int selectCountAll() {
        return (int)faqRepository.count();
    }
}
