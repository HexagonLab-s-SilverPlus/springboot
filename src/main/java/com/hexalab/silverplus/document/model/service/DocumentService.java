package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.common.Paging;
import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import com.hexalab.silverplus.document.jpa.repository.DocumentRepository;
import com.hexalab.silverplus.document.model.dto.DocFile;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.notice.jpa.entity.NoticeEntity;
import com.hexalab.silverplus.notice.model.dto.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;


import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocFileService docFileService;

    @Transactional
    public Document saveDocument(Document document) {
        document.setIsApproved(document.getIsApproved() != null ? document.getIsApproved() : "제출전");
        DocumentEntity entity = document.toEntity();
        DocumentEntity savedEntity = documentRepository.save(entity);
        log.info("문서 메타 정보 저장: {}", savedEntity);
        return savedEntity.toDto();
    }

    public Document getDocumentById(String docId) {
        Optional<DocumentEntity> documentOpt = documentRepository.findById(docId);
        return documentOpt.map(DocumentEntity::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
    }

    /**
     * memUuid로 노인사용자가 작성한 Document 리스트 조회
     * @param memUuid
     * @return
     */
    public List<Document> getDocumentsByMemUuid(String memUuid) {
        List<DocumentEntity> documentEntities = documentRepository.findByWrittenBy(memUuid);
        return documentEntities.stream()
                .map(DocumentEntity::toDto)
                .toList();
    }



    public List<Map<String, Object>> getDocumentsWithFiles(String memUuid){
        List<Document> documents=getDocumentsByMemUuid(memUuid);
        return documents.stream()
                .map(document -> {
                    DocFile docFile=docFileService.getDocFilesByDocId(document.getDocId());
                    return Map.of(
                            "document", document,
                            "file", docFile
                    );
                })
                .toList();
    }



//    @Transactional
//    public Document updateDocumentStatus(String docId, String newStatus) {
//        DocumentEntity entity = documentRepository.findById(docId)
//                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
//        entity.setDocStatus(newStatus);
//        DocumentEntity updatedEntity = documentRepository.save(entity);
//        log.info("문서 상태 업데이트: {}", updatedEntity);
//        return updatedEntity.toDto();
//    }


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
    public Document submitDocumentToMgr(String docId) {
        DocumentEntity entity = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        entity.setIsApproved("대기중"); // 제출전 -> 대기중
        entity.setSubmittedAt(new Timestamp(System.currentTimeMillis())); // 현재 Timestamp 를 제출날짜로 설정
        documentRepository.save(entity);
        return entity.toDto();
    }


    private final EntityManager entityManager;
    @Transactional
    //어르신관리에서 공문서 조회(컴포넌트)
    public List<Map<String, Object>> getDocumentsWithFiles2(String memUuid, String status, Paging paging) {
        // 문서 목록을 사용자별로 가져오기
        List<Document> documents = getDocumentsByMemUuid(memUuid);

        if (status != null && !status.trim().isEmpty()) {
            // 상태 값에 따라 필터링 (대기중, 승인, 반려)
            documents = documents.stream()
                    .filter(doc -> doc.getIsApproved() != null)
                    .filter(doc -> doc.getIsApproved().equals(status))
                    .collect(Collectors.toList());
        }

        List<Document> pagedDocuments = documents.subList(paging.getStartRow() - 1, Math.min(paging.getEndRow(), documents.size()));
        log.info("Filtered Documents by status '{}': {}", status, documents);
        return pagedDocuments.stream()
                .map(document -> {
                    DocFile docFile = docFileService.getDocFilesByDocId(document.getDocId());

                    return Map.of(
                            "document", document,
                            "file", docFile
                    );
                })
                .collect(Collectors.toList());
    }
    //어르신관리 공문서 상태 처리 (승인, 반려)
    @Transactional
    public void mupdateDocumentStatus(String docId, String status, String approvedBy, Timestamp approvedAt) {

        DocumentEntity documentEntity = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없음: " + docId));

        documentEntity.setIsApproved(status);  // 문서의 상태를 업데이트
        documentEntity.setApprovedBy(approvedBy);  // 승인자 UUID를 설정
        documentEntity.setApprovedAt(approvedAt);  // 승인 시각을 설정

        documentRepository.save(documentEntity);  // 문서 정보를 저장
    }

    //어르신관리 공문서 상태 처리 (승인, 반려) - 페이징 처리
    @Transactional
    public Integer selectDocListCountByAction(String memUuid, String action) {
        if (memUuid == null || action == null) {
            throw new IllegalArgumentException("memUuid와 action은 null일 수 없습니다.");
        }

        // Native Query with Positional Parameters
        String sql = "SELECT COUNT(*) FROM document d WHERE d.written_by = ? AND d.is_approved = ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, memUuid);
        query.setParameter(2, action);

        return ((Number) query.getSingleResult()).intValue();
    }

    //관리중인 어르신의 공문서 목록
    public Page<Document> getPendingDocuments(String approvedBy, Pageable pageable) {
        return documentRepository.findByIsApprovedAndApprovedBy("대기중", approvedBy, pageable)
                .map(DocumentEntity::toDto);
    }

    //공문서 요청 카운트
    public long countPendingDocuments(String approvedBy) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        return documentRepository.countByIsApprovedAndApprovedBy("대기중", approvedBy);
    }


}
