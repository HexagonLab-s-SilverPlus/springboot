package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import com.hexalab.silverplus.document.jpa.repository.DocumentRepository;
import com.hexalab.silverplus.document.model.dto.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor    //매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
@DynamicUpdate // 변경된 필드만 업데이트하도록 설정
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

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

    /**
     * 공문서 저장 및 NAS 업로드
     */
    @Transactional
    public Document saveDocument(Document document, File file ) {
        // 1. 문서 엔터티 저장
        DocumentEntity entity = document.toEntity();
        DocumentEntity savedEntity = documentRepository.save(entity);

        // 2. NAS FTP Upload
        try(FTPUtility ftpUtility=new FTPUtility()){
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);
            String remoteFilePath=ftpRemoteDir+file.getName();
            ftpUtility.uploadFile(file.getAbsolutePath(), remoteFilePath);
            log.info("나스에 파일이 성공적으로 업로드됨: {}", remoteFilePath);
        }catch(IOException e){
            log.error("나스에 파일 업로드하는게 실패:{}", e.getMessage());
            throw new RuntimeException("FTP upload failed", e);
        }

        return savedEntity.toDto();
    }


    /**
     * 공문서 조회
     */
    public Document getDocumentById(String docId) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(docId);
        if (documentOpt.isPresent()) {
            DocumentEntity entity = documentOpt.get();
            log.info("Document retrieved: {}", entity);
            return entity.toDto();
        } else {
            log.warn("Document not found for ID: {}", docId);
            throw new IllegalArgumentException("Document not found");
        }
    }


    /**
     * 공문서 상태 업데이트(승인, 반려)
     */
    @Transactional
    public Document updateDocumentStatus(String docId, String newStatus) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(docId);
        if (documentOpt.isPresent()) {
            DocumentEntity entity = documentOpt.get();
            entity.setDocStatus(newStatus);
            DocumentEntity updatedEntity = documentRepository.save(entity);
            log.info("Document status updated: {}", updatedEntity);
            return updatedEntity.toDto();
        } else {
            log.warn("Document not found for ID: {}", docId);
            throw new IllegalArgumentException("Document not found");
        }
    }

    /**
     * 담당자에게 공문서 전송
     */
    @Transactional
    public void sendDocumentToApprover(String docId, String approverId) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(docId);
        if (documentOpt.isPresent()) {
            DocumentEntity document = documentOpt.get();
            document.setApprovedBy(approverId);
            documentRepository.save(document);
            log.info("Document sent to approver: {}", approverId);
        } else {
            log.warn("Document not found for ID: {}", docId);
            throw new IllegalArgumentException("Document not found.");
        }
    }

}
