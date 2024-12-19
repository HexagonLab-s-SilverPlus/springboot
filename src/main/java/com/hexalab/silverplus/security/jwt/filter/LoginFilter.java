package com.hexalab.silverplus.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexalab.silverplus.member.jpa.repository.MemberRepository;
import com.hexalab.silverplus.member.model.service.MemberService;
import com.hexalab.silverplus.security.jwt.filter.input.InputMember;
import com.hexalab.silverplus.security.jwt.filter.output.CustomUserDetails;
import com.hexalab.silverplus.security.jwt.jpa.entity.RefreshToken;
import com.hexalab.silverplus.security.jwt.model.service.RefreshService;
import com.hexalab.silverplus.security.jwt.model.service.UserService;
import com.hexalab.silverplus.security.jwt.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final RefreshService refreshService;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    private final long access_expiration;
    private final long refresh_expiration;

    // 생성자를 통한 의존성 주입
    public LoginFilter(UserService userService,
                       RefreshService refreshService, MemberRepository memberRepository,
                       AuthenticationManager authenticationManager,
                       JWTUtil jwtUtil,
                       @Value("${jwt.access-token.expiration}") long access_expiration,
                       @Value("${jwt.refresh-token.expiration}") long refresh_expiration) {
        this.userService = userService;
        this.refreshService = refreshService;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.access_expiration = access_expiration;
        this.refresh_expiration = refresh_expiration;

        // 로그인 url 요청에 대한 앤드포인트 설정
        setFilterProcessesUrl("/login");
    }

    // 요청 본문을 읽는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("LoginFilter in attemptAuthentication");
        log.info("요청 메서드: {}", request.getMethod());
        log.info("요청 경로: {}", request.getRequestURI());
        log.info("요청 Content-Type: {}", request.getContentType());
        try {
            InputMember loginData = new ObjectMapper().readValue(request.getInputStream(), InputMember.class);
            log.info("login data: {}", loginData);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginData.getMemId(), loginData.getMemPw());
            log.info("authToken: {}", authToken);
            log.info("authenticationManager: {}", authenticationManager);
            log.info("authentication : {}", authenticationManager.authenticate(authToken));
            return authenticationManager.authenticate(authToken);

