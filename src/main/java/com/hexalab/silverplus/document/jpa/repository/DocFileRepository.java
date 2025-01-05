package com.hexalab.silverplus.document.jpa.repository;

import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocFileRepository extends JpaRepository<DocFileEntity, String> {
}
