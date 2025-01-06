package com.hexalab.silverplus.qna.controller;

import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.qna.model.dto.QnA;
import com.hexalab.silverplus.qna.model.service.QnAService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j    //log 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@RequiredArgsConstructor
@RestController
@RequestMapping("/qna")
@CrossOrigin
public class QnAController {
    private final QnAService qnaService;
    private final MemberService memberService;

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
                }else if(search.getAction().equals("title")) {
                    search.setListCount(qnaService.selectTitleListCount(uuid, search.getKeyword()));
                }else if(search.getAction().equals("date")) {
                    search.setListCount(qnaService.selectDateListCount(uuid, search));
                }

                qnaList = qnaService.selectMytList(uuid, pageable, search);
            }else{
                //Admin
                if(search.getAction().equals("all")) {
                    search.setListCount(qnaService.selectAllListCount());
                }else if(search.getAction().equals("title")) {
                    search.setListCount(qnaService.selectTitleAllListCount(search.getKeyword()));
                }else if(search.getAction().equals("date")) {
                    search.setListCount(qnaService.selectDateAllListCount(search));
                }

                qnaList = qnaService.selectADList(pageable, search);
            }

            log.info("Map<String, Object> : {}", qnaList);

            return ResponseEntity.ok(qnaList);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/detail/{qnaId}")
    public ResponseEntity<Map<String, Object>> selectDetailQnA(@PathVariable String qnaId) {
        try {
            QnA qna = qnaService.selectOne(qnaId);
            Member member = memberService.selectMember(qna.getQnaWCreateBy());
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            String[] fileList = ftpUtility.search(ftpRemoteDir + "qna/" + qnaId);

            Map<String, Object> map = new HashMap<>();
            map.put("qna", qna);
            map.put("member", member);
            map.put("files", fileList);

            return ResponseEntity.ok(map);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/qfdown")
    public ResponseEntity<Resource> fileDownload(
            @RequestParam("fileName") String filename
            ) {

        try (FTPUtility ftpUtility = new FTPUtility()) {
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            // 임시 파일 생성 후 FTP에서 다운로드
            File tempFile = File.createTempFile("downloa-", null);
            ftpUtility.downloadFile(filename, tempFile.getAbsolutePath());

            Resource resource = new FileSystemResource(tempFile);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    @PostMapping
    public ResponseEntity<QnA> insertQnA(
            @ModelAttribute QnA qna,
            @RequestParam(name="newFiles",required = false) MultipartFile[] files
            ) {

        QnA inserQnA = qnaService.insertQnA(qna);
        if(inserQnA != null) {
            if(files != null && files.length > 0) {
                try {
                    FTPUtility ftpUtility = new FTPUtility();
                    ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);
                    ftpUtility.uploadFile_mkDir(ftpRemoteDir + "qna/" + inserQnA.getQnaId());

                    for (int i = 0; i < files.length; i++) {
                        String ext = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().indexOf(".") + 1);
                        String fileName = "qna_" + inserQnA.getQnaId() + "_" + i  + "." + ext;

                        File tempFile = File.createTempFile("QnA-",null);
                        files[i].transferTo(tempFile);
                        // file upload
                        String filePath = ftpRemoteDir + "qna/" + inserQnA.getQnaId() + "/" + fileName;
                        ftpUtility.uploadFile(tempFile.getAbsolutePath(), filePath);
                        tempFile.delete();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("{role}")
    public void updateQnA(
            @ModelAttribute QnA qna,
            @PathVariable String role,
            @RequestParam(name="newFiles",required = false) MultipartFile[] newFiles,
            @RequestParam(name="deleteFiles", required = false) String[] deleteFiles) {

        try {
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            QnA qnaO = qnaService.selectOne(qna.getQnaId());
            if(role.equals("ADMIN")) {
                qnaO.setQnaADUpdateBy(qna.getQnaADUpdateBy());
                qnaO.setQnaADUpdateAt(new Timestamp(System.currentTimeMillis()));
                qnaO.setQnaADContent(qna.getQnaADContent());
                if(qnaO.getQnaADCreateBy() == null){
                    qnaO.setQnaADCreateBy(qna.getQnaADUpdateBy());
                    qnaO.setQnaADCreateAt(qna.getQnaADUpdateAt());
                }
            }else{
                qnaO.setQnaTitle(qna.getQnaTitle());
                qnaO.setQnaWContent(qna.getQnaWContent());
                qnaO.setQnaWUpdateAt(new Timestamp(System.currentTimeMillis()));
            }

            if(deleteFiles != null && deleteFiles.length > 0) {

                for (int i = 0; i < deleteFiles.length; i++) {
                    int dotIndex = deleteFiles[i].lastIndexOf(".");
                    int underscoreIndex = deleteFiles[i].lastIndexOf("_");

                    String num =  deleteFiles[i].substring(underscoreIndex + 1, dotIndex);
                    String ext =  deleteFiles[i].substring(dotIndex + 1);

                    String deleteFileName = "qna_" + qnaO.getQnaId() + "_" + num + "." + ext;
                    String deleteFilePath = ftpRemoteDir + "qna/" + qnaO.getQnaId() + "/" + deleteFileName;
                    ftpUtility.deleteFile(deleteFilePath);
                }

                int idx = ftpUtility.search(ftpRemoteDir + "qna/" + qnaO.getQnaId()).length;
                for(int i = 0; i < idx; i++) {
                    String qnaFileName = "qna_" + qnaO.getQnaId() + "_" + i;
                    String qnaFilePath =  ftpRemoteDir + "qna/" + qnaO.getQnaId() + "/" + qnaFileName;

                    String[] qnaFileList = ftpUtility.search(ftpRemoteDir + "qna/" + qnaO.getQnaId());

                    log.info("qnaFilePath : " + qnaFilePath);
                    log.info("qnaOFilePath : " + qnaFileList[i].substring(0,qnaFileList[i].lastIndexOf(".")));
                    if(!qnaFileList[i].substring(0,qnaFileList[i].lastIndexOf(".")).equals(qnaFilePath)){
                        for(int j = i; j < qnaFileList.length; j++) {
                            int numIdx = qnaFileList[j].lastIndexOf("_");
                            int extIdx = qnaFileList[j].lastIndexOf(".");

                            String fir = qnaFileList[j].substring(0,numIdx);
                            String ext =  qnaFileList[i].substring(extIdx + 1);
                            int num = Integer.parseInt(qnaFileList[j].substring(numIdx + 1, extIdx));

                            String reQnAfile = fir + "_" + (num - 1) + "." + ext;
                            ftpUtility.reName(qnaFileList[j], reQnAfile);
                            log.info("asdad");
                        }
                        i -= 1;
                    }
                }
            }

            if(newFiles != null && newFiles.length > 0) {
                int idx = ftpUtility.search(ftpRemoteDir + "qna/").length;

                for (int i = 0; i < newFiles.length; i++) {
                    String ext = newFiles[i].getOriginalFilename().substring(newFiles[i].getOriginalFilename().indexOf(".") + 1);
                    String fileName = "qna_" + qnaO.getQnaId() + "_" + (i + idx)  + "." + ext;

                    try {
                        File tempFile = File.createTempFile("QnA-",null);
                        newFiles[i].transferTo(tempFile);
                        // file upload
                        String filePath = ftpRemoteDir + "qna/" + qnaO.getQnaId() + "/" + fileName;
                        ftpUtility.uploadFile(tempFile.getAbsolutePath(), filePath);
                        tempFile.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            qnaService.updateOne(qnaO);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @DeleteMapping("{qnaId}")
    public void deleteQnA(@PathVariable String qnaId) {
        try {
            qnaService.deleteOne(qnaId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
