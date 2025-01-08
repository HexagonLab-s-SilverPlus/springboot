package com.hexalab.silverplus.emg.jpa.repository;

import com.hexalab.silverplus.emg.jpa.entity.EMGEntity;
import com.hexalab.silverplus.emg.jpa.entity.QEMGEntity;
import com.hexalab.silverplus.emg.model.dto.EMG;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EMGRepositoryCustomImpl implements EMGRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final EntityManager entityManager;

    private QEMGEntity emg = QEMGEntity.eMGEntity;

    @Override
    public List<EMG> searchIdAll(String uuid, Pageable pageable) {
        List<EMGEntity> list = queryFactory
                .select(emg)
                .from(emg)
                .where(emg.emgUserUUID.eq(uuid))
                .orderBy(emg.emgCancel.asc(),emg.emgCancelAt.desc(), emg.emgCreatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<EMG> emgs = new ArrayList<>();
        for( EMGEntity emgEntity : list){
            emgs.add(emgEntity.toDto());
        }
        return emgs;
    }

    @Override
    public int selectCountId(String uuid) {
        return (int)queryFactory
                .selectFrom(emg)
                .where(emg.emgUserUUID.eq(uuid))
                .fetchCount();
    }
}
