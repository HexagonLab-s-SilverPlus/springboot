package com.hexalab.silverplus.medical.jpa.repository;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;
import com.hexalab.silverplus.medical.jpa.entity.QMedicalEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MedicalRepositoryImpl implements MedicalRepositoryCustom {
    //QueryDSL
    private final JPAQueryFactory queryFactory;
    //JPQL
    private final EntityManager entityManager;
    private QMedicalEntity medical = QMedicalEntity.medicalEntity;

    @Override
    public List<MedicalEntity> selectAllMedicalList(String mediSnrUUID, Pageable pageable) {
        return queryFactory
                .selectFrom(medical)
                .where(medical.mediSnrUUID.eq(mediSnrUUID))
                .orderBy(medical.mediDiagDate.desc(), medical.mediCreatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public int selectAllCount(String mediSnrUUID) {
        return (int)queryFactory
                .selectFrom(medical)
                .where(medical.mediSnrUUID.eq(mediSnrUUID))
                .fetchCount();
    }

    @Override
    public int updateMedicalPrivacy(String mediSnrUUID, String mediPrivacy) {
        long updatedRows = queryFactory
                .update(medical)
                .set(medical.mediPrivacy, mediPrivacy)
                .where(medical.mediSnrUUID.eq(mediSnrUUID))
                .execute();

        return (int) updatedRows;
    }

    @Override
    public String selectMedicalStatus(String mediSnrUUID) {
        return queryFactory
                .select(medical.mediPrivacy) // 가져오고자 하는 필드 선택
                .from(medical)
                .where(medical.mediSnrUUID.eq(mediSnrUUID))
                .limit(1) // 여러 개의 결과 중 하나만 가져오기
                .fetchOne(); // 단일 결과 반환
    }
}//MedicalRepositoryImpl end
