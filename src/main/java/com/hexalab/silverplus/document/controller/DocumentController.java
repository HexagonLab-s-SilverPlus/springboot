package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.document.model.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 공문서 메타 정보 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Document>> saveDocument(@RequestBody Document document) {
        Document savedDocument = documentService.saveDocument(document);
        return ResponseEntity.ok(
                ApiResponse.<Document>builder()
                        .success(true)
                        .message("문서 메타 정보가 성공적으로 저장되었습니다.")
                        .data(savedDocument)
                        .build()
        );
    }

    /**
     * 공문서 메타 정보 조회
     */
    @GetMapping("/{docId}")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable String docId) {
        Document document = documentService.getDocumentById(docId);
        return ResponseEntity.ok(
                ApiResponse.<Document>builder()
                        .success(true)
                        .message("문서 메타 정보를 성공적으로 조회했습니다.")
                        .data(document)
                        .build()
        );
    }

    /**
     * 공문서 상태 업데이트
     */
//    @PatchMapping("/{docId}/status")
//    public ResponseEntity<ApiResponse<Document>> updateDocumentStatus(
//            @PathVariable String docId,
//            @RequestParam String newStatus) {
//        Document updatedDocument = documentService.updateDocumentStatus(docId, newStatus);
//        return ResponseEntity.ok(
//                ApiResponse.<Document>builder()
//                        .success(true)
//                        .message("문서 상태가 성공적으로 업데이트되었습니다.")
//                        .data(updatedDocument)
//                        .build()
//        );
//    }

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
                        .message("문서가 담당자에게 성공적으로 전송되었습니다.")
                        .build()
        );
    }
}
