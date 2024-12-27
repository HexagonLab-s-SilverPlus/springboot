package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import com.hexalab.silverplus.member.jpa.entity.QMemberFilesEntity;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberFilesRepositoryCustomImpl implements MemberFilesRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final QMemberFilesEntity memberFiles = QMemberFilesEntity.memberFilesEntity;

    @Override
    public List<MemberFilesEntity> findByMemUuid(String memUuid) {
        return queryFactory
                .selectFrom(memberFiles)
                .where(memberFiles.mfMemUUID.eq(memUuid))
                .fetch();
    }
}
