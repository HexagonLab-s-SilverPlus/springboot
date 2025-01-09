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

    public List<Map<String, Object>> getCustomDocumentList(int pageNumber, int pageSize) {
        // Native Query를 사용해 필요한 컬럼만 선택
        String sql = "SELECT d.DOC_ID, d.DOC_TYPE, d.WRITTEN_BY, d.DOC_COMPLETED_AT, m.MEM_UUID, m.MEM_NAME " +
                "FROM DOCUMENT d " +
                "LEFT JOIN MEMBER m ON d.WRITTEN_BY = m.MEM_UUID";
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
            document.put("memberUuid", row[4]);
            document.put("memberName", row[5] != null ? row[5] : "Unknown");
            documents.add(document);
            log.warn("Row: " + Arrays.toString(row));
            log.warn("Document Data:" + document);


        }
        return documents;
    }

    private ArrayList<Document> toList(Page<DocumentEntity> entityList) {
        //컨트롤러로 리턴할 ArrayList<Notice> 타입으로 변경 처리 필요함
        ArrayList<Document> list = new ArrayList<>();
        //Page 안의 NoticeEntity 를 Notice 로 변환해서 리스트에 추가 처리함
        for(DocumentEntity entity : entityList){
            list.add(entity.toDto());
        }
        return list;
    }

    public Page<Document> selectList(Pageable pageable) {
        return documentRepository.findAll(pageable).map(DocumentEntity::toDto);
    }


    public int dselectListCount() {
        return (int) documentRepository.count();
    }


    public List<Map<String, Object>> getDocumentsWithFiles2(String memUuid, String status, Paging paging) {
        // 문서 목록을 사용자별로 가져오기
        List<Document> documents = getDocumentsByMemUuid(memUuid);

        // status 값이 존재하고 공백이 아니면 해당 상태로 필터링
        if (status != null && !status.trim().isEmpty()) {
            // 상태 값에 따라 필터링 (대기중, 승인, 반려)
            documents = documents.stream()
                    .filter(doc -> doc.getIsApproved() != null) // 상태가 null이 아니어야 필터링
                    .filter(doc -> doc.getIsApproved().equals(status)) // 전달된 상태에 맞는 문서만 필터링
                    .collect(Collectors.toList());
        }

        // 페이징 처리: startRow, endRow에 해당하는 데이터만 추출
        List<Document> pagedDocuments = documents.subList(paging.getStartRow() - 1, Math.min(paging.getEndRow(), documents.size()));


        log.info("Filtered Documents by status '{}': {}", status, documents);


        // 파일과 문서 정보를 함께 반환
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


    @Transactional
    public void mupdateDocumentStatus(String docId, String status, String approvedBy, Timestamp approvedAt) {
        // 1. DocumentEntity를 가져옵니다.
        DocumentEntity documentEntity = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다: " + docId));

        // 2. 상태를 업데이트합니다.
        documentEntity.setIsApproved(status);  // 문서의 상태를 업데이트
        documentEntity.setApprovedBy(approvedBy);  // 승인자 UUID를 설정
        documentEntity.setApprovedAt(approvedAt);  // 승인 시각을 설정

        // 3. 엔티티를 데이터베이스에 저장합니다.
        documentRepository.save(documentEntity);  // 문서 정보를 저장
    }

    public Integer selectDocListCountByAction(String memUuid, String action) {
        if (memUuid == null || action == null) {
            throw new IllegalArgumentException("memUuid와 action은 null일 수 없습니다.");
        }

        // Native Query with Positional Parameters
        String sql = "SELECT COUNT(*) FROM document d WHERE d.written_by = ? AND d.is_approved = ?";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, memUuid); // 위치 기반 1번 파라미터
        query.setParameter(2, action); // 위치 기반 2번 파라미터

        return ((Number) query.getSingleResult()).intValue();
    }

}
