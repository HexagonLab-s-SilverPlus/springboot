package com.hexalab.silverplus.medical.model.service;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;
import com.hexalab.silverplus.medical.jpa.repository.MedicalRepository;
import com.hexalab.silverplus.medical.model.dto.Medical;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalService {
    private final MedicalRepository medicalRepository;

    public ArrayList<Medical> selectAllMedicalList(@NotBlank String mediSnrUUID) {
        ArrayList<Medical> medicalList = new ArrayList<>();
        List<MedicalEntity> entityList = medicalRepository.selectAllMedicalList(mediSnrUUID);
        for (MedicalEntity entity : entityList) {
            medicalList.add(entity.toDto());
        }
        return medicalList;
    }

    public int insertMedical(Medical medical) {
        try {
            medicalRepository.save(medical.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    public int updateMedicalPrivacy(String mediSnrUUID, String mediPrivacy) {
        try {
            int updatedRows = medicalRepository.updateMedicalPrivacy(mediSnrUUID, mediPrivacy);
            return updatedRows;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    public Medical selectMedicalBymediId(String mediId) {
        return medicalRepository.findById(mediId).get().toDto();
    }

    public int updateMedical(Medical updatedMedical) {
        try {
            medicalRepository.save(updatedMedical.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }

    public int deleteMedicals(List<String> mediIds) {
        log.info("deleteMedicals mediIds = {}", mediIds);

        try {
            int deletedCount = 0;
            for (String mediId : mediIds) {
                if (medicalRepository.existsById(mediId)) {
                    medicalRepository.deleteById(mediId);
                    deletedCount++;
                } else {
                    log.info("Medical record not found for ID: {}", mediId);
                }
            }//for end

            log.info("Medical records deleted: {}", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }
}//MedicalService end
