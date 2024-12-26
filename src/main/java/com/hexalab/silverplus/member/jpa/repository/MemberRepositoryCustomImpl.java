package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.entity.QMemberEntity;
import com.hexalab.silverplus.member.model.dto.Member;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final QMemberEntity member = QMemberEntity.memberEntity;


    @Override
    public long selectCheckId(String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memId.eq(memId))
                .fetchCount();
    }

    @Override
    public MemberEntity findByMemId(String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memId.eq(memId))
                .fetchOne();
    }

    @Override
    public long removeByMemId(String memId) {
        return queryFactory
                .update(member)
                .set(member.memStatus, "REMOVED")
                .where(member.memId.eq(memId))
                .execute();
    }

    @Override
    public List<MemberEntity> selectAllMember(Pageable pageable, Search search) {
        entityManager.clear();
        List<MemberEntity> list = new ArrayList<>();
        if(search.getAction().equals("all")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN"))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            log.info("조회하는 값 확인(전체) : {}", list);
        } else if (search.getAction().equals("memId")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memId.like("%" + search.getKeyword() + "%")))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("memName")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memName.like("%" + search.getKeyword() + "%")))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("memStatus")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memStatus.eq(search.getKeyword())))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("memType"))
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memType.eq(search.getKeyword())))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        return list;
    }

    public long selectAllCount(){
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN"))
                .fetchCount();
    }


    @Override
    public long selectMemIdCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memId.like("%" + keyword + "%")))
                .fetchCount();
    }

    @Override
    public long selectMemNameCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memName.like("%" + keyword + "%")))
                .fetchCount();
    }

    @Override
    public long selectMemStatusCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memStatus.eq(keyword)))
                .fetchCount();
    }

    @Override
    public long selectMemTypeCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memType.eq(keyword)))
                .fetchCount();
    }

}
