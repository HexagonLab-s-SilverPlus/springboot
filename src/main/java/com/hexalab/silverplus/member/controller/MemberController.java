package com.hexalab.silverplus.member.controller;

import com.hexalab.silverplus.member.model.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@Slf4j      // 로그
@RestController     // REST API 어노테이션
@RequestMapping("/member")
@RequiredArgsConstructor    // 자동 객체 생성(의존성 주입)
@CrossOrigin(origins = "http://localhost:3000")      // 서로 다른 포트 사용시 CrossOrigin 이슈 처리
public class MemberController {

    private final MemberService memberService;

    private final MemberFilesService memberFilesService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${uploadDir}")
    private String uploadDir;

    // 회원가입 처리 메소드
    @PostMapping
    public ResponseEntity<String> memberEnrollMethod(
            @ModelAttribute Member member, HttpServletRequest request){
//            @RequestParam(name="mfiles", required = false) MultipartFile[] mfiles) {
        log.info("전송온 member 데이터 확인 : " + member);    // 전송온 member 데이터 확인
        // 패스워드 암호화 처리
        member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
        log.info("member" + member);    // 암호화처리 정상 작동 확인

        // 파일첨부 처리
        String savePath = uploadDir + File.separator + "member";
        log.info("savePath" + savePath);    // 파일 저장 폴더경로 설정 정상 지정 확인
        // MemberFiles 객체 생성
        MemberFiles memberFiles = new MemberFiles();
        File directory = new File(savePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

//        for (MultipartFile mfile : mfiles) {
//            if(!mfile.isEmpty()) {
//                try {
//                    memberFiles.setMfOriginalName(mfile.getOriginalFilename());
//                    String renameFilename = member.getMemId() + "_" + mfile.getOriginalFilename() + System.currentTimeMillis();
//                    memberFiles.setMfRename(renameFilename);
//                    memberFiles.setMfId(member.getMemUUID());
//                    mfile.transferTo(new File(savePath, renameFilename));
//                    memberFilesService.insertMemberFiles(memberFiles);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return new ResponseEntity<String>("파일업로드 오류 발생", HttpStatus.BAD_REQUEST);
//                }
//            }
//        }

        memberService.insertMember(member);
        return new ResponseEntity<String>("회원가입 성공", HttpStatus.OK);
    }

/*
    // 로그인 처리 메소드
    @PostMapping("/login")
    public ResponseEntity<?> memberLoginMethod() {}

    // 로그아웃 처리 메소드
    @GetMapping("/logout")
    public ResponseEntity<?> memberLogoutMethod() {}

    // 회원정보 수정 처리 메소드
    @PutMapping
    public ResponseEntity<?> memberUpdateMethod() {}

    // 회원 탈퇴 처리 메소드
    @PutMapping("/remove")
    public ResponseEntity<?> memberRemoveMethod() {}

    // 회원 상태정보 수정 처리 메소드 (관리자)
    @PutMapping("/status")
    public ResponseEntity<?> memberStatusUpdateMethod() {}

    @PostMapping("/idchk")
    // 아이디 중복 검사 처리 메소드
    public ResponseEntity<?> memberCheckIdMethod() {}

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
