package com.hexalab.silverplus.notice.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import com.hexalab.silverplus.notice.model.dto.Notice;
import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import com.hexalab.silverplus.notice.model.service.NoticeFilesService;
import com.hexalab.silverplus.notice.model.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

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
                    insertFile.setNfId(UUID.randomUUID().toString());
                    String renameFile = CreateRenameFileName.create(insertFile.getNfId(),fileName);
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

    // 공지사항 상세보기
    @GetMapping("/detail/{notId}")
    public ResponseEntity<Map> noticeDetail(
            @PathVariable("notId") String notId
    ){
        log.info("notId : " + notId);
        // 담을객체
        Map<String,Object> map = new HashMap<>();
        List<Map<String, Object>> fileList = new ArrayList<>();

        try {
            // 조회수 증가
            noticeService.upReadCount(notId);

            // 공지사항 불러오기
            Notice notice = noticeService.selectNotice(notId);
            log.info("notice : " + notice);
            map.put("notice", notice);

            // 첨부파일 갯수 확인
            ArrayList<NoticeFiles> noticeFiles = new ArrayList<NoticeFiles>();
            int fileCount = noticeFilesService.checkNoticeFiles(notId);
            log.info("첨부파일 갯수:{}", fileCount);

            // 첨부파일 있을시 첨부파일 리스트 담기
            if (fileCount != 0) {
                noticeFiles = noticeFilesService.selectNoticeFiles(notId);
                log.info("noticeFiles : " + noticeFiles);
                map.put("noticeFiles", noticeFiles);
            }

            // FTP 서버 연결
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            for (NoticeFiles file : noticeFiles) {
                Map<String, Object> fileData = new HashMap<>();

                String mfRename = file.getNfRename();

                // mfRename 값 확인
                log.info("mfRename 값 확인: {}", mfRename);

                // 파일 경로 구성
                String remoteFilePath = ftpRemoteDir + "notice/" + mfRename;
                log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);

                // 파일 다운로드
                File tempFile = File.createTempFile("preview-", null);
                ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                // 파일 읽기
                byte[] fileContent = Files.readAllBytes(tempFile.toPath());
                tempFile.delete();

                // MIME 타입 결정
                String mimeType = getMimeType(file.getNfOreginalName());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                // 파일 데이터 구성
                fileData.put("fileName", file.getNfOreginalName());
                fileData.put("mimeType", mimeType);
                fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // Base64로 인코딩
                fileList.add(fileData);
            }
            // 파일 담기
            map.put("fileList", fileList);
                // 리턴
            return ResponseEntity.ok(map);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/nfdown")
    public ResponseEntity<Resource> fileDownload(
            @RequestParam("ofile") String originalFileName,
            @RequestParam("rfile") String renameFileName
    ) {
        log.info("ofile : " + originalFileName);
        log.info("rfile : " + renameFileName);

        try (FTPUtility ftpUtility = new FTPUtility()) {
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            // 임시 파일 생성 후 FTP에서 다운로드
            File tempFile = File.createTempFile("download-", null);
            String remoteFilePath = ftpRemoteDir + "notice/" + renameFileName;
            ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

            Resource resource = new FileSystemResource(tempFile);

            String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //MIME타입
    private String getMimeType(String snrFileOGName) {
        if (snrFileOGName == null || !snrFileOGName.contains(".")) {
            return null;
        }
        String extension = snrFileOGName.substring(snrFileOGName.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
            case "tif":
            case "tiff":
                return "image/" + extension;
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "hwp":
                return "application/x-hwp";
            case "hwpx":
                return "application/hwp+zip";
            case "doc":
            case "docx":
                return "application/msword";
            case "zip":
            case "rar":
            case "7z":
            case "tar":
            case "gz":
                return "application/zip";
            default:
                return "application/octet-stream"; // 기본 MIME 타입
        }
    }//getMimeType end

}
