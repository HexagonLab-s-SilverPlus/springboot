package com.hexalab.silverplus.member.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import com.hexalab.silverplus.member.model.service.MemberFilesService;
import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @PostMapping("/enroll")
    public ResponseEntity<String> memberEnrollMethod(
            @ModelAttribute Member member, HttpServletRequest request,
            @RequestParam(name="memFiles", required = false) MultipartFile[] memFiles) {
        try {
            log.info("전송온 member 데이터 확인 : " + member);    // 전송온 member 데이터 확인
            // 패스워드 암호화 처리
            member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            log.info("member" + member);    // 암호화처리 정상 작동 확인

            member.setMemUUID(UUID.randomUUID().toString());

            memberService.insertMember(member);
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
            log.info("저장할 member 객체 확인 : {}", member);

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

    // 회원 탈퇴 처리 메소드
    @PutMapping("/remove/{memId}")
    public ResponseEntity memberRemoveMethod(@PathVariable String memId) {
        try {
            if (memberService.removeByMemId(memId) == 1) {
                return ResponseEntity.ok().build();
            } else {
                log.info("회원 탈퇴 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    // 회원정보 수정 처리 메소드
    @PutMapping
    public ResponseEntity memberUpdateMethod(@ModelAttribute Member member, HttpServletRequest request) {
        log.info("수정 메소드 작동, 전달온 값 확인 : {}", member);
        try {
            // 요청 헤더에서 기존 비밀번호 정보 추출
            String OriginalPassword = request.getHeader("OriginalPassword");
            // 비밀번호 변경 요청 시
            if (member.getMemPw() != null && member.getMemPw().length() > 0) {
                // 수정하고자 하는 비밀번호 암호화처리
                member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            } else {
                // 비밀번호 변경 요청 아닐 시 기존 비밀번호 저장처리
                member.setMemPw(OriginalPassword);
            }

//            member.setMemChangeStatus(new LocalDateTime(System.currentTimeMillis()));



            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원정보 수정 실패 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/adminList")
    // 회원 목록 출력 처리 메소드
    public List<Member> memberListMethod(@ModelAttribute Search search) {
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));

        try {
            List<Member> list = new ArrayList<Member>();
            if(search.getAction().equals("all")){
                search.setListCount(memberService.selectAllCount());
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(전체) : {}", list);
            } else if (search.getAction().equals("memId")) {
                search.setListCount(memberService.selectMemIdCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(아이디로 검색) : {}", list);
            } else if (search.getAction().equals("memName")) {
                search.setListCount(memberService.selectMemNameCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(이름으로 검색) : {}", list);
            } else if (search.getAction().equals("memStatus")) {
                search.setListCount(memberService.selectMemStatusCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(계정 상태로 검색) : {}", list);
            } else if (search.getAction().equals("memType")) {
                search.setListCount(memberService.selectMemTypeCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(계정 타입으로 검색) : {}", list);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원목록 출력 실패 : {}", e.getMessage());
            return null;
        }

    }

/*





    // 회원 상태정보 수정 처리 메소드 (관리자)
    @PutMapping("/status")
    public ResponseEntity<?> memberStatusUpdateMethod() {}



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
