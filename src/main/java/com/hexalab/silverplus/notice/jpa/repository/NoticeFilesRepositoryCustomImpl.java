package com.hexalab.silverplus.notice.jpa.repository;

import com.hexalab.silverplus.notice.jpa.entity.NoticeFilesEntity;
import com.hexalab.silverplus.notice.jpa.entity.QNoticeEntity;
import com.hexalab.silverplus.notice.jpa.entity.QNoticeFilesEntity;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeFilesRepositoryCustomImpl implements NoticeFilesRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private QNoticeFilesEntity noticeFiles = QNoticeFilesEntity.noticeFilesEntity;

    // 파일 갯수확인
    @Override
    public int checkNoticeFiles(String notId){
        return (int)queryFactory
                .selectFrom(noticeFiles)
                .where(noticeFiles.nfNotId.eq(notId))
                .fetchCount();
    }

    // 파일 리스트 출력
    @Override
    public List<NoticeFilesEntity> selectNoticeFiles(String notId) {
        return queryFactory
                .selectFrom(noticeFiles)
                .where(noticeFiles.nfNotId.eq(notId))
                .fetch();
    }

    ;
}
