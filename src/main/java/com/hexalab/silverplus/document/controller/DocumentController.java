package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.Paging;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.document.model.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
public class DocumentController {
    @Autowired
    private DocumentService documentService;


    /**
     * 공문서 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Document>> saveDocument(@RequestBody Document document) {
        Document savedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(
                ApiResponse.<Document>builder()
                        .success(true)
                        .message("Document saved successfully.")
                        .data(savedDocument)
                        .build()
        );
    }

    /**
     * 공문서 조회
     */
    @GetMapping("/{docId}")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable String docId) {
        Document document = documentService.getDocumentById(docId);
        return ResponseEntity.ok(
                ApiResponse.<Document>builder()
                        .success(true)
                        .message("Document retrieved successfully.")
                        .data(document)
                        .build()
        );
    }

    /**
     * 공문서 상태 업데이트
     */
    @PatchMapping("/{docId}/status")
    public ResponseEntity<ApiResponse<Document>> updateDocumentStatus(
            @PathVariable String docId,
            @RequestParam String newStatus) {
        Document updatedDocument = documentService.updateDocumentStatus(docId, newStatus);
        return ResponseEntity.ok(
                ApiResponse.<Document>builder()
                        .success(true)
                        .message("Document status updated successfully.")
                        .data(updatedDocument)
                        .build()
        );
    }

    /**
     * 담당자에게 공문서 전송
     */
    @PostMapping("/{docId}/send")
    public ResponseEntity<ApiResponse<Void>> sendDocumentToApprover(
            @PathVariable String docId,
            @RequestParam String approverId) {
        documentService.sendDocumentToApprover(docId, approverId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Document sent to approver successfully.")
                        .build()
        );
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> documentList() {
        List<Map<String, Object>> list = documentService.getCustomDocumentList();
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        return ResponseEntity.ok(map);
    }

}
