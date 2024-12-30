package com.hexalab.silverplus.notice.model.service;

import com.hexalab.silverplus.notice.jpa.entity.NoticeFilesEntity;
import com.hexalab.silverplus.notice.jpa.repository.NoticeFilesRepository;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoticeFilesService {

    private final NoticeFilesRepository noticeFilesRepository;

    // 파일 리스트 등록
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

    // 파일 갯수 확인
    public int checkNoticeFiles(String notId){
        return noticeFilesRepository.checkNoticeFiles(notId);
    };

    // 파일리스트 출력
    public ArrayList<NoticeFiles> selectNoticeFiles(String notId) {
        List<NoticeFilesEntity> list = noticeFilesRepository.selectNoticeFiles(notId);
        ArrayList<NoticeFiles> noticeFiles = new ArrayList<>();
        for (NoticeFilesEntity noticeFilesEntity : list) {
            noticeFiles.add(noticeFilesEntity.toDto());
        }
        return noticeFiles;
    }

    public int deleteNoticeFile(String nfId) {
        try {
            noticeFilesRepository.deleteById(nfId);
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }
}
