package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String>, MemberRepositoryCustom {
    /* 2025-01-08. 은영이가 구현 */
    // memUUID로 memName 조회하는 인터페이스
    MemberEntity findByMemUUID(String memUUID);

    //25-01-11(수진이가 구현) - 대시보드 카운트 구현(한 매니저당 관리하는 어르신 카운트)
        @Query("SELECT COUNT(m) FROM MemberEntity m WHERE m.memUUIDMgr = :memUUID AND m.memType = :memType")
        int selectAllSeniorCount(@Param("memUUID") String memUUID, @Param("memType") String memType);
}
