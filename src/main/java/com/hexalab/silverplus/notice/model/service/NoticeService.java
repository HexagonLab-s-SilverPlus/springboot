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
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    // 공지사항 입력
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

    // 공지사항 갯수
    public int selectAllNoticeListCount() {
        return (int)noticeRepository.count();
    }

    // 공지사항 리스트 출력
    public ArrayList<Notice> selectAllNoticeList(Pageable pageable) {
        Page<NoticeEntity> entityList = noticeRepository.findAll(pageable);
        ArrayList<Notice> noticeList = new ArrayList<Notice>();
        for(NoticeEntity entity : entityList) {
            noticeList.add(entity.toDto());
        }
        return noticeList;
    }

    // 파일업로드 실패시 입력한 공지사항 삭제용
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

    // 공지사항 검색 리스트 갯수(제목)
    public int selectSearchTitleNoticeListCount(String keyword) {
        return (int)noticeRepository.selectSearchTitleNoticeListCount(keyword);
    }

    // 공지사항 검색 리스트 갯수(내용)
    public int selectSearchContentNoticeListCount(String keyword) {
        return (int)noticeRepository.selectSearchContentNoticeListCount(keyword);
    }

    // 공지사항 검색 리스트(제목)
    public ArrayList<Notice> selectSearchTitleNoticeList(String keyword, Pageable pageable) {
        ArrayList<Notice> list=new ArrayList<Notice>();
        List<NoticeEntity> noticeList = noticeRepository.selectSearchTitleNoticeList(keyword,pageable);
        for(NoticeEntity entity : noticeList) {
            list.add(entity.toDto());
        }
        return list;
    }

    // 공지사항 검색 리스트(내용)
    public ArrayList<Notice> selectSearchContentNoticeList(String keyword, Pageable pageable) {
        ArrayList<Notice> list=new ArrayList<Notice>();
        List<NoticeEntity> noticeList = noticeRepository.selectSearchContentNoticeList(keyword,pageable);
        for(NoticeEntity entity : noticeList) {
            list.add(entity.toDto());
        }
        return list;
    }

    // 공지사항 상세보기
    public Notice selectNotice(String notId) {
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(notId);
        return noticeEntity.get().toDto();
    }
    
    // 공지사항 조회수 증가
    @Transactional
    public Notice upReadCount(String notId) {
        Optional<NoticeEntity> noticeEntity = noticeRepository.findById(notId);
        NoticeEntity notice = noticeEntity.get();
        log.info("upReadCount : " + notice);
        notice.setNotReadCount(notice.getNotReadCount() + 1);
        return noticeRepository.save(notice).toDto();
    }
}
