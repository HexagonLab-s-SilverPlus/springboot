package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.model.dto.DocFile;
import com.hexalab.silverplus.document.model.service.DocFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/doc-files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocFileController {

    private final DocFileService docFileService;

    /**
     * 파일 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DocFile>> saveDocFile(@RequestBody DocFile docFile) {
        DocFile savedDocFile = docFileService.saveDocFile(docFile);
        return ResponseEntity.ok(
                ApiResponse.<DocFile>builder()
                        .success(true)
                        .message("DocFile saved successfully.")
                        .data(savedDocFile)
                        .build()
        );
    }



    /**
     * 파일 조회
     */
    @GetMapping("/{dfId}")
    public ResponseEntity<ApiResponse<DocFile>> getDocFileById(@PathVariable String dfId) {
        DocFile docFile = docFileService.getDocFileById(dfId);
        return ResponseEntity.ok(
                ApiResponse.<DocFile>builder()
                        .success(true)
                        .message("DocFile retrieved successfully.")
                        .data(docFile)
                        .build()
        );
    }

    /**
     * 파일 삭제
     */
    @DeleteMapping("/{dfId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocFileById(@PathVariable String dfId) {
        docFileService.deleteDocFileById(dfId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("DocFile deleted successfully.")
                        .build()
        );
    }



}
