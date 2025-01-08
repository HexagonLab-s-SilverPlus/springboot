package com.hexalab.silverplus.document.model.service;

import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import com.hexalab.silverplus.document.jpa.repository.DocumentRepository;
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

import java.util.*;


import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional
    public Document saveDocument(Document document) {
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
    public void sendDocumentToApprover(String docId, String approverId) {
        DocumentEntity entity = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));
        entity.setApprovedBy(approverId);
        documentRepository.save(entity);
        log.info("문서 담당자 전송 완료: {}", approverId);
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
}
