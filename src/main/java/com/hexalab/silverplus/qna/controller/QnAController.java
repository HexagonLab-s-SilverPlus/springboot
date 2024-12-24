package com.hexalab.silverplus.qna.controller;

import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.model.dto.QnA;
import com.hexalab.silverplus.qna.model.service.QnAService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/qna")
@CrossOrigin
public class QnAController {
    private final QnAService qnaService;

    // file upload path valiable
    @Value("${ftp.server}")
    private String ftpServer;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.username}")
    private String ftpUsername;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.remote-dir}")
    private String ftpRemoteDir;

    //qna list view
    @GetMapping("/mylist")
    public ResponseEntity<Map<String, Object>> selectMyListQnA(
            @RequestParam(required = false) String uuid,
            @ModelAttribute Search search) {
        log.info("uuid : {}", uuid);
        log.info("search : {}", search);

        Map<String, Object> map = new HashMap();

        Pageable pageable = PageRequest.of(search.getPageNumber() - 1,
                search.getPageSize(), Sort.by(Sort.Direction.DESC, "qnaWUpdateAt"));
        int listCount = 0;
        try {
            Map<String, Object> qnaList = new HashMap<>();
            if (uuid != null ) {
                //not Admin
                if(search.getAction().equals("all")){
                    search.setListCount(qnaService.selectMytListCount(uuid));
                    qnaList = qnaService.selectMytList(uuid, pageable, search);
                }
            }else{
                //Admin
                if(search.getAction().equals("all")) {
                    search.setListCount(qnaService.selectAllListCount());
                }else if(search.getAction().equals("title")) {
                    search.setListCount(qnaService.selectTitleListCount(search.getKeyword()));
                }

                qnaList = qnaService.selectADList(pageable, search);
            }

            log.info("Map<String, Object> : {}", qnaList);

            return ResponseEntity.ok(qnaList);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping
    public ResponseEntity insertQnA(
            @ModelAttribute QnA qna,
            @RequestParam(name="newFiles",required = false) MultipartFile[] files
            ) {
        log.info("qna insert : {}", qna);
        QnA inserQnA = qnaService.insertQnA(qna);
        log.info("insert QnA : {}", inserQnA);
        if(inserQnA != null) {
            if(files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String ext = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().indexOf(".") + 1);
                    String fileName = "qna_" + inserQnA.getQnaId() + "_" + i  + "." + ext;

                    try {
                        FTPUtility ftpUtility = new FTPUtility();
                        ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

                        File tempFile = File.createTempFile("QnA-",null);
                        files[i].transferTo(tempFile);
                        // file upload
                        String filePath = ftpRemoteDir + "qna/"+ fileName;
                        ftpUtility.uploadFile(tempFile.getAbsolutePath(), filePath);
                        tempFile.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
