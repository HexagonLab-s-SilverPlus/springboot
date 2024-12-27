package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
import com.hexalab.silverplus.notice.jpa.repository.NoticeRepository;
import com.hexalab.silverplus.notice.model.dto.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public int noticeDelete(String notId) {
        try {
            noticeRepository.deleteById(notId);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    public int selectSearchTitleNoticeListCount(String keyword) {
        return (int)noticeRepository.selectSearchTitleNoticeListCount(keyword);
    }

    public int selectSearchContentNoticeListCount(String keyword) {
        return (int)noticeRepository.selectSearchContentNoticeListCount(keyword);
    }

    public ArrayList<Notice> selectSearchTitleNoticeList(String keyword, Pageable pageable) {
        ArrayList<Notice> list=new ArrayList<Notice>();
        List<NoticeEntity> noticeList = noticeRepository.selectSearchTitleNoticeList(keyword,pageable);
        for(NoticeEntity entity : noticeList) {
            list.add(entity.toDto());
        }
        return list;
    }

    public ArrayList<Notice> selectSearchContentNoticeList(String keyword, Pageable pageable) {
        ArrayList<Notice> list=new ArrayList<Notice>();
        List<NoticeEntity> noticeList = noticeRepository.selectSearchContentNoticeList(keyword,pageable);
        for(NoticeEntity entity : noticeList) {
            list.add(entity.toDto());
        }
        return list;
    }
}
