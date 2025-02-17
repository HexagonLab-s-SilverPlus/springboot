package com.hexalab.silverplus.dashboard.jpa.repository;

import com.hexalab.silverplus.dashboard.jpa.entity.DashBoardEntity;
import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface DashBoardRepository extends JpaRepository<DashBoardEntity, String> ,DashBoardRepositoryCustom{
    List<DashBoard> findByTaskDate(Timestamp taskDate);

    @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.memUUIDMgr = :memUUID AND m.memType = :memType")
    int selectAllSeniorCount(@Param("memUUID") String memUUID, @Param("memType") String memType);

}
