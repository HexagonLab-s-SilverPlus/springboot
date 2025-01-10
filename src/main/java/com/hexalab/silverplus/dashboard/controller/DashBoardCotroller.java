package com.hexalab.silverplus.dashboard.controller;

import com.hexalab.silverplus.common.ApiResponse;
import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import com.hexalab.silverplus.dashboard.model.service.DashBoardService;
import com.hexalab.silverplus.document.model.service.DocumentService;
import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.*;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashBoardCotroller {
    private final DashBoardService dashBoardService;

    //등록
    @PostMapping
    public ResponseEntity dashboardInsert(
            @ModelAttribute DashBoard dashBoard) {
        log.info("Insert dashboard inserted" + dashBoard);
        try {
            // taskStatus를 'Y' 또는 'N'으로 변환
            String taskStatus = dashBoard.getTaskStatus();
            dashBoard.setTaskStatus("true".equalsIgnoreCase(taskStatus) ? "Y" : "N");

            dashBoard.setTaskId(UUID.randomUUID().toString());

            dashBoardService.insertDashboard(dashBoard);


            log.info("Insert dashboard inserted" + dashBoard);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

////    목록
//    @GetMapping("/date/{taskDate}")
//    public ResponseEntity<List<DashBoard>> getTodosByDate(@PathVariable Timestamp taskDate) {
//        log.info("Fetching todos for date: {}", taskDate);
//
//        List<DashBoard> tasks = dashBoardService.findByDate(taskDate);
//
//        if (!tasks.isEmpty()) {
//            return ResponseEntity.ok(tasks);
//        } else {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//        }
//    }

    //    TODO 전체목록 보기처리용
    @GetMapping
    public Map<String, Object> dashboardList() {

        //서비스 목록 조회 요청하고 결과 받기
        ArrayList<DashBoard> list = dashBoardService.selectList();

        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        return map;
    }

    //ToDo 삭제 요청
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteDashboard(@PathVariable String taskId) {
        log.info("Delete request received for taskId: {}", taskId);
        try {
            dashBoardService.deleteDashboard(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> DashboardUpdate(
            @PathVariable("id") String id,
            @RequestBody DashBoard dashBoard) { // @RequestBody 사용
        log.info("Update request received for Id: {}", id);


        // `taskDate`를 그대로 사용
        if (dashBoard.getTaskDate() == null) {
            return ResponseEntity.badRequest().build(); // taskDate가 없으면 잘못된 요청 처리
        }
        // PathVariable로 받은 ID를 DashBoard 객체에 설정
        dashBoard.setTaskId(id);

        // taskStatus 기본값 설정
        if (dashBoard.getTaskStatus() == null) {
            dashBoard.setTaskStatus("N");
        }

        try {
            int result = dashBoardService.updateDashBoard(dashBoard);
            if (result > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Update failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //공문서 목록 카운트
    private final DocumentService documentService;
    @GetMapping("/Count/{mgrUUID}")
    public ApiResponse<Long> getDocumentCount(@PathVariable String mgrUUID) {
        long count = documentService.countPendingDocuments(mgrUUID);
        log.info("Count 확인: {}", count);
        log.info("mgrUUID: {}", mgrUUID);

        return ApiResponse.<Long>builder()
                .success(true)
                .message("대기중 문서 개수 조회 성공")
                .data(count)
                .build();
    }
    @Autowired
    private MemberService memberService;

//    @GetMapping("/Countsnr/{memUUID}")
//    public ResponseEntity<Map<String, Object>> getSeniorCount(@PathVariable("memUUID") String memUUID) {
//        try {
//            log.info("Received memUUID: {}", memUUID); // memUUID 확인
//            int count = memberService.selectAllSeniorCount(memUUID);
//            log.info("Senior Count for {}: {}", memUUID, count); // 결과 확인
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("count", count);
//            result.put("message", "매니저가 관리하는 노인 수 조회 성공");
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Error fetching senior count for memUUID {}: {}", memUUID, e.getMessage());
//            Map<String, Object> errorResult = new HashMap<>();
//            errorResult.put("message", "매니저가 관리하는 노인 수 조회 실패");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
//        }
//    }






}//DashBoard End.

