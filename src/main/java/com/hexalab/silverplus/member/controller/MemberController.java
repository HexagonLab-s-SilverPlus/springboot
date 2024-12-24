package com.hexalab.silverplus.member.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import com.hexalab.silverplus.member.model.service.MemberFilesService;
import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j      // 로그
@RestController     // REST API 어노테이션
@RequestMapping("/member")
@RequiredArgsConstructor    // 자동 객체 생성(의존성 주입)
public class MemberController {

    private final MemberService memberService;

    private final MemberFilesService memberFilesService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${uploadDir}")
    private String uploadDir;

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

    // 회원가입 처리 메소드
    @PostMapping
    public ResponseEntity<String> memberEnrollMethod(
            @ModelAttribute Member member, HttpServletRequest request,
            @RequestParam(name="memFiles", required = false) MultipartFile[] memFiles) {
        try {
            log.info("전송온 member 데이터 확인 : " + member);    // 전송온 member 데이터 확인
            // 패스워드 암호화 처리
            member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            log.info("member" + member);    // 암호화처리 정상 작동 확인

            member.setMemUUID(UUID.randomUUID().toString());


            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            if(memFiles != null && memFiles.length > 0) {
                for (MultipartFile mfile : memFiles) {
                    // MemberFiles 객체 생성
                    MemberFiles memberFiles = new MemberFiles();

                    String fileName = mfile.getOriginalFilename();
                    String renamFileName = CreateRenameFileName.create(member.getMemUUID(), fileName);

                    memberFiles.setMfId(member.getMemUUID());
                    memberFiles.setMfOriginalName(mfile.getOriginalFilename());
                    memberFiles.setMfRename(renamFileName);
                    memberFiles.setMfMemUUID(member.getMemUUID());

                    File tempFile = File.createTempFile("member-", null);
                    mfile.transferTo(tempFile);

                    String remoteFilePath = ftpRemoteDir + "member/" + renamFileName;
                    ftpUtility.uploadFile(tempFile.getAbsolutePath(), remoteFilePath);

                    memberFilesService.insertMemberFiles(memberFiles);

                    tempFile.delete();

                }
            }
            memberService.insertMember(member);
            return new ResponseEntity<String>("회원가입 성공", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/idchk")
    // 아이디 중복 검사 처리 메소드
    public ResponseEntity<String> memberCheckIdMethod(@RequestParam("memId") String memId) {
        int memIdCount = memberService.selectCheckId(memId);
        if(memIdCount > 0) {
            return new ResponseEntity<String>("dup", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("ok", HttpStatus.OK);
        }
    }

/*

    // 회원정보 수정 처리 메소드
    @PutMapping
    public ResponseEntity<?> memberUpdateMethod() {}

    // 회원 탈퇴 처리 메소드
    @PutMapping("/remove")
    public ResponseEntity<?> memberRemoveMethod() {}

    // 회원 상태정보 수정 처리 메소드 (관리자)
    @PutMapping("/status")
    public ResponseEntity<?> memberStatusUpdateMethod() {}

    @GetMapping
    // 회원 목록 출력 처리 메소드
    public List memberListMethod() {}

    @GetMapping("/mdetail")
    // 회원 상세정보 출력 처리 메소드 (관리자)
    public ResponseEntity<?> memberDetailViewMethod() {}

    @GetMapping("/minfo")
    // 마이페이지(내 정보) 출력 처리 메소드
    public ResponseEntity<?> memberInfoMethod() {}

    @GetMapping("/fid")
    // 아이디 찾기 처리 메소드
    public ResponseEntity<?> memberFindIdMethod() {}

    @PutMapping("/fpwd")
    // 비밀번호 찾기 처리 메소드
    public ResponseEntity<?> memberFindPwdMethod() {}
*/


}
