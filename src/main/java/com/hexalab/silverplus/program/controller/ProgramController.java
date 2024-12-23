package com.hexalab.silverplus.program.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.program.model.dto.Program;
import com.hexalab.silverplus.program.model.dto.ProgramFile;
import com.hexalab.silverplus.program.model.service.ProgramFileService;
import com.hexalab.silverplus.program.model.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Timestamp;
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
        program.setSnrProgramId(UUID.randomUUID());
        program.setSnrCreatedAt(new Timestamp(System.currentTimeMillis()));


        //set (ProgramFile)
        try {
            // NAS FTP 연결
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    ProgramFile programFile = new ProgramFile();
                    String fileName = file.getOriginalFilename();
                    String renameFile = CreateRenameFileName.create(program.getSnrProgramId(), fileName);
                    programFile.setSnrFileId(UUID.randomUUID());
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
        } catch (Exception e) {
            e.printStackTrace();
            log.error("어르신 프로그램 등록 중 오류발생 : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        //program insert
        if (programService.insertProgram(program) > 0) {
            log.info("Program Insertion Successfully");
            return ResponseEntity.ok().build();
        } else {
            log.info("Program Insertion Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }//insertProgramMethod end

}//ProgramController end
