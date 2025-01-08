package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.document.model.dto.DocFile;
import com.hexalab.silverplus.document.model.service.DocFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/doc-files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DocFileController {

    private final DocFileService docFileService;

    /**
     * 공문서 파일 저장
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DocFile>> saveDocFile(
            @RequestParam("docId") String docId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("csvFilename") String csvFilename // Flask에서 전송된 csvFilename
            ) {
        try {
            DocFile savedDocFile = docFileService.saveDocFile(docId, file, csvFilename);
            return ResponseEntity.ok(
                    ApiResponse.<DocFile>builder()
                            .success(true)
                            .message("문서 파일이 성공적으로 저장되었습니다.")
                            .data(savedDocFile)
                            .build()
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    ApiResponse.<DocFile>builder()
                            .success(false)
                            .message("문서 파일 저장 중 오류가 발생했습니다.")
                            .build()
            );
        }
    }


    /**
     * 문서 ID로 파일 조회
     *
     * @param docId 연결된 문서 ID
     * @return 파일 정보
     */
    @GetMapping("/document/{docId}")
    public ResponseEntity<ApiResponse<DocFile>> getDocFileByDocId(@PathVariable String docId) {
        try{
            DocFile docFile = docFileService.getDocFilesByDocId(docId);
            return ResponseEntity.ok(
                    ApiResponse.<DocFile>builder()
                            .success(true)
                            .message("파일 조회 성공")
                            .data(docFile)
                            .build()
            );

        }catch(Exception e){
            e.printStackTrace();
            log.error("파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body(
                    ApiResponse.<DocFile>builder()
                           .success(false)
                           .message("파일 조회 중 오류가 발생했습니다.")
                           .build()
            );
        }
    }

    /**
     * 공문서 파일 조회
     */
    @GetMapping("/{dfId}")
    public ResponseEntity<ApiResponse<DocFile>> getDocFileById(@PathVariable String dfId) {
        DocFile docFile = docFileService.getDocFileById(dfId);
        return ResponseEntity.ok(
                ApiResponse.<DocFile>builder()
                        .success(true)
                        .message("문서 파일 정보를 성공적으로 조회했습니다.")
                        .data(docFile)
                        .build()
        );
    }

    /**
     * 공문서 파일 삭제
     */
    @DeleteMapping("/{dfId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocFileById(@PathVariable String dfId) {
        docFileService.deleteDocFileById(dfId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("문서 파일이 성공적으로 삭제되었습니다.")
                        .build()
        );
    }
}
