package com.hexalab.silverplus.qna.controller;

import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.model.dto.QnA;
import com.hexalab.silverplus.qna.model.service.QnAService;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/qna")
@CrossOrigin
public class QnAController {
    private final QnAService qnaService;
    private final MemberService memberService;

    @GetMapping("/mylist")
    public ResponseEntity<Map<String, Object>> selectMyListQnA(
            @RequestParam(required = false) String uuid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("uuid : {}", uuid);
        log.info("page : {}", page);
        log.info("limit : {}", limit);

        Map<String, Object> map = new HashMap();

        Pageable pageable = PageRequest.of(page,
                limit, Sort.by(Sort.Direction.DESC, "qnaWUpdateAt"));
        map.put("paging", pageable);

        try {
            Map<String, Object> qnaList = new HashMap<>();
            if (uuid != null) {
                qnaList = qnaService.selectMytList(uuid, pageable);
            }else{
                qnaList = qnaService.selectAllList(pageable);
            }

            log.info("Map<String, Object> : {}", qnaList);

            return ResponseEntity.ok(qnaList);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping
    public ResponseEntity insertQnA(
            @ModelAttribute QnA qna
            ){
        log.info("qna insert : {}", qna);
        if(qnaService.insertQnA(qna)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
