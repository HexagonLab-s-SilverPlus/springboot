package com.hexalab.silverplus.qna.jpa.repository;

import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QnARepositoryCustom {

    List<QnAEntity> selectMyQnA(String uuid, Pageable pageable);
}
