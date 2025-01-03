package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import com.hexalab.silverplus.document.jpa.repository.DocFileRepository;
import com.hexalab.silverplus.document.model.dto.DocFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@DynamicUpdate
public class DocFileService {

    private final DocFileRepository docFileRepository;

    /**
     * 파일 저장
     */
    @Transactional
    public DocFile saveDocFile(DocFile docFile) {
        DocFileEntity entity = docFile.toEntity();
        DocFileEntity savedEntity = docFileRepository.save(entity);
        log.info("DocFile saved: {}", savedEntity);
        return savedEntity.toDto();
    }

    /**
     * 파일 조회
     */
    public DocFile getDocFileById(String dfId) {
        Optional<DocFileEntity> docFileOpt = docFileRepository.findById(dfId);
        if (docFileOpt.isPresent()) {
            DocFileEntity entity = docFileOpt.get();
            log.info("DocFile retrieved: {}", entity);
            return entity.toDto();
        } else {
            log.warn("DocFile not found for ID: {}", dfId);
            throw new IllegalArgumentException("DocFile not found");
        }
    }

    /**
     * 파일 삭제
     */
    @Transactional
    public void deleteDocFileById(String dfId) {
        if (docFileRepository.existsById(dfId)) {
            docFileRepository.deleteById(dfId);
            log.info("DocFile deleted: {}", dfId);
        } else {
            log.warn("DocFile not found for ID: {}", dfId);
            throw new IllegalArgumentException("DocFile not found");
        }
    }
}
