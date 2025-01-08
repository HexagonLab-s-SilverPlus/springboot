package com.hexalab.silverplus.document.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.document.model.dto.DocFile;
import com.hexalab.silverplus.document.model.service.DocFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/doc-files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DocFileController {

    private final DocFileService docFileService;



    // file upload path valiable
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
     * FTP를 통해 파일 다운로드
     *
     * @param fileName 다운로드할 파일명
     * @return 파일 바이너리 데이터
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFileFromFTP(@PathVariable String fileName) {
        try (FTPUtility ftpUtility = new FTPUtility()) {
            // FTP 서버 연결
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            // 로컬 임시 파일 생성
            File tempFile = File.createTempFile("ftp_download_", "_" + fileName);

            // FTP에서 파일 다운로드
            ftpUtility.downloadFile(ftpRemoteDir + "document/" + fileName, tempFile.getAbsolutePath());

            // 파일 데이터를 Resource로 변환
            Resource resource = new InputStreamResource(new FileInputStream(tempFile));

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // 임시 파일 삭제
            tempFile.delete();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            log.error("FTP 파일 다운로드 실패: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
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
