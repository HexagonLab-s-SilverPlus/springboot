package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.entity.QMemberEntity;
import com.hexalab.silverplus.qna.jpa.entity.QQnAEntity;
import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import com.querydsl.core.Tuple;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
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

    @Override
    public Map<String, Object> selectADList(Pageable pageable, Search search) {
        List<Tuple> qnaEntityList = null;
        if(search.getAction().equals("all")){
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        } else if(search.getAction().equals("title")) {
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .where(qna.qnaTitle.like("%" + search.getKeyword() + "%"))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if(search.getAction().equals("date")) {
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .where(qna.qnaWCreateAt.between(search.getStartDate(), search.getEndDate()))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        Map<String, Object> resultList = new HashMap<>();
        ArrayList<QnAEntity> qnaList = new ArrayList<>();
        ArrayList<MemberEntity> memberList = new ArrayList<>();

        for (Tuple tuple : qnaEntityList) {
            qnaList.add(tuple.get(0, QnAEntity.class));
            memberList.add(tuple.get(1, MemberEntity.class));
        }
        resultList.put("qna", qnaList);
        resultList.put("member", memberList);
        resultList.put("search", search);

        return resultList;
    }

    @Override
    public Map<String, Object> selectMyQnA(String uuid, Pageable pageable, Search search) {
        List<Tuple> qnaEntityList = null;
        if(search.getAction().equals("all")){
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .where(qna.qnaWCreateBy.eq(uuid))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if(search.getAction().equals("title")) {
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .where(qna.qnaWCreateBy.eq(uuid).and(qna.qnaTitle.like("%" + search.getKeyword() + "%")))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if(search.getAction().equals("date")) {
            qnaEntityList = queryFactory
                    .select(qna, member)
                    .from(qna)
                    .leftJoin(member).on(qna.qnaWCreateBy.eq(member.memUUID))
                    .where(qna.qnaWCreateBy.eq(uuid).and(qna.qnaWCreateAt.between(search.getStartDate(), search.getEndDate())))
                    .orderBy(qna.qnaWUpdateAt.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }


        Map<String, Object> resultList = new HashMap<>();
        ArrayList<QnAEntity> qnaList = new ArrayList<>();
        ArrayList<MemberEntity> memberList = new ArrayList<>();
        for (Tuple tuple : qnaEntityList) {
            qnaList.add(tuple.get(0, QnAEntity.class));
            memberList.add(tuple.get(1, MemberEntity.class));
        }

        resultList.put("qna", qnaList);
        resultList.put("member", memberList);
        resultList.put("search", search);

        return resultList;
    }

    @Override
    public int myCount(String uuid) {
        return (int)queryFactory
                .selectFrom(qna)
                .where(qna.qnaWCreateBy.eq(uuid))
                .fetchCount();
    }

    @Override
    public int adTitleCount(String keyword) {
        return (int)queryFactory
                .selectFrom(qna)
                .where(qna.qnaTitle.like("%" + keyword + "%"))
                .fetchCount();
    }

    @Override
    public int adDateCount(Search search) {
        return (int)queryFactory
                .selectFrom(qna)
                .where(qna.qnaWCreateAt.between(search.getStartDate(),search.getEndDate()))
                .fetchCount();
    }

    @Override
    public int myTitleCount(String uuid, String keyword) {
        return (int)queryFactory
                .selectFrom(qna)
                .where(qna.qnaWCreateBy.eq(uuid).and(qna.qnaTitle.like("%" + keyword + "%")))
                .fetchCount();
    }

    @Override
    public int myDateCount(String uuid, Search search) {
        return (int)queryFactory
                .selectFrom(qna)
                .where(qna.qnaWCreateBy.eq(uuid).and(qna.qnaWCreateAt.between(search.getStartDate(),search.getEndDate())))
                .fetchCount();
    }
}
