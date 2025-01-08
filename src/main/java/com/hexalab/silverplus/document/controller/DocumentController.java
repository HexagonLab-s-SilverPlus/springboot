package com.hexalab.silverplus.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.Paging;
import com.hexalab.silverplus.document.model.dto.Document;
import com.hexalab.silverplus.document.model.service.DocumentService;
import com.hexalab.silverplus.member.model.service.MemberService;
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


    // 수진 작업
    @GetMapping()
    public Map<String, Object> documentList(
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", defaultValue = "10") int limit)
    {
        // 받은 값을 로그로 확인
        System.out.println("Received pageNumber: " + currentPage);
        System.out.println("Received limit: " + limit);

        int listCount = documentService.dselectListCount();
        System.out.println("Total list count: " + listCount);

        Paging paging = new Paging(listCount, limit, currentPage);
        paging.calculate();
        System.out.println("Paging after calculate: " + paging);

        Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
                Sort.by(Sort.Direction.DESC, "docId"));
        System.out.println("Created Pageable: " + pageable);

        Page<Document> page = documentService.selectList(pageable);
        List<Document> list = page.getContent();
        System.out.println("Data for current page: " + list);

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("paging", Map.of(
                "currentPage", page.getNumber() + 1,
                "listCount", (int) page.getTotalElements(),
                "pageSize", page.getSize(),
                "totalPages", page.getTotalPages()
        ));

        return response;
    }




}
