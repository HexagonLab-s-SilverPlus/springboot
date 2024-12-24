package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
import com.hexalab.silverplus.notice.jpa.repository.NoticeRepository;
import com.hexalab.silverplus.notice.model.dto.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


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

    public int selectAllNoticeListCount() {
        return (int)noticeRepository.count();
    }


    public ArrayList<Notice> selectAllNoticeList(Pageable pageable) {
        Page<NoticeEntity> entityList = noticeRepository.findAll(pageable);
        ArrayList<Notice> noticeList = new ArrayList<Notice>();
        for(NoticeEntity entity : entityList) {
            noticeList.add(entity.toDto());
        }
        return noticeList;
    }
}
