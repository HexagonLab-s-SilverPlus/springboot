package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import com.hexalab.silverplus.document.jpa.repository.DocumentRepository;
import com.hexalab.silverplus.document.model.dto.Document;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor    //매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
@DynamicUpdate // 변경된 필드만 업데이트하도록 설정
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    /**
     * 공문서 저장
     */
    @Transactional
    public Document saveDocument(Document document) {
        DocumentEntity entity = document.toEntity();
        DocumentEntity savedEntity = documentRepository.save(entity);
        log.info("Document saved: {}", savedEntity);
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
//            entity.setDocStatus(newStatus);
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


        private final EntityManager entityManager;
        @Transactional

        public List<Map<String, Object>> getCustomDocumentList() {
            // Native Query를 사용해 필요한 컬럼만 선택
            String sql = "SELECT DOC_ID, DOC_TYPE, WRITTEN_BY, DOC_COMPLETED_AT AS CREATE_AT FROM DOCUMENT";
            Query query = entityManager.createNativeQuery(sql);

            // 결과를 Map 형식으로 변환
            List<Object[]> result = query.getResultList();
            List<Map<String, Object>> documents = new ArrayList<>();
            for (Object[] row : result) {
                Map<String, Object> document = new HashMap<>();
                document.put("docId", row[0]);
                document.put("docType", row[1]);
                document.put("writtenBy", row[2]);
                document.put("createAt", row[3]);
                documents.add(document);
            }
            return documents;
        }

}
