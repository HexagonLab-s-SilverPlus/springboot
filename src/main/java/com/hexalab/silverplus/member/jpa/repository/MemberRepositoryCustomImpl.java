package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.entity.QMemberEntity;
import com.hexalab.silverplus.member.model.dto.Member;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Member findByMemId(String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memId.eq(memId))
                .fetchOne().toDto();
    }


}
