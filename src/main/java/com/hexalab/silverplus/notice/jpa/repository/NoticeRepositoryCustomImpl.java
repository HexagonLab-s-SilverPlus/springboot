package com.hexalab.silverplus.notice.jpa.repository;

import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
import com.hexalab.silverplus.notice.jpa.entity.QNoticeEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {
    // QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;
    // JPQL 사용을 위한 의존성 주입
    private final EntityManager entityManager;
    private QNoticeEntity notice = QNoticeEntity.noticeEntity;

    @Override
    public long selectAllNoticeListCount() {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .fetchCount();
    }

    @Override
    public List<NoticeEntity> selectAllNoticeList(Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .orderBy(notice.notCreateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long selectSearchTitleNoticeListCount(String keyword) {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.notTitle.like("%" + keyword + "%"))  //where noticetitle %keyword%
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .fetchCount();
    }

    @Override
    public long selectSearchContentNoticeListCount(String keyword) {
        return queryFactory
                .selectFrom(notice)     //select * from notice
                .where(notice.notContent.like("%" + keyword + "%"))  //where noticetitle %keyword%
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .fetchCount();
    }

    @Override
    public List<NoticeEntity> selectSearchTitleNoticeList(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.notTitle.like("%" + keyword + "%"))
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .orderBy(notice.notCreateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<NoticeEntity> selectSearchContentNoticeList(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(notice)
                .where(notice.notContent.like("%" + keyword + "%"))
                .where(notice.notDeleteAt.isNull()) // 삭제되지 않은 자료
                .orderBy(notice.notCreateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
