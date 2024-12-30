package com.hexalab.silverplus.dashboard.controller;

import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import com.hexalab.silverplus.dashboard.model.service.DashBoardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    //TODO 등록 요청 처리용
    @PostMapping
    public ResponseEntity dashboardInsert(
            @ModelAttribute DashBoard dashBoard) {
        log.info("Insert dashboard inserted" + dashBoard);
        try{
        // taskStatus를 'Y' 또는 'N'으로 변환
        String taskStatus = dashBoard.getTaskStatus();
        dashBoard.setTaskStatus("true".equalsIgnoreCase(taskStatus) ? "Y" : "N");

        dashBoard.setTaskId(UUID.randomUUID().toString());

            dashBoardService.insertDashboard(dashBoard);


            log.info("Insert dashboard inserted" + dashBoard);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


    @GetMapping("/date/{taskDate}")
    public ResponseEntity<List<DashBoard>> getTodosByDate(@PathVariable Timestamp taskDate) {
        log.info("Fetching todos for date: {}", taskDate);

        List<DashBoard> tasks = dashBoardService.findByDate(taskDate);

        if (!tasks.isEmpty()) {
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    //    TODO 전체목록 보기처리용
    @GetMapping
    public Map<String, Object> dashboardList(){

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

    @PutMapping("/{Id}")
    public ResponseEntity DashboardUpdate(@ModelAttribute DashBoard dashBoard) {
        log.info("Update request received for Id: {}", dashBoard);

        if (dashBoard.getTaskStatus() == null) {
            if (dashBoard.getTaskStatus().equals("false")) {
                dashBoard.setTaskStatus("N");

            }
            if (dashBoard.getTaskStatus().equals("true")) {
                dashBoard.setTaskStatus("Y");
            }
        }



            if (dashBoardService.updateDashBoard(dashBoard) > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        }//update End

}//DashBoard End.
