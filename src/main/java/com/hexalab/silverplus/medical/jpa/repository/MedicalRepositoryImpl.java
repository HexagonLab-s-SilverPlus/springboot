package com.hexalab.silverplus.medical.jpa.repository;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;
import com.hexalab.silverplus.medical.jpa.entity.QMedicalEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<MedicalEntity> selectAllMedicalList(String mediSnrUUID) {
        return queryFactory
                .selectFrom(medical)
                .where(medical.mediSnrUUID.eq(mediSnrUUID))
                .orderBy(medical.mediCreatedAt.desc())
                .fetch();
    }
}//MedicalRepositoryImpl end
