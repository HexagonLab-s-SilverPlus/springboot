package com.hexalab.silverplus.program.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Paging;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.notice.model.dto.Notice;
import com.hexalab.silverplus.program.model.dto.Program;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import com.hexalab.silverplus.program.model.service.ProgramFileService;
import com.hexalab.silverplus.program.model.service.ProgramService;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/program")
@CrossOrigin
public class ProgramController {
    //service DI
    private final ProgramService programService;
    private final ProgramFileService programFileService;

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

    //insert
    @PostMapping
    public ResponseEntity insertProgramMethod(
            @ModelAttribute Program program,
            @RequestParam(name = "files", required = false) MultipartFile[] files
    ) {
        //data check
        log.info("program data : {}", program);
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                log.info("Original File Name : ", file.getOriginalFilename());
            }
        }

        //set
        program.setSnrStartedAt(program.getSnrStartedAt());
        program.setSnrEndedAt(program.getSnrEndedAt());
        program.setSnrProgramId(UUID.randomUUID().toString());
        program.setSnrCreatedAt(new Timestamp(System.currentTimeMillis()));

        //program insert
        if (programService.insertProgram(program) > 0) {
            log.info("Program Insertion Successfully");
        } else {
            log.info("Program Insertion Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        //set (ProgramFile)
        try {
            // NAS FTP 연결
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    ProgramFile programFile = new ProgramFile();
                    String fileName = file.getOriginalFilename();
                    programFile.setSnrFileId(UUID.randomUUID().toString());
                    String renameFile = CreateRenameFileName.create(programFile.getSnrFileId(), fileName);
                    programFile.setSnrFileOGName(fileName);
                    programFile.setSnrFileName(renameFile);
                    programFile.setSnrProgramId(program.getSnrProgramId());

                    // 임시 파일 생성
                    File tempFile = File.createTempFile("program-", null);
                    file.transferTo(tempFile);

                    // 파일 업로드
                    String remoteFilePath = ftpRemoteDir + "program/" + renameFile;
                    ftpUtility.uploadFile(tempFile.getAbsolutePath(), remoteFilePath);

                    // DB 저장
                    if (programFileService.insertProgramFile(programFile) > 0) {
                        log.info("ProgramFile Insertion Successfully");
                    } else {
                        log.error("ProgramFile Insertion Failed");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }

                    // 임시 파일 삭제
                    tempFile.delete();
                }//for end
            }//if end

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("어르신 프로그램 등록 중 오류발생 : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }//insertProgramMethod end

    //Program List
    @GetMapping
    public Map<String, Object> programListMethod(
            @RequestParam(name = "page", defaultValue = "1") int currentPage,
            @RequestParam(name = "limit", defaultValue = "8") int limit
    ) {
        int listCount = programService.selectListCount();

        //페이지 관련 항목 계산 처리
        Paging paging = new Paging(listCount, limit, currentPage);
        paging.calculate();

        //JPA가 제공하는 메소드에 필요한 Pageable 객체 생성
        Pageable pageable = PageRequest.of(paging.getCurrentPage() - 1, paging.getLimit(),
                Sort.by(Sort.Direction.DESC, "snrCreatedAt"));

        ArrayList<Program> list = programService.selectList(pageable);

        //각 프로그램의 모든 파일 정보 추가
        ArrayList<Map<String, Object>> programWithImages = new ArrayList<>();
        try (FTPUtility ftpUtility = new FTPUtility()) {
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            for (Program program : list) {
                ArrayList<ProgramFile> files = programFileService.selectProgramFiles(program.getSnrProgramId());

                //파일 URL 리스트 생성
                ArrayList<String> fileUrls = new ArrayList<>();
                if (files != null && !files.isEmpty()) {
                    for (ProgramFile file : files) {
                        //FTP 경로 생성
                        //String remoteFilePath = ftpRemoteDir + "program/" + file.getSnrFileName();
                        //String fileUrl = "ftp://" + ftpServer + ":" + ftpPort + remoteFilePath; //URL 생성

                        //String remoteFilePath = (ftpRemoteDir.endsWith("/") ? ftpRemoteDir : ftpRemoteDir + "/") + "program/" + file.getSnrFileName();
                        //String fileUrl = "ftp://" + ftpServer + ":" + ftpPort + remoteFilePath;

                        String remoteFilePath = ftpRemoteDir + "program/" + file.getSnrFileName();
                        String fileUrl = "https://" + ftpServer + ":" + ftpPort + "/files/" + remoteFilePath;

                        fileUrls.add(fileUrl);  //리스트에 추가
                    }
                }//if end

                //프로그램 데이터와 파일 URL 리스트 병합
                Map<String, Object> programData = new HashMap<>();
                programData.put("program", program);
                programData.put("fileUrls", fileUrls);  //모든 파일 URL 추가

                programWithImages.add(programData);
            }//for end

        } catch (Exception e) {
            log.error("FTP 작업 중 오류 발생", e);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", programWithImages);
        map.put("paging", paging);

        return map;
    }//selectProgramList() end

}//ProgramController end
