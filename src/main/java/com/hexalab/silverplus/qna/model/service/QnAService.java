package com.hexalab.silverplus.qna.model.service;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import com.hexalab.silverplus.qna.jpa.repository.QnARepository;
import com.hexalab.silverplus.qna.model.dto.QnA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {
    private final QnARepository qnARepository;
    private final MemberService memberService;

    public Map<String, Object> selectMytList(String uuid, Pageable pageable){
        return qnARepository.selectMyQnA(uuid, pageable);
    }

    public Map<String, Object> selectAllList(Pageable pageable){
        return qnARepository.selectAllQnA(pageable);
    }

    public boolean insertQnA(QnA qna){
        try {
            qnARepository.save(qna.toEntity());
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
}
