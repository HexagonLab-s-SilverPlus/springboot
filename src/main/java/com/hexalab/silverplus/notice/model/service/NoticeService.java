package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.repository.NoticeRepository;
import com.hexalab.silverplus.notice.model.dto.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // notice insert
    public int noticeInsert(Notice notice) {
        try {
            noticeRepository.save(notice.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }
}
