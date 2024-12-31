package com.hexalab.silverplus.program.model.service;

import com.hexalab.silverplus.program.jpa.entity.ProgramFileEntity;
import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepository;
import com.hexalab.silverplus.program.jpa.repository.ProgramFileRepositoryCustom;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProgramFileService {
    private final ProgramFileRepository programFileRepository;

    public int insertProgramFile(ProgramFile programFile) {
        try {
            programFileRepository.save(programFile.toEntity());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }//insertProgramFile end

    public ArrayList<ProgramFile> selectProgramFiles(String snrProgramId) {
        return programFileRepository.findBySnrProgramId(snrProgramId)
                .stream().map(ProgramFileEntity::toDto)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int deleteProgramFile(String snrFileId) {
        try {
            programFileRepository.deleteById(snrFileId);
            log.info("ProgramFile deleted successfully: {}", snrFileId);
            return 1; // 성공
        } catch (Exception e) {
            log.error("Error deleting ProgramFile with ID: {}", snrFileId, e);
            return 0; // 실패
        }
    }

    public ProgramFile selectProgramFile(String snrFileId) {
        try {
            log.info("Fetching ProgramFile with ID: {}", snrFileId);

            // 데이터베이스에서 파일 ID로 엔티티 조회
            Optional<ProgramFileEntity> optionalEntity = programFileRepository.findById(snrFileId);
            if (optionalEntity.isEmpty()) {
                log.warn("No ProgramFile found for ID: {}", snrFileId);
                return null; // 파일 없음
            }

            // 조회된 엔티티를 DTO로 변환하여 반환
            ProgramFileEntity entity = optionalEntity.get();
            return entity.toDto();
        } catch (Exception e) {
            log.error("Error fetching ProgramFile with ID: {}", snrFileId, e);
            throw new RuntimeException("파일 조회 중 오류가 발생했습니다.", e);
        }
    }

}//ProgramFileService end
