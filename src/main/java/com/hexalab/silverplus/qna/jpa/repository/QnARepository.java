package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnARepository extends JpaRepository<QnAEntity, String>, QnARepositoryCustom {

}
