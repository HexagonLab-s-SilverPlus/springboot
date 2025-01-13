package com.hexalab.silverplus.member.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.querydsl.core.Tuple;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
import java.net.URLEncoder;
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
    private final FTPUtility ftpUtility;

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

    // 회원가입 처리 메소드
    @PostMapping("/enroll")
    public ResponseEntity<String> memberEnrollMethod(
            @ModelAttribute Member member, HttpServletRequest request,
            @RequestParam(name="memFiles", required = false) MultipartFile[] memFiles,
            @RequestParam(value = "seniorRelationshipData", required = false) String seniorRelationshipData,
            @RequestParam(name="orgData", required = false) String orgData) {
        try {

            log.info("전송온 member 데이터 확인 : " + member);    // 전송온 member 데이터 확인
            log.info("전송온 seniorRelationshipData 데이터 확인 : " + seniorRelationshipData);    // 전송온 seniorRelationshipData 데이터 확인
            // 패스워드 암호화 처리
            member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            log.info("member" + member);    // 암호화처리 정상 작동 확인

            member.setMemUUID(UUID.randomUUID().toString());
            member.setMemSocialKakao("N");      // 카카오 소셜 연동 여부 초기값 설정
            member.setMemKakaoEmail("N/A");
            member.setMemSocialGoogle("N");     // 구글 소셜 연동 여부 초기값 설정
            member.setMemGoogleEmail("N/A");
            member.setMemSocialNaver("N");      // 네이버 소셜 연동 여부 초기값 설정
            member.setMemNaverEmail("N/A");
            member.setMemKakaoPi("N/A");        // 카카오 소셜 고유 ID 초기값 설정
            member.setMemGooglePi("N/A");       // 구글 소셜 고유 ID 초기값 설정
            member.setMemNaverPi("N/A");        // 네이버 소셜 고유 ID 초기값 설정
            member.setMemSeniorProfile("N/A");      // 어르신 프로필파일 정보 초기값 설정
            member.setMemSenFamRelationship("N/A");     // 어르신과 가족계정의 관계정보 초기값 설정
            member.setMemFamilyApproval("N/A");     // 가족계정 승인여부 초기값 설정
            member.setMemPhone("N/A");      // 일반전화번호 초기값 설정
            member.setMemUUIDFam("N/A");        // 어르신의 가족 UUID 초기값 설정
            member.setMemUUIDMgr("N/A");        // 어르신의 담당자 UUID 초기값 설정
            if (member.getMemType().equals("MANAGER")) {    // 회원가입 하는 사용자가 담당자일 경우
                if (orgData != null) {
                    Map<String, String> orgDataSet =new ObjectMapper().readValue(orgData, Map.class);
                    String orgName = orgDataSet.get("name");
                    String orgAddress = orgDataSet.get("add");
                    member.setMemAddress(orgAddress);
                    member.setMemOrgName(orgName);
                }

            } else if(member.getMemType().equals("FAMILY")) {       // 회원가입 하는 사용자가 가족일 경우
                if (seniorRelationshipData != null) {
                    List<Map<String, String>> relationship = new ObjectMapper().readValue(seniorRelationshipData, new TypeReference<List<Map<String, String>>>() {});
                    for (Map<String, String> data : relationship) {
                        String memUUID = data.get("memUUID");
                        String relationshipData = data.get("relationship");
                        String memUUIDFam = member.getMemUUID();
                        memberService.updateSeniorFamApproval(memUUID, relationshipData, memUUIDFam);
                    }
                }
            }

            memberService.insertMember(member);
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
        if(search.getAction() == null || search.getAction().isEmpty()) {
            search.setAction("all");
        }

        try {
            List<Member> list = new ArrayList<Member>();
            if(search.getAction().equals("all")) {
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
            log.info("계산된 search 값 확인 () : {}", search);
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
    @PostMapping("/pwdupdate")
    public ResponseEntity memberUpdatePwdMethod(@RequestBody Member member) {
        log.info("전달 온 값 확인(memberUpdatePwdMethod) : {}", member);
        log.info("전달 온 값 확인(memberUpdatePwdMethod) : {}", member.getMemPw());

        if (member.getMemPw() != null && member.getMemPw().length() > 0) {
            String encodeMemPw = bCryptPasswordEncoder.encode(member.getMemPw());
            log.info("전달 온 UUID 값 확인 : ", member.getMemUUID());
            log.info("암호화 처리된 비밀번호 확인(memberUpdatePwdMethod) : {}", encodeMemPw);
            int result = memberService.updateMemPw(encodeMemPw, member.getMemUUID());
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
    public ResponseEntity<Map<String, Object>> managementListMethod(@ModelAttribute Search search, @RequestParam("memUUID")String memUUID) {
        log.info("전달 온 담당자 UUID 확인 : {}", memUUID);
        String type = memberService.selectMember(memUUID).getMemType();

//        if (search.getPageNumber()==0) {
//            search.setPageNumber(1);
//            search.setPageSize(10);
//        }
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));
        Map<String, Object> result = new HashMap<>();

        log.info("전달 온 search 값 확인(managementListMethod) : {}", search);
        try {
            List<Member> list = new ArrayList<Member>();
            if (type.equals("MANAGER")) {
                switch (search.getAction()) {
                    case "선택" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(전체)(managementListMethod)(담당자) : {}", list);
                        log.info("조회한 search 값 확인(전체) : {}", search);
                    }
                    case "이름" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(이름)(managementListMethod)(담당자) : {}", list);
                    }
                    case "성별" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(성별)(managementListMethod)(담당자) : {}", list);
                    }
                    case "나이" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(나이)(managementListMethod)(담당자) : {}", list);
                    }
                    case "주소" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(주소)(managementListMethod)(담당자) : {}", list);
                    }
                }
            } else if (type.equals("FAMILY")) {
                switch (search.getAction()) {
                    case "선택" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(전체)(managementListMethod)(가족) : {}", list);
                        log.info("조회한 search 값 확인(전체) : {}", search);
                    }
                    case "이름" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(이름)(managementListMethod)(가족) : {}", list);
                    }
                    case "성별" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(성별)(managementListMethod)(가족) : {}", list);
                    }
                    case "나이" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(나이)(managementListMethod)(가족) : {}", list);
                    }
                    case "주소" -> {
                        search.setListCount(memberService.selectSeniorCount(search.getKeyword(), memUUID, search.getAction(), type));
                        list = memberService.selectAllSenior(pageable, search, memUUID, type);
                        log.info("조회해 온 리스트 확인(주소)(managementListMethod)(가족) : {}", list);
                    }
                }
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

            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            if(sprofile != null) {
                // MemberFiles 객체 생성
                MemberFiles memberFiles = new MemberFiles();
                memberFiles.setMfId(UUID.randomUUID().toString());
                String fileName = sprofile.getOriginalFilename();
                String renamFileName = CreateRenameFileName.create(memberFiles.getMfId(), fileName);

                member.setMemUUID(UUID.randomUUID().toString());
                member.setMemSocialKakao("N");  // 소셜 연동 여부 default 값 처리
                member.setMemKakaoEmail("N/A");
                member.setMemKakaoPi("N/A");        // 카카오 소셜 고유 ID 초기값 설정
                member.setMemSocialGoogle("N");     // 소셜 연동 여부 default 값 처리
                member.setMemGoogleEmail("N/A");
                member.setMemGooglePi("N/A");       // 구글 소셜 고유 ID 초기값 설정
                member.setMemSocialNaver("N");      // 소셜 연동 여부 default 값 처리
                member.setMemNaverPi("N/A");        // 네이버 소셜 고유 ID 초기값 설정
                member.setMemNaverEmail("N/A");
                member.setMemFamilyApproval("N/A");       // 가족 승인 여부 default 값 처리
                member.setMemSeniorProfile(fileName);       // 프로필 사진 이름 저장
                member.setMemSenFamRelationship("N/A");     // 어르신과 가족계정의 관계정보 초기값 설정
                member.setMemUUIDFam("N/A");        // 어르신의 가족 UUID 초기값 설정

                memberService.insertMember(member);

                memberFiles.setMfOriginalName(fileName);
                memberFiles.setMfRename(renamFileName);
                memberFiles.setMfMemUUID(member.getMemUUID());

                File tempFile = File.createTempFile("member-", null);
                sprofile.transferTo(tempFile);

                String remoteFilePath = ftpRemoteDir + "member/profile/" + renamFileName;
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

    // 어르신 정보 수정 처리 메소드
    @PutMapping("/seniorUpdate/{memUUID}")
    public ResponseEntity managementUpdateMethod(@ModelAttribute Member member, BindingResult result,
                                                 @RequestParam(name="profile", required = false) MultipartFile sprofile) {
        log.info("수정 메소드 작동, 전달온 값 확인(managementUpdateMethod) : {}", member);
        try {
            // 비밀번호 변경 요청 시
            if (!member.getMemPw().equals(memberService.findByMemId(member.getMemId()).getMemPw()))  {
                // 수정하고자 하는 비밀번호 암호화처리
                log.info("비밀번호 변경요청처리 확인(managementUpdateMethod)");
                member.setMemPw(bCryptPasswordEncoder.encode(member.getMemPw()));
            } else {
                log.info("비밀번호 변경요청안함 처리 확인(managementUpdateMethod)");
            }

            ftpUtility.connect(ftpServer,ftpPort,ftpUsername,ftpPassword);

            if(sprofile != null) {
                // MemberFiles 객체 생성
                MemberFilesEntity mFiles = memberFilesService.findByProfileMemUuid(member.getMemUUID());
                String fileName = sprofile.getOriginalFilename();
                String renamFileName = CreateRenameFileName.create(mFiles.getMfId(), fileName);

                member.setMemSeniorProfile(fileName);       // 프로필 사진 이름 저장

                memberService.insertMember(member);

                mFiles.setMfOriginalName(fileName);
                mFiles.setMfRename(renamFileName);
                mFiles.setMfMemUUID(member.getMemUUID());

                File tempFile = File.createTempFile("member-", null);
                sprofile.transferTo(tempFile);

                String remoteFilePath = ftpRemoteDir + "member/profile/" + renamFileName;
                ftpUtility.uploadFile(tempFile.getAbsolutePath(), remoteFilePath);

                memberFilesService.insertMemberFiles(mFiles.toDto());
                tempFile.delete();
            }

            // 클라이언트로 넘어오는 "null" 값 처리
            validator.validate(member, result);
            log.info("전송보내는 memeber 객체 확인(managementUpdateMethod) : {}", member);

            memberService.updateMember(member);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원정보 수정 실패(managementUpdateMethod) : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 어르신 상세정보 출력 처리 메소드 (담당자)
    @GetMapping("/sdetail/{memUUID}")
    public ResponseEntity<Map<String, Object>> managementDetailMethod(@PathVariable String memUUID) {
        try {            
            log.info("전달온 UUID 확인(managementDetailMethod) : {}", memUUID);
            Member member = memberService.selectMember(memUUID);
            log.info("조회해 온 정보 확인(managementDetailMethod) : {}", member);
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> profileData = new HashMap<>();
            MemberFilesEntity profile = memberFilesService.findByProfileMemUuid(memUUID);

            // 파일 미리보기 코드
            // FTP 서버 연결
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);

            if (profile != null) {
                String mfRename = profile.getMfRename();

                // 파일 경로 구성
                String remoteFilePath = ftpRemoteDir + "member/profile/" + mfRename;
                log.info("다운로드 시도 - 파일 경로: {}", remoteFilePath);

                // 파일 다운로드
                File tempFile = File.createTempFile("preview-", null);
                ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

                // 파일 읽기
                byte[] fileContent = Files.readAllBytes(tempFile.toPath());
                tempFile.delete();

                // MIME 타입 결정
                String mimeType = getMimeType(profile.getMfOriginalName());
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                profileData.put("mimeType", mimeType);
                profileData.put("fileContent", fileContent);
            }


            map.put("profileData", profileData);        // 프로필 사진 데이터 전송
            map.put("member", member);      // 어르신 정보 객체 전송
            if (member.getMemUUIDFam() != null && !member.getMemUUIDFam().equals("N/A")) {
                Member familyInfo = memberService.selectMember(member.getMemUUIDFam());
                map.put("familyInfo", familyInfo);      // 가족 정보 객체 전송
            }
            if (member.getMemUUIDMgr() != null && !member.getMemUUIDMgr().equals("N/A")) {
                Member managerInfo = memberService.selectMember(member.getMemUUIDMgr());
                map.put("managerInfo", managerInfo);    // 담당자 정보 객체 전송
            }

            
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 가족 계정 승인/반려 처리 메소드
    @PutMapping("/approval/{memUUID}")
    public ResponseEntity managementApprovalMethod(@PathVariable String memUUID, @RequestParam("status") String status) {
        log.info("전달 온 승인/반려 status 값 확인 : {}", status);
        try {
            memberService.updateApproval(memUUID, status);      // memUUID = 가족계정 UUID . status = 승인 또는 반려
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 담당자가 관리하는 어르신의 가족계정 승인여부 조회
    @GetMapping("/approvalCount/{memUUID}")
    public ResponseEntity managementNeedApprovalCount(@PathVariable String memUUID) {
        int result = memberService.selectNeedApprovalCount(memUUID);
        return ResponseEntity.ok().body(result);
    }

    // 어르신 목록 확인(가족이 회원가입할 때)
    @GetMapping("/fsSearch")
    public ResponseEntity<Map<String, Object>> familyEnrollSeniorSearch(@ModelAttribute Search search) {
        if (search.getPageNumber()==0) {
            search.setPageNumber(1);
            search.setPageSize(7);
        }
        log.info("페이지 사이즈 : {}", search.getPageSize());
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));
        Map<String, Object> result = new HashMap<>();

        try {
            if(search.getAction() == null || search.getAction().isEmpty()){
                search.setAction("전체");
                search.setListCount(memberService.selectAllSeniorFamCount());
                result = memberService.selectAllSeniorFam(pageable, search);
                log.info("조회해 온 리스트 확인(전체)(familyEnrollSeniorSearch) : {}", result);
            } else if (search.getAction().equals("이름")) {
                search.setListCount(memberService.selectSeniorNameFamCount(search.getKeyword()));
                result = memberService.selectAllSeniorFam(pageable, search);
                log.info("조회해 온 리스트 확인(이름)(familyEnrollSeniorSearch) : {}", result);
            }

            result.put("search", search);
            log.info("클라이언트로 보낼 데이터 확인 : {}", result);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원목록 출력 실패 : {}", e.getMessage());
            return null;
        }
    }

    // 가족계정이 첨부한 파일목록 출력
    @GetMapping("/fflist/{memUUID}")
    public ResponseEntity<Map<String, Object>> familyFileList(@PathVariable String memUUID) {
        try {
            List<MemberFilesEntity> list = memberFilesService.findByMemUuid(memUUID);
            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 가족계정이 첨부한 파일 다운로드
    @GetMapping("/fdown")
    public ResponseEntity<Resource> familyFileDownload(@RequestParam String oFileName, @RequestParam String rFileName) {
        log.info("전달 온 데이터 확인(familyFileDownload) : {}", oFileName); // 오리지널 파일네임
        log.info("전달 온 데이터 확인(familyFileDownload) : {}", rFileName); // 리네임 파일네임

        try {
            ftpUtility.connect(ftpServer, ftpPort, ftpUsername, ftpPassword);
            // 임시파일 생성 후 FTP에서 다운로드
            File tempFile = File.createTempFile("download-", null);
            String remoteFilePath = ftpRemoteDir + "member/" + rFileName;
            ftpUtility.downloadFile(remoteFilePath, tempFile.getAbsolutePath());

            Resource resource = new FileSystemResource(tempFile);

            String encodedFileName = URLEncoder.encode(oFileName, "UTF-8").replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approvalList")
    public ResponseEntity<Map<String, Object>> selectApprovalListMethod(@ModelAttribute Search search, @RequestParam("memUUID") String memUUID) {
        if (search.getPageNumber()==0) {
            search.setPageNumber(1);
            search.setPageSize(10);
        }
        log.info("페이지 사이즈 : {}", search.getPageSize());
        Pageable pageable = PageRequest.of(search.getPageNumber() - 1, search.getPageSize(), Sort.by(Sort.Direction.ASC, "memEnrollDate"));
        Map<String, Object> result = new HashMap<>();

        try {
            search.setListCount(memberService.selectApprovalCount(memUUID));
            result = memberService.selectApprovalList(pageable, search, memUUID);
            log.info("조회해 온 리스트 확인(전체)(familyEnrollSeniorSearch) : {}", result);

            result.put("search", search);
            log.info("클라이언트로 보낼 데이터 확인 : {}", result);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("회원목록 출력 실패 : {}", e.getMessage());
            return null;
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





