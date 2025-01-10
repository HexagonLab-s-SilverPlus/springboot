package com.hexalab.silverplus.document.jpa.repository;

import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
    // writtenBy를 기준으로 문서를 조회하는 메소드 추가
    List<DocumentEntity> findByWrittenBy(String writtenBy);

    // '대기중' 상태이면서 승인자가 본인인 문서 조회
    Page<DocumentEntity> findByIsApprovedAndApprovedBy(String isApproved, String approvedBy, Pageable pageable);

}
