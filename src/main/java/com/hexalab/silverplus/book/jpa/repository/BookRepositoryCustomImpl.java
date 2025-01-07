package com.hexalab.silverplus.book.jpa.repository;

import com.hexalab.silverplus.book.jpa.entity.BookEntity;
import com.hexalab.silverplus.book.jpa.entity.QBookEntity;
import com.hexalab.silverplus.notice.jpa.entity.QNoticeEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    // QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;
    // JPQL 사용을 위한 의존성 주입
    private final EntityManager entityManager;
    private QBookEntity book = QBookEntity.bookEntity;

    // 북 검색 리스트 갯수 조회
    @Override
    public long selectSearchBookListCount(String keyword) {
        return queryFactory
                .selectFrom(book)
                .where(book.bookTitle.like("%"+keyword+"%"))
                .fetchCount();
    }
    // 북 검색 리스트
    @Override
    public List<BookEntity> selectSearchBookList(String keyword, Pageable pageable) {
        return queryFactory
                .selectFrom(book)
                .where(book.bookTitle.like("%"+keyword+"%"))
                .orderBy(book.bookCreateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
