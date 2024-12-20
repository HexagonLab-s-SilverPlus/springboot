package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.QMemberEntity;
import com.hexalab.silverplus.qna.jpa.entity.QQnAEntity;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QnARepositoryCustomImpl implements QnARepositoryCustom {

    //QueryDSL 에 대한 config 클래스를 먼저 만들고 나서 필드 선언함
    private final JPAQueryFactory queryFactory;

    //JPQL 사용을 위해 의존성 주입
    private final EntityManager entityManager;

    //QnA 테이블을 의미하는 QnAEntity 를 qna 로 표현한다는 선언임
    private QQnAEntity qna = QQnAEntity.qnAEntity;
    private QMemberEntity member = QMemberEntity.memberEntity;

//    @Override
//    public List<QnAEntity> selectMyQnA(String uuid, Pageable pageable) {
//        return queryFactory
//                .selectFrom(qna)
//                .where(qna.qnaWCreateBy.eq(uuid))
//                .orderBy(qna.qnaWUpdateAt.desc())
//                .offset(pageable.getOffset())
//                .limit((pageable.getPageSize()))
//                .fetch();
//    }
    @Override
    public List<Map<String, Object>> selectMyQnA(String uuid, Pageable pageable) {
        List<Tuple> qnaEntityList = queryFactory
                .select(qna, member.memName)
                .from(qna)
                .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                .where(qna.qnaWCreateBy.eq(uuid))
                .orderBy(qna.qnaWUpdateAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Tuple tuple : qnaEntityList) {
            Map<String, Object> result = new HashMap<>();
            result.put("qna", tuple.get(0, QQnAEntity.class));
            result.put("memName", tuple.get(1, String.class));
            resultList.add(result);
        }

        return resultList;
    }
}
