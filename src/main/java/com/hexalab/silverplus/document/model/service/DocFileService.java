package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import com.hexalab.silverplus.document.jpa.repository.DocFileRepository;
import com.hexalab.silverplus.document.model.dto.DocFile;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DocFileService {
    @Autowired
    private final DocFileRepository docFileRepository;
    @Autowired
    private FTPUtility ftPUtility;

    @Value("${ftp.server}")
    private String ftpServer;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.remote-dir}")
    private String ftpRemoteDir;

    @Transactional
    public DocFile saveDocFile(String docId, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String renamedFilename = UUID.randomUUID() + "_" + originalFilename;

        // NAS 업로드
        try {
            ftPUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);
            String remoteFilePath = ftpRemoteDir + renamedFilename;
            ftPUtility.uploadFile(file.getInputStream().toString(), remoteFilePath);
            log.info("NAS에 파일 업로드 완료: {}", remoteFilePath);
        } finally {
            ftPUtility.close();
        }

        // DB 저장
        DocFileEntity docFileEntity = DocFileEntity.builder()
                .dfId(UUID.randomUUID().toString())
                .dfOriginalName(originalFilename)
                .dfRename(renamedFilename)
                .docId(docId)
                .build();
        DocFileEntity savedEntity = docFileRepository.save(docFileEntity);
        log.info("파일 정보 저장 완료: {}", savedEntity);
        return savedEntity.toDto();
    }

    public DocFile getDocFileById(String dfId) {
        DocFileEntity entity = docFileRepository.findById(dfId)
                .orElseThrow(() -> new IllegalArgumentException("DocFile not found"));
        return entity.toDto();
    }

    @Transactional
    public void deleteDocFileById(String dfId) {
        if (!docFileRepository.existsById(dfId)) {
            throw new IllegalArgumentException("DocFile not found");
        }
        docFileRepository.deleteById(dfId);
        log.info("파일 삭제 완료: {}", dfId);
    }
}
