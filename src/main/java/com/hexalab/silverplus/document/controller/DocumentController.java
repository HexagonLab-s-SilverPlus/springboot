package com.hexalab.silverplus.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.Paging;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.document.model.dto.DocFile;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.document.model.service.DocumentService;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.map;

@Slf4j
@RestController
@RequestMapping("/api/document")
@CrossOrigin(origins = "*") // CORS 설정 (보안을 위해 필요한 대로 설정하기)
public class DocumentController {
    @Autowired
    private DocumentService documentService;
    @Autowired
    private MemberService memberService;


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
     * 사용자 UUID로 작성된 문서 목록 조회
     *
     * @param memUuid 노인 사용자 UUID
     * @return 문서 목록
     */
    @GetMapping("/doc/{memUuid}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByUser(
            @PathVariable String memUuid) {
        try{
            List<Document> documents = documentService.getDocumentsByMemUuid(memUuid);
            return ResponseEntity.ok(
                    ApiResponse.<List<Document>>builder()
                            .success(true)
                            .message("문서 목록 조회 성공")
                            .data(documents)
                            .build()
            );
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<Document>>builder()
                           .success(false)
                           .message("문서 목록 조회 실패")
                           .build()
                    );
        }
    }


    /**
     * 사용자 UUID로 작성된 문서와 파일 목록 조회
     *
     * @param memUuid 노인 사용자 UUID
     * @return 문서 및 파일 목록
     */
    @GetMapping("/{memUuid}/with-files")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDocumentsWithFiles(
            @PathVariable String memUuid) {
        try{
            List<Map<String, Object>> documentsWithFiles = documentService.getDocumentsWithFiles(memUuid);

            // JSON 직렬화로 보기 좋게 출력
            ObjectMapper objectMapper = new ObjectMapper();

            log.info("Documents with files: {}", objectMapper.writeValueAsString(documentsWithFiles));


            return ResponseEntity.ok(
                    ApiResponse.<List<Map<String, Object>>>builder()
                            .success(true)
                            .message("문서와 파일 목록 조회 성공")
                            .data(documentsWithFiles)
                            .build()
            );

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<Map<String, Object>>>builder()
                            .success(false)
                            .message("문서와 파일 목록 조회 실패")
                            .build()
            );
        }
    }


    /**
     * memUuid로 Member 객체 조회
     * @param memUuid
     * @return
     */
    @GetMapping("/mgrName/{memUuid}")
    public ResponseEntity<ApiResponse<Member>> getMemberByUUID(@PathVariable String memUuid) {
        try{
            Member member = memberService.selectMember(memUuid);
            return ResponseEntity.ok(
                    ApiResponse.<Member>builder()
                            .success(true)
                            .message("멤버 조회 성공")
                            .data(member)
                            .build()
            );
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Member>builder()
                           .success(false)
                           .message("멤버 조회 실패")
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
    public ResponseEntity<ApiResponse<Document>> submitDocumentToMgr(
            @PathVariable String docId) {
        try{
            Document document = documentService.submitDocumentToMgr(docId);
            return ResponseEntity.ok(
                    ApiResponse.<Document>builder()
                            .success(true)
                            .message("Document sent to approver successfully.")
                            .data(document)
                            .build()
            );

        }catch(Exception e){
            log.error("Error submitting document to manager");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Document>builder()
                           .success(false)
                           .message("Document submission failed")
                           .build()
            );
        }
    }













    /**
     * '대기중' 상태 문서 조회 (페이징)
     * @param page 현재 페이지
     * @param size 페이지 크기 (기본값: 10)
     * @param mgrUUID 로그인한 사용자의 UUID
     * @return ApiResponse<Page<Document>>
     */
    @GetMapping("/pending")
    public ApiResponse<Page<Document>> getPendingDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String mgrUUID
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Document> pendingDocuments = documentService.getPendingDocuments(mgrUUID, pageable);
            return ApiResponse.<Page<Document>>builder()
                    .success(true)
                    .message("대기중 문서 조회 성공")
                    .data(pendingDocuments)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error fetching pending documents: {}", e.getMessage(), e);
            return ApiResponse.<Page<Document>>builder()
                    .success(false)
                    .message("대기중 문서 조회 실패")
                    .build();
        }
    }











    @GetMapping("/{memUuid}/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDocManagedList(
            @PathVariable String memUuid,   //senior uuid
            @RequestParam(required = false) String action, // status가 전달되지 않으면 대기중 상태로 기본값 설정
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer listCount

    ) {
        try {
            log.info("Fetching documents for memUuid: {}, status: {}", memUuid, action);
            log.info("Received pageNumber: {}", pageNumber);  // pageNumber 로그 찍기
            log.info("Received pageSize: {}", pageSize);
            log.info("Received listCount: {}", listCount);

            // status가 null 또는 빈 값이면 대기중으로 기본값 설정
            if (action == null || action.trim().isEmpty()) {
                action = "값없음";
            }

            //총 갯수 조회
            listCount = documentService.selectDocListCountByAction(memUuid, action);
            if (listCount == null || listCount < 1) {
                listCount = 1;
            } else {
                listCount = listCount;
            }
            log.info("selectDocListCountByAction: {}", listCount);

            // 기본값 설정 (null일 경우)
            pageNumber = pageNumber == null ? 1 : pageNumber;
            pageSize = pageSize == null ? 5 : pageSize;
//            listCount = listCount == null ? 0 : listCount;
            log.info("pageNumber: {}", pageNumber);
            log.info("pageSize: {}", pageSize);
            log.info("listCount: {}", listCount);

            //Paging 객체를 생성하고 페이징 계산
            Paging paging = new Paging(listCount, pageSize, pageNumber);
            paging.calculate(); // 페이지 계산
            log.info("Paging after calculate: " + paging);

            // 상태에 맞는 문서만 필터링하고, 페이징 처리된 결과 반환
            List<Map<String, Object>> documentsWithFiles = documentService.getDocumentsWithFiles2(memUuid, action, paging);

            log.info("Documents with status '{}': {}", action, documentsWithFiles);

            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("action", action);                // 상태
            response.put("listCount", listCount);          // 총 데이터 개수
            response.put("pageNumber", paging.getCurrentPage()); // 현재 페이지
            response.put("pageSize", pageSize);            // 페이지 크기
            response.put("startPage", paging.getStartPage());    // 시작 페이지
            response.put("endPage", paging.getEndPage());        // 끝 페이지
            response.put("maxPage", paging.getMaxPage());        // 최대 페이지
            response.put("documents", documentsWithFiles);       // 문서 데이터

            // JSON 직렬화로 보기 좋게 출력
//            ObjectMapper objectMapper = new ObjectMapper();
//            log.info("Documents with files: {}", objectMapper.writeValueAsString(documentsWithFiles));

            return ResponseEntity.ok(
                    ApiResponse.<Map<String, Object>>builder()
                            .success(true)
                            .message("공문서 목록 조회 성공")
                            .data(response)
                            .build()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("공문서 목록 조회 실패")
                            .build()
            );
        }
    }



    // 문서 상태 업데이트 (승인 또는 반려)
    // 문서 상태 업데이트 (승인 또는 반려)
    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDocument(
            @PathVariable String docId,
            @RequestParam String status, // "승인" 또는 "반려"
            @RequestParam(required = false) String approvedBy, // 승인자 UUID
            @RequestParam(required = false) String approvedAt // 승인 시각
    ) {
        try {
            // `approvedAt` 값이 ISO 8601 형식이라면 이를 Timestamp 형식으로 변환
            if (approvedAt != null && !approvedAt.isEmpty()) {
                // 예시: "2025-01-09T10:30:00Z" => "2025-01-09 10:30:00"
                String formattedDate = approvedAt.replace("T", " ").substring(0, 19); // "yyyy-MM-dd HH:mm:ss" 형식으로 변경
                Timestamp timestamp = Timestamp.valueOf(formattedDate);

                // 문서 상태 업데이트
                documentService.mupdateDocumentStatus(docId, status, approvedBy, timestamp);

                return ResponseEntity.ok(
                        ApiResponse.<Void>builder()
                                .success(true)
                                .message("문서 상태 업데이트 성공")
                                .build()
                );
            } else {
                throw new IllegalArgumentException("approvedAt 값이 필요합니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message("문서 상태 업데이트 실패")
                            .build()
            );
        }
    }










}
