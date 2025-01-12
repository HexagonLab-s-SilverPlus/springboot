package com.hexalab.silverplus.medical.jpa.repository;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicalRepositoryCustom {
    List<MedicalEntity> selectAllMedicalList(String mediSnrUUID, Pageable pageable);
    int selectAllCount(String mediSnrUUID);
    int updateMedicalPrivacy(String mediSnrUUID, String mediPrivacy);
    String selectMedicalStatus(String mediSnrUUID);
}//MedicalRepositoryCustom end
