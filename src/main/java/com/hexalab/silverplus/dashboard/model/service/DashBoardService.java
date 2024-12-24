package com.hexalab.silverplus.dashboard.model.service;


import com.hexalab.silverplus.dashboard.jpa.repository.DashBoardRepository;
import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashBoardService {

    private final DashBoardRepository dashBoardRepository;

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
}
