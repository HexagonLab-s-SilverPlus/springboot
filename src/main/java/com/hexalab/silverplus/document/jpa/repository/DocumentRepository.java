package com.hexalab.silverplus.document.jpa.repository;

import com.hexalab.silverplus.document.jpa.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;


public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {
    // writtenBy를 기준으로 문서를 조회하는 메소드 추가
    List<DocumentEntity> findByWrittenBy(String writtenBy);

}
