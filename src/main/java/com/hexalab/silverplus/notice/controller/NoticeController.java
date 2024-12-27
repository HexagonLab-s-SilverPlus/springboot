package com.hexalab.silverplus.notice.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.notice.model.dto.Notice;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import com.hexalab.silverplus.notice.model.service.NoticeFilesService;
import com.hexalab.silverplus.notice.model.service.NoticeService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
@CrossOrigin
public class NoticeController {

    // service DI
    private final NoticeService noticeService;
    private final NoticeFilesService noticeFilesService;

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


    // insert
    @PostMapping
    public ResponseEntity noticeInsert(
            @ModelAttribute Notice notice,
            @RequestParam(name="newFiles",required = false) MultipartFile[] files
    ){
        // front data check
        log.info("notice data : " + notice);
        if(files != null){
            for (MultipartFile file : files) {
                log.info("파일이름 : " + file.getOriginalFilename());
            }
        }

        // set(notice)
        notice.setNotId(UUID.randomUUID().toString()); // UUID
        notice.setNotCreateAt(new Timestamp(System.currentTimeMillis())); // createBy
        notice.setNotUpdateAt(new Timestamp(System.currentTimeMillis())); // insertBy
        notice.setNotReadCount(0); // readCount set

        // set(noticeFiles)
        // insert
        try {
            // notice insert
            if (noticeService.noticeInsert(notice)==1){
                log.info("공지사항 글등록 성공");
            } else{
                log.info("공지사항 글 등록 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // nas ftp connect
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);
            if (files != null && files.length > 0) {
                // noticeFiles set
                for (MultipartFile file : files) {
                    // setter
                    NoticeFiles insertFile = new NoticeFiles();
                    String fileName = file.getOriginalFilename();
                    String renameFile = CreateRenameFileName.create(notice.getNotId(),fileName);
                    insertFile.setNfId(UUID.randomUUID().toString());
                    insertFile.setNfNotId(notice.getNotId());
                    insertFile.setNfOreginalName(fileName);
                    insertFile.setNfRename(renameFile);

                    // create file
                    File tempFile = File.createTempFile("notice-",null);
                    file.transferTo(tempFile);

                    // file upload
                    String remoteFilePath = ftpRemoteDir + "notice/"+renameFile;
                    ftpUtility.uploadFile(tempFile.getAbsolutePath(),remoteFilePath);

                    // db save
                    if (noticeFilesService.noticeFileInsert(insertFile) ==1 ){
                        // delete tempFile
                        tempFile.delete();
                        log.info("insert file : " + fileName);
                    } else {
                        log.info("공지사항 첨부파일 등록 실패");
                        if(noticeService.noticeDelete(notice.getNotId())==1){
                            log.info("등록실패 공지사항 삭제 성공");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        } else{
                            log.info("등록실패 공지사항 삭제 실패 확인이 필요합니다.");
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    }
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e){
            e.printStackTrace();
            log.error("공지사항 등록 중 오류발생 : ",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //All NoticeList
    @GetMapping
    public ResponseEntity<Map> noticeList(
            @ModelAttribute Search search
    ){
        log.info("search data : " + search);
        // 검색조건 없을시
        if (search.getKeyword() == null || search.getKeyword().isEmpty()
        ){
            try{
                // list count
                int listCount = noticeService.selectAllNoticeListCount();
                log.info("list count : " + listCount);

                //search setting
                if(search.getPageNumber()==0){
                    search.setPageNumber(1);
                    search.setPageSize(10);
                }
                search.setListCount(listCount);

                // pageable 객체 생성
                Pageable pageable = PageRequest.of(
                        search.getPageNumber() - 1,
                        search.getPageSize(),
                        Sort.by(Sort.Direction.DESC,"notCreateAt")
                );
                // 목록조회
                ArrayList<Notice> noticeList = noticeService.selectAllNoticeList(pageable);
                log.info("list count : " + noticeList.size());

                // Map에 담아 전송
                Map<String,Object> map = new HashMap<>();
                map.put("list",noticeList);
                map.put("search",search);
                log.info("map : " + map);
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        // 검색조건 있을시
        } else if(search.getKeyword() != null){
            try{
                int listCount = 0;
                // 목록 갯수 출력
                if (search.getAction().equals("제목")){
                    listCount = noticeService.selectSearchTitleNoticeListCount(search.getKeyword());
                } else if (search.getAction().equals("내용")){
                    listCount = noticeService.selectSearchContentNoticeListCount(search.getKeyword());
                }
                log.info("listCount : " + listCount);

//                //search setting
//                if(search.getPageNumber()==0){
//                    search.setPageNumber(1);
//                    search.setPageSize(10);
//                }
                search.setListCount(listCount);

                // pageable 객체 생성
                Pageable pageable = PageRequest.of(
                        search.getPageNumber() - 1,
                        search.getPageSize(),
                        Sort.by(Sort.Direction.DESC,"notCreateAt")
                );
                // 목록조회
                ArrayList<Notice> noticeList =new ArrayList<Notice>();
                if (search.getAction().equals("제목")){
                    noticeList = noticeService.selectSearchTitleNoticeList(search.getKeyword(),pageable);
                } else if (search.getAction().equals("내용")){
                    noticeList = noticeService.selectSearchContentNoticeList(search.getKeyword(),pageable);
                }
                log.info("list count : " + noticeList.size());

                // Map에 담아 전송
                Map<String,Object> map = new HashMap<>();
                map.put("list",noticeList);
                map.put("search",search);
                log.info("map : " + map);
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return null;
    }
}
