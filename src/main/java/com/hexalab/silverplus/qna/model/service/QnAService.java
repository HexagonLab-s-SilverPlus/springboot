package com.hexalab.silverplus.qna.model.service;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.jpa.repository.QnARepository;
import com.hexalab.silverplus.qna.model.dto.QnA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;


import java.util.Map;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor
@Transactional
public class QnAService {
    private final QnARepository qnARepository;
    private final MemberService memberService;

    public Map<String, Object> selectMytList(String uuid, Pageable pageable, Search search){
        return qnARepository.selectMyQnA(uuid, pageable, search);
    }

    public Map<String, Object> selectADList(Pageable pageable, Search search){
        return qnARepository.selectADList(pageable, search);
    }

    public QnA insertQnA(QnA qna){
        try {
            return qnARepository.save(qna.toEntity()).toDto();
        }catch (Exception e){
            return null;
        }
    }

    public int selectAllListCount() {
        return (int)qnARepository.count();
    }

    public int selectMytListCount(String uuid) {
        return qnARepository.myCount(uuid);
    }

    public int selectTitleAllListCount(String keyword) {
        return qnARepository.adTitleCount(keyword);
    }

    public int selectDateAllListCount(Search search) {
        return qnARepository.adDateCount(search);
    }

    public int selectTitleListCount(String uuid, String keyword) {
        return qnARepository.myTitleCount(uuid, keyword);
    }

    public int selectDateListCount(String uuid, Search search) {
        return qnARepository.myDateCount(uuid, search);
    }

    public QnA selectOne(String qnaId) {
        return qnARepository.findById(qnaId).get().toDto();
    }

    public void deleteOne(String qnaId) {
        qnARepository.deleteById(qnaId);
    }

    public void updateOne(QnA qnaO) {
        qnARepository.save(qnaO.toEntity());
    }
}
