package com.hexalab.silverplus.medical.jpa.repository;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;

import java.util.List;

public interface MedicalRepositoryCustom {
    List<MedicalEntity> selectAllMedicalList(String mediSnrUUID);
}//MedicalRepositoryCustom end
