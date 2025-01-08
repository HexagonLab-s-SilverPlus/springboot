package com.hexalab.silverplus.document.jpa.repository;

import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocFileRepository extends JpaRepository<DocFileEntity, String> {
    // docId를 기준으로 파일을 조회하는 메소드
    DocFileEntity findByDocId(String docId);
}
