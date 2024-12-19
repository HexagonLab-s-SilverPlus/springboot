package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QnARepository extends JpaRepository<QnAEntity, UUID> {

}
