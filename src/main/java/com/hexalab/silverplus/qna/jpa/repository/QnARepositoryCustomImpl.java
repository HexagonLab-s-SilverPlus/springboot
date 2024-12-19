package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.qna.jpa.entity.QQnAEntity;
import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QnARepositoryCustomImpl implements QnARepositoryCustom {

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;

    //JPQL 사용을 위해 의존성 주입
    private final EntityManager entityManager;

    //QnA 테이블을 의미하는 QnAEntity 를 qna 로 표현한다는 선언임
    private QQnAEntity qna = QQnAEntity.qnAEntity;

    @Override
    public List<QnAEntity> selectMyQnA(String uuid, Pageable pageable) {
        return queryFactory
                .selectFrom(qna)
                .where(qna.qnaWCreateBy.eq(uuid))
                .orderBy(qna.qnaWUpdateAt.desc())
                .offset(pageable.getOffset())
                .limit((pageable.getPageSize()))
                .fetch();
    }
}
