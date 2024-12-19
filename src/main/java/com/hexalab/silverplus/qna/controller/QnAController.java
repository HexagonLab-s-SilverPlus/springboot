package com.hexalab.silverplus.qna.controller;

import com.hexalab.silverplus.qna.model.dto.QnA;
import com.hexalab.silverplus.qna.model.service.QnAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/qna")
@CrossOrigin
public class QnAController {
    private final QnAService qnaService;

    @GetMapping("/mylist")
    public ResponseEntity<QnA> selectMyListQnA(@RequestParam String uuid) {
        log.info("selectMyListQnA  sada {}", uuid);
        try {
            QnA qna = qnaService.selectList(uuid);
            return ResponseEntity.ok(qna);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping
    public ResponseEntity insertQnA(
            @ModelAttribute QnA qna
            ){
        log.info("qna insert");
        if(qnaService.insertQnA(qna)){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
