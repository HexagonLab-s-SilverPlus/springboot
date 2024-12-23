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
        notice.setNotId(UUID.randomUUID()); // UUID
        notice.setNotCreateAt(new Timestamp(System.currentTimeMillis())); // createBy
        notice.setNotUpdateAt(new Timestamp(System.currentTimeMillis())); // insertBy
        notice.setNotReadCount(0); // readCount set

        // set(noticeFiles)
        // insert
        try {
            // notice insert
            noticeService.noticeInsert(notice);
            log.info("notice inserted");

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
                    insertFile.setNfId(UUID.randomUUID());
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
                    noticeFilesService.noticeFileInsert(insertFile);

                    // delete tempFile
                    tempFile.delete();

                    log.info("insert file : " + fileName);
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

        // 검색조건 없을시
        if ((search.getKeyword() == null || search.getKeyword().isEmpty())
            && (search.getStartDate() == null || search.getEndDate() == null)
        ){
            try{

                // 리스트 갯수 확인
                int listCount = noticeService.selectAllNoticeListCount();
                log.info("list count : " + listCount);

                // pageable 객체 생성
                Pageable pageable = PageRequest.of(
                        search.getPageNumber() -1,
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
                return ResponseEntity.ok(map);
            } catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else
        return null;
    }
}
