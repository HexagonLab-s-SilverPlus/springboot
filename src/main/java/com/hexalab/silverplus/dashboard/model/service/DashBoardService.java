package com.hexalab.silverplus.dashboard.model.service;


import com.hexalab.silverplus.dashboard.jpa.entity.DashBoardEntity;
import com.hexalab.silverplus.dashboard.jpa.repository.DashBoardRepository;
import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashBoardService {

    private final DashBoardRepository dashBoardRepository;


//insert
    @Transactional
    public int insertDashboard(DashBoard dashBoard) {
        try{
            dashBoardRepository.save(dashBoard.toEntity());
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }


    }
 //selctt

    public ArrayList<DashBoard> selectList() {
         // 전체 데이터를 조회
         List<DashBoardEntity> entities = dashBoardRepository.findAll();

         // DTO로 변환
         ArrayList<DashBoard> list = new ArrayList<>();
         for (DashBoardEntity entity : entities) {
             list.add(entity.toDto());
         }

         return list; // 변환된 리스트 반환
     }

     @Transactional
    public int deleteDashboard(String taskId) {
        try {
            dashBoardRepository.deleteById(taskId);
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
     }



}
