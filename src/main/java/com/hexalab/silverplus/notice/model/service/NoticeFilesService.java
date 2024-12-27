package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.repository.NoticeFilesRepository;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeFilesService {

    private final NoticeFilesRepository noticeFilesRepository;

    public int noticeFileInsert(NoticeFiles noticeFiles) {
        try {
            noticeFilesRepository.save(noticeFiles.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }
}
