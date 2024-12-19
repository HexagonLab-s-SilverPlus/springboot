package com.hexalab.silverplus.qna.model.service;

import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.jpa.entity.QnAEntity;
import com.hexalab.silverplus.qna.jpa.repository.QnARepository;
import com.hexalab.silverplus.qna.model.dto.QnA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {
    private final QnARepository qnARepository;
    private final MemberService memberService;

    public ArrayList<QnA> selectList(String uuid, Pageable pageable){
        List<QnAEntity> qnaEntityList = qnARepository.selectMyQnA(uuid, pageable);
        ArrayList<QnA> list = new ArrayList<>();

        for (QnAEntity qnaEntity : qnaEntityList) {
            list.add(qnaEntity.toDto());
        }
        log.info("ArrayList<QnA> : {}",list);
        return list;
    }

//    public QnA selectListAll(){
//
//    }

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
