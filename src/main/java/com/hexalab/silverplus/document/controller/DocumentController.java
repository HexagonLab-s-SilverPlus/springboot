package com.hexalab.silverplus.document.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.common.Paging;
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
            Member member = memberService.findByMemUUID(memUuid);
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


    // 수진 작업
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }
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


        List<Map<String, Object>> document = documentService.getCustomDocumentList(currentPage, limit);


        Page<Document> page = documentService.selectList(pageable);
        List<Document> list = page.getContent();
        System.out.println("Data for current page: " + list);

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("list", document);
        response.put("paging", Map.of(
                "currentPage", page.getNumber() + 1,
                "listCount", (int) page.getTotalElements(),
                "pageSize", page.getSize(),
                "totalPages", page.getTotalPages()
        ));


        return response;
    }

//    //어르신 관리 뷰에서 공문서 승인 반려 처리
//    @GetMapping("/document/{memUUID}")
//    public ResponseEntity<Map> docManagedList(@PathVariable String memUUID
////            @PathVariable("docId") String docId
//    ){
//        log.info("Received docId: " + memUUID);
//        Map<String,Object> map = new HashMap<>();
//        List<Map<String, Object>> fileList = new ArrayList<>();
//
//        try{
//            //공문서 요청목록 불러오기
//            Document document = documentService.getDocumentById(memUUID);
//            log.info("doc : " + document);
//            map.put("doc", document);
//
//            //공문서 갯수 확인
////            ArrayList<DocFile> docFiles = new ArrayList<>();
////            int fileCount = documentfile
//            return ResponseEntity.ok(map);
//        }catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    //공문서 파일 다운로드 및 승인 반려 처리
    @GetMapping("/{memUuid}/request")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDocManagedList(
            @PathVariable String memUuid) {
        try{
            log.info("Fetching documents for memUuid: {}", memUuid);
            // 대기중 상태의 문서만 필터링해서 반환
            List<Map<String, Object>> documentsWithFiles = documentService.getDocumentsWithFiles2(memUuid, "대기중");
            log.info("Documents with status '대기중': {}", documentsWithFiles); // 여기서 필터링된 결과를 확인

            // JSON 직렬화로 보기 좋게 출력
            ObjectMapper objectMapper = new ObjectMapper();

            log.info("Documents with files: {}", objectMapper.writeValueAsString(documentsWithFiles));


            return ResponseEntity.ok(
                    ApiResponse.<List<Map<String, Object>>>builder()
                            .success(true)
                            .message("공문서 목록 조회 성공")
                            .data(documentsWithFiles)
                            .build()
            );

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<Map<String, Object>>>builder()
                            .success(false)
                            .message("공문서와 목록 조회 실패")
                            .build()
            );
        }
    }

    @PutMapping("/{docId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDocument(
            @PathVariable String docId,
            @RequestParam String status // "승인" 또는 "반려"
    ) {
        try {
            documentService.updateDocumentStatus(docId, status);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message("문서 상태 업데이트 성공")
                            .build()
            );
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
