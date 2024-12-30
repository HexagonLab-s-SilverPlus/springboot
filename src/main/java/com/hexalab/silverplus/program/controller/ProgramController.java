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
import java.nio.file.Files;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

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

    //Program List
    @GetMapping
    public ResponseEntity<Map<String, Object>> selectProgramListMethod(
        @ModelAttribute Search search
    ) {
        Map<String, Object> programWithFiles = new HashMap<>();

        Pageable pageable = PageRequest.of(search.getPageNumber() - 1,
                search.getPageSize(), Sort.by(Sort.Direction.DESC, "snrCreatedAt"));

        int listCount = 0;

        try {
            Map<String, Object> programlist = new HashMap<>();

            if (search.getAction().equals("all")) {
                search.setListCount(programService.selectAllListCount());
            } else if (search.getAction().equals("pgTitle")) {
                search.setListCount(programService.selectTitleListCount(search.getKeyword()));
            } else if (search.getAction().equals("pgContent")) {
                search.setListCount(programService.selectContentListCount(search.getKeyword()));
            } else if (search.getAction().equals("pgArea")) {
                search.setListCount(programService.selectAreaListCount(search.getKeyword()));
            } else if (search.getAction().equals("pgOrg")) {
                search.setListCount(programService.selectOrgNameListCount(search.getKeyword()));
            } else if (search.getAction().equals("pgDate")) {
                search.setListCount(programService.selectDateListCount(search));
            }

            programlist = programService.selectSearchList(pageable, search);
            log.info("programlist : {}", programlist);

            //각 프로그램의 모든 파일 정보 추가
            List<Map<String, Object>> programWithFilesList = new ArrayList<>();

            try (FTPUtility ftpUtility = new FTPUtility()) {
                ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

                // 프로그램 목록 가져오기
                List<Program> programs = (List<Program>) programlist.get("list");

                for (Program program : programs) {
                    List<ProgramFile> files = programFileService.selectProgramFiles(program.getSnrProgramId());

                    //파일 URL 리스트 생성
                    List<Map<String, Object>> fileDataList = new ArrayList<>();
                    if (files != null && !files.isEmpty()) {
                        for (ProgramFile file : files) {
                            Map<String, Object> fileData = new HashMap<>();

                            //파일 경로 구성
                            String remoteFilePath = ftpRemoteDir + "program/" + file.getSnrFileName();

                            //파일 다운로드 및 처리
                            File tempFile = File.createTempFile("program-", null);
                            ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                            //파일 내용 읽기
                            byte[] fileContent = Files.readAllBytes(tempFile.toPath());
                            tempFile.delete();

                            //MIME 타입 결정
                            String mimeType = getMimeType(file.getSnrFileOGName());
                            if (mimeType == null) {
                                mimeType = "application/octet-stream";
                            }

                            //파일 데이터 구성
                            fileData.put("fileName", file.getSnrFileOGName());
                            fileData.put("mimeType", mimeType);
                            fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent));

                            fileDataList.add(fileData);
                        }
                    }//if end

                    //프로그램 데이터와 파일 파일 데이터 병합
                    Map<String, Object> programData = new HashMap<>();
                    programData.put("program", program);
                    programData.put("pgfiles", fileDataList);  //모든 파일 URL 추가

                    programWithFilesList.add(programData);
                }//for end

                // 프로그램 리스트를 최종 반환 데이터에 추가
                programWithFiles.put("list", programWithFilesList);
                programWithFiles.put("search", search);

            } catch (Exception e) {
                log.error("FTP 작업 중 오류 발생", e);
            }

            return ResponseEntity.ok(programWithFiles);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("어르신 프로그램 목록 불러오기 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }//try~catch end

    }//selectProgramList() end

    //Program Detail
    @GetMapping("/detail/{snrProgramId}")
    public ResponseEntity<Map<String, Object>> selectProgramDetailMethod(
            @PathVariable String snrProgramId
    ) {
        try {
            Map<String, Object> programWithFiles = new HashMap<>();
            
            Program program = programService.selectProgram(snrProgramId);
            
            try (FTPUtility ftpUtility = new FTPUtility()) {
                ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

                List<ProgramFile> files = programFileService.selectProgramFiles(snrProgramId);
                List<Map<String, Object>> fileDataList = new ArrayList<>();

                if (files != null && !files.isEmpty()) {
                    for (ProgramFile file : files) {
                        Map<String, Object> fileData = new HashMap<>();

                        // FTP 파일 경로
                        String remoteFilePath = ftpRemoteDir + "program/" + file.getSnrFileName();

                        // 다운로드
                        File tempFile = File.createTempFile("program-", null);
                        ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                        // 파일 읽기 및 MIME 타입 결정
                        byte[] fileContent = Files.readAllBytes(tempFile.toPath());
                        String mimeType = getMimeType(file.getSnrFileOGName());
                        if (mimeType == null) {
                            mimeType = "application/octet-stream";
                        }

                        // 데이터 구성
                        fileData.put("fileName", file.getSnrFileOGName()); // 원본 파일명
                        fileData.put("mimeType", mimeType);
                        fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent));

                        fileDataList.add(fileData);

                        // 임시 파일 삭제
                        tempFile.delete();
                    }
                }

                programWithFiles.put("program", program);
                programWithFiles.put("files", fileDataList);

            } catch (Exception e) {
                log.error("FTP 작업 중 오류 발생", e);
            }//ftp try~catch

            return ResponseEntity.ok(programWithFiles);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("어르신 프로그램 디테일 불러오기 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }//selectProgramDetailMethod end

}//ProgramController end
