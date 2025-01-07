package com.hexalab.silverplus.member.controller;

import com.hexalab.silverplus.common.CreateRenameFileName;
import com.hexalab.silverplus.common.CustomValidator;
import com.hexalab.silverplus.common.FTPUtility;
import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.dto.MemberFiles;
import com.hexalab.silverplus.member.model.service.MemberFilesService;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j      // 로그
@RestController     // REST API 어노테이션
@RequestMapping("/member")
@RequiredArgsConstructor    // 자동 객체 생성(의존성 주입)
public class MemberController {

    private final MemberService memberService;
    private final RefreshService refreshService;
    private final MemberFilesService memberFilesService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final CustomValidator validator;

    private final JWTUtil jwtUtil;

    @Value("${jwt.access-token.expiration}")
    private long access_expiration;
    @Value("${jwt.refresh-token.expiration}")
    private long refresh_expiration;



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


    private String getMimeType(String fileName) {
        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(fileName));
        } catch (IOException e) {
            mimeType = null;
        }

        // 파일 확장자를 기준으로 MIME 타입 설정
        if (mimeType == null || mimeType.equals("application/octet-stream")) {
            if (fileName.endsWith(".png")) {
                mimeType = "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                mimeType = "image/jpeg";
            } else if (fileName.endsWith(".gif")) {
                mimeType = "image/gif";
            } else {
                mimeType = "application/octet-stream";
            }
        }
        return mimeType;
    }

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
            member.setMemSocialKakao("N");
            member.setMemSocialGoogle("N");
            member.setMemSocialNaver("N");
            member.setMemFamilyApproval("N");

            memberService.insertMember(member);
            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            if(memFiles != null && memFiles.length > 0) {
                for (MultipartFile mfile : memFiles) {
                    // MemberFiles 객체 생성
                    MemberFiles memberFiles = new MemberFiles();
                    memberFiles.setMfId(UUID.randomUUID().toString());

                    String fileName = mfile.getOriginalFilename();
                    String renamFileName = CreateRenameFileName.create(memberFiles.getMfId(), fileName);


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
        log.info("전달온 아이디 값 확인(memberRemoveMethod) : {}", memId);
        try {
            if (memberService.removeByMemId(memId) > 0) {
                return ResponseEntity.ok().header("Response", "success").build();
            } else {
                log.info("회원 탈퇴 실패");
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Response", "error").build();
        }

    }

    // 회원정보 수정 처리 메소드
    @PutMapping("/update/{memUUID}")
    public ResponseEntity memberUpdateMethod(@ModelAttribute Member member, BindingResult result) {
        log.info("수정 메소드 작동, 전달온 값 확인 : {}", member);
        try {
            // 비밀번호 변경 요청 시
            if (!member.getMemPw().equals(memberService.findByMemId(member.getMemId()).getMemPw()))  {
                // 수정하고자 하는 비밀번호 암호화처리
                log.info("비밀번호 변경요청처리 확인");
                member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            } else {
                log.info("비밀번호 변경요청안함 처리 확인");
            }

            // 클라이언트로 넘어오는 "null" 값 처리
            validator.validate(member, result);
            log.info("전송보내는 memeber 객체 확인(memberUpdateMethod) : {}", member);

            memberService.updateMember(member);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원정보 수정 실패 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/adminList")
    // 회원 목록 출력 처리 메소드
    public ResponseEntity<Map<String, Object>> memberListMethod(@ModelAttribute Search search) {
        if (search.getPageNumber()==0) {
           search.setPageNumber(1);
           search.setPageSize(10);
        }
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));
        Map<String, Object> result = new HashMap<>();

        log.info("전달 온 search 값 확인 : {}", search);

        try {
            List<Member> list = new ArrayList<Member>();
            if(search.getAction() == null || search.getAction().isEmpty()){
                search.setAction("all");
                search.setListCount(memberService.selectAllCount());
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(전체) : {}", list);
            } else if (search.getAction().equals("아이디")) {
                search.setListCount(memberService.selectMemIdCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(아이디로 검색) : {}", list);
            } else if (search.getAction().equals("이름")) {
                search.setListCount(memberService.selectMemNameCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(이름으로 검색) : {}", list);
            } else if (search.getAction().equals("계정상태")) {
                if (search.getKeyword().equals("활동")) {
                    search.setKeyword("ACTIVE");
                } else if (search.getKeyword().equals("휴면")) {
                    search.setKeyword("INACTIVE");
                } else if (search.getKeyword().equals("정지")) {
                    search.setKeyword("BLOCKED");
                } else if (search.getKeyword().equals("탈퇴")) {
                    search.setKeyword("REMOVED");
                }
                search.setListCount(memberService.selectMemStatusCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(계정 상태로 검색) : {}", list);
            } else if (search.getAction().equals("계정타입")) {
                if (search.getKeyword().equals("담당자")) {
                    search.setKeyword("MANAGER");
                } else if (search.getKeyword().equals("가족")) {
                    search.setKeyword("FAMILY");
                } else if (search.getKeyword().equals("어르신")) {
                    search.setKeyword("SENIOR");
                }
                search.setListCount(memberService.selectMemTypeCount(search.getKeyword()));
                list = memberService.selectAllMember(pageable, search);
                log.info("조회해 온 리스트 확인(계정 타입으로 검색) : {}", list);
            }

            result.put("list", list);
            result.put("search", search);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원목록 출력 실패 : {}", e.getMessage());
            return null;
        }

    }

    @GetMapping("/mdetail/{memUUID}")
    // 회원 상세정보 출력 처리 메소드 (관리자)
    public ResponseEntity memberDetailViewMethod(@PathVariable String memUUID) {
//        try {
//            // 파일 미리보기 코드
//            List<Map<String, Object>> fileList = new ArrayList<>();
//            List<MemberFilesEntity> mfiles = memberFilesService.findByMemUuid(memUUID);
//
//            // FTP 서버 연결
//            FTPUtility ftpUtility = new FTPUtility();
//            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);
//
//            for (MemberFilesEntity memberFilesEntity : mfiles) {
//                // 리스트에 저장하기 위한 Map 객체 생성
//                Map<String, Object> fileData = new HashMap<>();
//
//                // 파일 불러오기 위한 리네임 추출
//                String mfRename = memberFilesEntity.getMfRename();
//
//                // mfRename 값 확인
//                log.info("mfRename 값 확인: {}", mfRename);
//
//                // 파일 경로 구성
//                String remoteFilePath = ftpRemoteDir + "member/" + mfRename;
//                log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);
//
//                // 파일 다운로드
//                File tempFile = File.createTempFile("preview-", null);
//                ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());
//
//                // 파일 읽기
//                byte[] fileContent = Files.readAllBytes(tempFile.toPath());
//                tempFile.delete();
//
//                // MIME 타입 결정
//                String mimeType = getMimeType(memberFilesEntity.getMfOriginalName());
//                if (mimeType == null) {
//                    mimeType = "application/octet-stream";
//                }
//
//                // 파일 데이터 구성
//                fileData.put("fileName", memberFilesEntity.getMfOriginalName());
//                fileData.put("mimeType", mimeType);
//                fileData.put("fileContent", Base64.getEncoder().encodeToString(fileContent)); // Base64로 인코딩
//                fileList.add(fileData);
//
//            }
//
//            return ResponseEntity.ok(fileList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
        log.info("전달온 UUID 확인(memberDetailViewMethod) : {}", memUUID);
        Member member = memberService.selectMember(memUUID);
        log.info("조회해 온 정보 확인(memberDetailViewMethod) : {}", member);

        return ResponseEntity.ok(member);
    }

    @PostMapping("/fid")
    // 아이디 찾기 처리 메소드
    public ResponseEntity<String> memberFindIdMethod(@RequestBody Member member) {
        log.info("전달온 객체 확인(memberFindIdMethod) : {}", member);

        if (member.getMemEmail() != null && member.getMemEmail().length() > 0) {        // 이메일 인증으로 아이디 찾기 시도 시
            if (!memberService.findByEmailName(member.getMemEmail(), member.getMemName())) {        // 전달온 이메일 정보와 이름 정보로 DB 조회
                // DB 정보 조회 성공 시 전달온 이름 정보로 DB 에서 member 정보 조회하여 저장
                Member resultMember = memberService.findByMemName(member.getMemName());
                // 저장된 객체에서 아이디 정보만 추출하여 클라이언트로 반환
                return ResponseEntity.ok().header("Response", "success").body(resultMember.getMemId());
            } else {
                // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        } else if (member.getMemCellphone() != null && member.getMemCellphone().length() > 0) {     // 휴대전화 인증으로 아이디 찾기 시도 시
            if (!memberService.findByPhoneName(member.getMemCellphone(), member.getMemName())) {        // 전달온 휴대전화 정보와 이름 정보로 DB 조회
                // DB 정보 조회 성공 시 전달온 이름 정보로 DB 에서 member 정보 조회하여 저장
                Member resultMember = memberService.findByMemName(member.getMemName());
                // 저장된 객체에서 아이디 정보만 추출하여 클라이언트로 반환
                return ResponseEntity.ok().header("Response", "success").body(resultMember.getMemId());
            } else {
                // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        }
        // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
        return ResponseEntity.ok().header("Response", "verifyError").build();
    }

    @PostMapping("/fpwd")
    // 비밀번호 찾기 처리 메소드
    public ResponseEntity memberFindPwdMethod(@RequestBody Member member) {
        log.info("전달온 객체 확인(memberFindPwdMethod) : {}", member);

        if (member.getMemEmail() != null && member.getMemEmail().length() > 0) {        // 이메일 인증으로 비밀번호 찾기 시도 시
            if (!memberService.findByEmailId(member.getMemEmail(), member.getMemId())) {        // 전달온 이메일 정보와 아이디 정보로 DB 조회
                // 아이디 정보로 DB 조회하여 조회된 member 객체 저장
                Member resultMember = memberService.findByMemId(member.getMemId());
                // 조회된 member 객체에서 UUID 정보 추출하여 클라이언트로 반환
                return ResponseEntity.ok().header("Response", "success").body(resultMember.getMemUUID());
            } else {
                // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        } else if (member.getMemCellphone() != null && member.getMemCellphone().length() > 0) {     // 휴대전화 인증으로 비밀번호 찾기 시도 시
            if (!memberService.findByPhoneId(member.getMemCellphone(), member.getMemId())) {        // 전달온 휴대전화 정보와 아이디 정보로 DB 조회
                // 아이디 정보로 DB 조회하여 조회된 member 객체 저장
                Member resultMember = memberService.findByMemId(member.getMemId());
                // 조회된 member 객체에서 UUID 정보 추출하여 클라이언트로 반환
                return ResponseEntity.ok().header("Response", "success").body(resultMember.getMemUUID());
            } else {
                // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        }
        // DB 조회 실패시 반환(클라이언트의 AuthProvider 인터셉터를 피하기 위해 ok 결과로 반환. 헤더에 실패정보 함께 보냄)
        return ResponseEntity.ok().header("Response", "verifyError").build();
    }

    // 비밀번호 수정 처리 메소드
    @PutMapping("/pwdupdate/{memUUID}")
    public ResponseEntity memberUpdatePwdMethod(@PathVariable String memUUID, @RequestBody Member member) {
        log.info("전달 온 값 확인(memberUpdatePwdMethod) : {}", member.getMemPw());
        log.info("전달 온 값 확인(memberUpdatePwdMethod) : {}", memUUID);


        if (member.getMemPw() != null && member.getMemPw().length() > 0) {
            String encodeMemPw = bCryptPasswordEncoder.encode(member.getMemPw());
            log.info("암호화 처리된 비밀번호 확인(memberUpdatePwdMethod) : {}", encodeMemPw);
            int result = memberService.updateMemPw(encodeMemPw, memUUID);
            if (result > 0) {
                log.info("비밀번호 수정 성공 여부(memberUpdatePwdMethod) : {}", result);
                return ResponseEntity.ok().header("Response", "success").build();
            } else {
                return ResponseEntity.ok().header("Response", "failed").build();
            }
        }
        return ResponseEntity.ok().header("Response", "verifyError").build();
    }


    // 비밀번호 체크 메소드
    @PostMapping("/pwdCheck")
    public ResponseEntity memberCheckPwdMethod(@RequestBody Member member) {
        log.info("전달 온 member 객체 데이터 확인(memberCheckPwdMethod) : {}", member);
        // 전달 온 UUID 로 DB 조회
        Member resultMember = memberService.selectMember(member.getMemUUID());
        log.info("DB 에 저장된 비밀번호 확인(memberCheckPwdMethod) : {}", resultMember.getMemPw());
        // 전달 온 비밀번호 암호화처리
//        member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
//        log.info("암호화 처리한 비밀번호 확인(memberCheckPwdMethod) : {}", member.getMemPw());
        if (bCryptPasswordEncoder.matches(member.getMemPw(), resultMember.getMemPw())) {
            return ResponseEntity.ok().header("Response", "true").build();
        } else {
            return ResponseEntity.ok().header("Response", "false").build();
        }
    }


    @GetMapping("/minfo/{memUUID}")
    // 마이페이지(내 정보) 출력 처리 메소드
    public ResponseEntity memberInfoMethod(@PathVariable String memUUID) {
        log.info("전달 온 UUID 확인(memberInfoMethod) : {}", memUUID);
        if (memUUID != null && memUUID.length() > 0) {
            Member member = memberService.selectMember(memUUID);
            return ResponseEntity.ok().header("Response", "success").body(member);
        }
        return ResponseEntity.ok().header("Response", "failed").build();
    }


    // 소셜 연동해제 처리 메소드
    @PutMapping("/social/{memUUID}")
    public ResponseEntity memberSocialUpdateMethod(@PathVariable String memUUID, @RequestParam("provider") String provider) {
        log.info("전달온 provider 값 확인 (memberSocialUpdateMethod) : {}", provider);
        Member member = memberService.selectMember(memUUID);
        switch (provider) {
            case "google" -> {
                memberService.updateSocial(false, provider, member.getMemGooglePi(), memUUID);
                return ResponseEntity.ok().header("Response", "success").build();
            }
            case "naver" -> {
                memberService.updateSocial(false, provider, member.getMemNaverPi(), memUUID);
                return ResponseEntity.ok().header("Response", "success").build();
            }
            case "kakao" -> {
                memberService.updateSocial(false, provider, member.getMemKakaoPi(), memUUID);
                return ResponseEntity.ok().header("Response", "success").build();
            }
        }
        return ResponseEntity.ok().header("Response", "failed").build();
    }


    // 페이스 로그인 처리 메소드
    @PostMapping("/facelogin")
    public ResponseEntity memberFaceLoginMethod(@RequestBody Member member, HttpServletResponse response) {
        String profile = member.getMemSeniorProfile();
        Member resultMember = memberService.findByProfile(profile);

        String memId = resultMember.getMemId();

        String access = jwtUtil.generateToken(memId, "access", access_expiration);
        String refresh = jwtUtil.generateToken(memId, "refresh", refresh_expiration);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenUuid(UUID.randomUUID().toString())
                .tokenStatus("activated")
                .tokenValue(refresh)
                .tokenExpIn(refresh_expiration)
                .tokenMemUuid(resultMember.getMemUUID())
                .build();
        refreshService.save(refreshTokenEntity);

        return ResponseEntity.ok().header("Authorization", "Bearer " + access).header("Response", refresh).build();
    }

    // 어르신 목록 출력 메소드
    @GetMapping("/seniorList")
    public ResponseEntity<Map<String, Object>> managementListMethod(@ModelAttribute Search search) {
        if (search.getPageNumber()==0) {
            search.setPageNumber(1);
            search.setPageSize(10);
        }
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));
        Map<String, Object> result = new HashMap<>();

        log.info("전달 온 search 값 확인(managementListMethod) : {}", search);

        try {
            List<Member> list = new ArrayList<Member>();
            if(search.getAction() == null || search.getAction().isEmpty()){
                search.setAction("all");
                search.setListCount(memberService.selectAllSeniorCount());
                list = memberService.selectAllSenior(pageable, search);
                log.info("조회해 온 리스트 확인(전체)(managementListMethod) : {}", list);
            } else if (search.getAction().equals("이름")) {
                search.setListCount(memberService.selectSeniorNameCount(search.getKeyword()));
                list = memberService.selectAllSenior(pageable, search);
                log.info("조회해 온 리스트 확인(이름)(managementListMethod) : {}", list);
            } else if (search.getAction().equals("성별")) {
                search.setListCount(memberService.selectSeniorGenderCount(search.getKeyword()));
                list = memberService.selectAllSenior(pageable, search);
                log.info("조회해 온 리스트 확인(성별)(managementListMethod) : {}", list);
            } else if (search.getAction().equals("나이")) {
                search.setListCount(memberService.selectSeniorAgeCount(search.getKeyword()));
                list = memberService.selectAllSenior(pageable, search);
                log.info("조회해 온 리스트 확인(나이)(managementListMethod) : {}", list);
            } else if (search.getAction().equals("주소")) {
                search.setListCount(memberService.selectSeniorAddressCount(search.getKeyword()));
                list = memberService.selectAllSenior(pageable, search);
                log.info("조회해 온 리스트 확인(주소)(managementListMethod) : {}", list);
            }

            result.put("list", list);
            result.put("search", search);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원목록 출력 실패 : {}", e.getMessage());
            return null;
        }
    }

    // 어르신 등록 메소드
    @PostMapping("/sregist")
    public ResponseEntity managementRegistMethod(@ModelAttribute Member member,
                                       @RequestParam("profile") MultipartFile sprofile) {
        try {
            log.info("전송온 member 데이터 확인(managementRegistMethod) : {}", member);    // 전송온 member 데이터 확인
            // 패스워드 암호화 처리
            member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            log.info("member(managementRegistMethod) : {}", member);    // 암호화처리 정상 작동 확인


            FTPUtility ftpUtility = new FTPUtility();
            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            if(sprofile != null) {
                // MemberFiles 객체 생성
                MemberFiles memberFiles = new MemberFiles();
                memberFiles.setMfId(UUID.randomUUID().toString());
                String fileName = sprofile.getOriginalFilename();
                String renamFileName = CreateRenameFileName.create(memberFiles.getMfId(), fileName);

                member.setMemUUID(UUID.randomUUID().toString());
                member.setMemSocialKakao("N");  // 소셜 연동 여부 default 값 처리
                member.setMemSocialGoogle("N");     // 소셜 연동 여부 default 값 처리
                member.setMemSocialNaver("N");      // 소셜 연동 여부 default 값 처리
                member.setMemFamilyApproval("N");       // 가족 승인 여부 default 값 처리
                member.setMemSeniorProfile(fileName);       // 프로필 사진 이름 저장

                memberService.insertMember(member);

                memberFiles.setMfOriginalName(fileName);
                memberFiles.setMfRename(renamFileName);
                memberFiles.setMfMemUUID(member.getMemUUID());

                File tempFile = File.createTempFile("member-", null);
                sprofile.transferTo(tempFile);

                String remoteFilePath = ftpRemoteDir + "member/" + renamFileName;
                ftpUtility.uploadFile(tempFile.getAbsolutePath(), remoteFilePath);

                memberFilesService.insertMemberFiles(memberFiles);
                tempFile.delete();
            }
            log.info("저장할 member 객체 확인(managementRegistMethod) : {}", member);

            return new ResponseEntity<String>("어르신 등록 성공", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}





