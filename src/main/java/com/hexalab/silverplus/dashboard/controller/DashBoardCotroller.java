package com.hexalab.silverplus.dashboard.controller;

import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import com.hexalab.silverplus.dashboard.model.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashBoardCotroller {
    private final DashBoardService dashBoardService;

    //할일 등록 요청 처리용
    @PostMapping
    public ResponseEntity dashboardInsert(
            @ModelAttribute DashBoard dashBoard) {
        log.info("Insert dashboard inserted" + dashBoard);

        try{
            dashBoardService.insertDashboard(dashBoard);
            log.info("Insert dashboard inserted" + dashBoard);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }


}
