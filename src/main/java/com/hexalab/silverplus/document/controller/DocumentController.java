package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.document.model.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
@RequiredArgsConstructor    // 자동 객체 생성(의존성 주입)
public class DocumentController {
    @Autowired
    private DocumentService documentService;


    /**
     * 공문서 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Document>> saveDocument(@RequestBody Document document,
                                                              @RequestParam("file")MultipartFile file) {
        try{
            // 1. MultipartFile을 임시 파일로 저장
            File localFile = new File(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(file.getOriginalFilename()));
            file.transferTo(localFile);

            // 2. Document 저장 및 업로드
            Document savedDocument = documentService.saveDocument(document, localFile);

            // 3. 응답 반환
            return ResponseEntity.ok(
                    ApiResponse.<Document>builder()
                            .success(true)
                            .message("문서 저장 및 파일 업로드를 성공했습니다.")
                            .data(savedDocument)
                            .build()
            );


        }catch(IOException e){
            e.printStackTrace();
            log.error("File processing error: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    ApiResponse.<Document>builder()
                            .success(false)
                            .message("문서 저장 및 파일 업로드를 실패했습니다.")
                            .build()
            );
        }
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
}