/*            // 요청 본문 읽기 (InputStream은 한 번만 읽을 수 있으므로 먼저 저장)
            String requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("요청 본문: {}", requestBody);

            // JSON 본문 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            InputMember loginData = objectMapper.readValue(requestBody, InputMember.class);

            // 사용자 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginData.getUserId(), loginData.getUserPwd());

            // 사용자 정보 조회
            Member member = memberService.selectMember(loginData.getUserId());
            if (member == null) {
                log.info("회원 정보가 없습니다.");
                throw new DisabledException("사용할 수 없는 계정입니다.");
            }

            // 실제 인증 수행
            return authenticationManager.authenticate(authToken);*/
        } catch (AuthenticationException e) {
            log.info("인증확인");
            log.error("인증 예외 발생: {}", e.getMessage());
            throw new AuthenticationServiceException("인증 처리 중 오류 발생", e);
        } catch (IOException e) {
            log.error("요청 본문 읽기 오류: {}", e.getMessage());
            throw new RuntimeException("Failed to parse login request", e);
        }
    }

    // 로그인 성공시 실행되는 메소드
    // 인증된 사용자 정보를 바탕으로 jwt token 을 생성하고, 이를 응답(response) 헤더에 추가함
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // 인증 절차가 성공되면 자동으로 구동됨
        log.info("successfulAuthentication");
        // 인증 객체에서 CustomUserDetails 를 추출함
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        // CustomUserDetails 에서 사용자 이름(아이디, 이메일)을 추출함
        String username = customUserDetails.getUsername();      // 로그인 한 userId
        String memUUID = memberRepository.findByMemId(username).getMemUUID();
        log.info("successfulAuthentication : {}", username);

        // 로그인이 성공했을 때 이므로, jwt 토큰을 생성함
        String access = jwtUtil.generateToken(username, "access", access_expiration);
        String refresh = jwtUtil.generateToken(username, "refresh", refresh_expiration);
        log.info("access_token : {}", access);
        log.info("refresh_token : {}", refresh);
        log.info("access_expiration : {}", access_expiration);

        // 새로 로그인 할때  DB에 토큰정보가 있을 시 기존 리프레시 토큰정보 삭제
        if (refreshService.findByMemUuidCount(memUUID) > 0) {
            log.info("토큰정보 삭제 코드 작동 확인");
            refreshService.deleteByRefreshTokenUuid(memUUID);
        }

        // 리프레시토큰은 데이터베이스에 저장 처리
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .tokenUuid(UUID.randomUUID().toString())
                .tokenStatus("activated")
                .tokenValue(refresh)
                .tokenExpIn(refresh_expiration)
                .tokenMemUuid(memUUID)
                .build();

        refreshService.save(refreshTokenEntity);
        log.info("loginfilter access : {}", access);
        log.info("loginfilter refresh : {}", refresh);
        // 응답 객체 헤더에 JWT를 "Authorization", "Bearer " 값 뒤에 엑세스 토큰 추가
        response.addHeader("Authorization", "Bearer " + access);

        // 추가로 클라이언트로 보낼 정보(데이터) 처리
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("refreshToken", refresh);

        // 응답객체에 응답 컨텐츠 타입을 설정함
        response.setContentType("application/json");

        log.info("responseBody : " + responseBody.toString());
        log.info("responseHeader : " + response.getHeader("Authorization"));

        // ObjectMapper 를 사용해서 Map 을 JSON 문자열로 변환함
        String jsonStr = new ObjectMapper().writeValueAsString(responseBody);

        // 응답객체 바디에 JSON 문자열을 추가해서 로그인 요청한 클라이언트에게 출력스트림으로 내보냄
        PrintWriter out = response.getWriter();
        out.write(jsonStr);
        out.flush();    // 출력스트림 청소
        out.close();        // 출력스트림 닫기
    }

    // 로그인 실패시 실행되는 메소드
    // 실패할 경우 HTTP 상태 코드 401 을 반환함
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {        // import org.springframework.security.core.AuthenticationException;
        // 인증조회가 실패하면 자동 구동됨
        // 전달받은 exception 객체로 부터 최종 예외 원인을 찾아냄
        Throwable rootCause = exception.getCause();
        while (rootCause != null && rootCause != exception) {
            rootCause = rootCause.getCause();
        }

        // 전달받은 exception 을 기반으로 오류메세지를 설정함
        String message = null;
        if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
            message = "계정이 존재하지 않거나 잘못된 계정입니다.";
        } else if (exception instanceof DisabledException) {
            message = "계정이 비활성화되었습니다. 현재 로그인 할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof LockedException) {
            message = "계정이 잠겨 있습니다. 5회 접속 요청 실패이므로 10분 뒤에 다시 접속하세요.";
        } else {
            // 다른 예외들은 모두
            message = "로그인 인증에 실패했습니다. 관리자에게 문의하세요.";
        }

        // 응답 데이터 준비. 클라이언트 뷰 페이지에 사용할 데이터들로 저장 처리
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("path", request.getRequestURI());
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("error", "Unauthorized");

        // 응답 처리
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("utf-8");     // 응답 데이터에 한글이 존재할 경우

        try {
            String jsonStr = new ObjectMapper().writeValueAsString(responseBody);
            PrintWriter out = response.getWriter();
            out.write(jsonStr);
            out.flush();
            out.close();
        } catch (IOException ignored) {
            // 예외 발생시 출력 또는 처리내용 없음
        }
    }


//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return "OPTIONS".equalsIgnoreCase(request.getMethod());
//    }


}
