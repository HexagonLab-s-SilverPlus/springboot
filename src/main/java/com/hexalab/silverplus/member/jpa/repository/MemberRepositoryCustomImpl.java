package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.jpa.entity.QMemberEntity;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private final QMemberEntity member = QMemberEntity.memberEntity;


    private StringExpression extractSubstring(StringPath column, int start, int length) {
        // 직접 SQL 표현식으로 substring 작성
        return Expressions.stringTemplate("substring({0}, {1}, {2})", column, start, length);
    }


    // 주민등록번호를 가지고 성별 추출하는 함수
    private BooleanExpression extractGenderCondition(StringPath memRnn, String gender) {
        // 주민등록번호 뒷자리 첫 번째 숫자 추출
        NumberExpression<Integer> genderCode = extractSubstring(memRnn, 8, 1).castToNum(Integer.class);

        if ("남성".equalsIgnoreCase(gender)) {
            return genderCode.in(1, 3); // 남성 코드
        } else if ("여성".equalsIgnoreCase(gender)) {
            return genderCode.in(2, 4); // 여성 코드
        } else {
            return null; // 성별 필터 없음
        }
    }

    // 주민등록번호를 가지고 나이 계산하는 함수
    private BooleanExpression calculateAgeCondition(StringPath memRnn, int minAge, int maxAge) {
        LocalDate today = LocalDate.now(); // 현재 날짜
        int currentYear = today.getYear();

        // 출생 연도를 계산하는 식
//        NumberExpression<Integer> yearOfBirth = memRnn.substring(1, 2).castToNum(Integer.class)
//                .add(
//                        memRnn.substring(8, 1).eq("1").or(memRnn.substring(8, 1).eq("2"))
//                                .when(memRnn.substring(8, 1).eq("1").or(memRnn.substring(8, 1).eq("2"))).then(1900)
//                                .otherwise(2000)
//                );

        // 출생 연도 계산
        NumberExpression<Integer> yearOfBirth = new CaseBuilder()
                .when(extractSubstring(memRnn, 8, 1).eq("1").or(extractSubstring(memRnn, 8, 1).eq("2")))
                .then(extractSubstring(memRnn, 1, 2).castToNum(Integer.class).add(1900))
                .otherwise(extractSubstring(memRnn, 1, 2).castToNum(Integer.class).add(2000));

        // 나이 계산
        NumberExpression<Integer> calculatedAge = yearOfBirth.subtract(currentYear).multiply(-1);

        // 나이 범위 조건
        return calculatedAge.goe(minAge).and(calculatedAge.loe(maxAge));
    }

    // 아이디 중복 체크용 쿼리문
    @Override
    public long selectCheckId(String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memId.eq(memId))
                .fetchCount();
    }

    // 아이디로 검색하는 쿼리문
    @Override
    public MemberEntity findByMemId(String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memId.eq(memId))
                .fetchOne();
    }

    // 탈퇴처리 쿼리문
    @Override
    public long removeByMemId(String memId) {
        return queryFactory
                .update(member)
                .set(member.memStatus, "REMOVED")
                .where(member.memId.eq(memId))
                .execute();
    }

    // 검색(관리자용)
    // 검색 목록 출력 쿼리문(관리자용)
    @Override
    public List<MemberEntity> selectAllMember(Pageable pageable, Search search) {
        entityManager.clear();
        List<MemberEntity> list = new ArrayList<>();
        if(search.getAction().equals("all")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN"))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            log.info("조회하는 값 확인(전체) : {}", list);
        } else if (search.getAction().equals("아이디")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memId.like("%" + search.getKeyword() + "%")))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("이름")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memName.like("%" + search.getKeyword() + "%")))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("계정상태")) {
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memStatus.eq(search.getKeyword())))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (search.getAction().equals("계정타입"))
            list = queryFactory
                    .selectFrom(member)
                    .where(member.memType.ne("ADMIN").and(member.memType.eq(search.getKeyword())))
                    .orderBy(member.memEnrollDate.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        return list;
    }

    // 회원 전체목록 카운트하는 쿼리문
    public long selectAllCount(){
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN"))
                .fetchCount();
    }

    // 회원 아이디로 카운트하는 쿼리문
    @Override
    public long selectMemIdCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memId.like("%" + keyword + "%")))
                .fetchCount();
    }

    // 회원 이름으로 카운트하는 쿼리문
    @Override
    public long selectMemNameCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memName.like("%" + keyword + "%")))
                .fetchCount();
    }

    // 계정 상태로 카운트하는 쿼리문
    @Override
    public long selectMemStatusCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memStatus.eq(keyword)))
                .fetchCount();
    }

    // 회원 타입으로 카운트하는 쿼리문
    @Override
    public long selectMemTypeCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.ne("ADMIN").and(member.memType.eq(keyword)))
                .fetchCount();
    }


    // 아이디 비빌번호 찾기
    // 아이디 찾기(이메일 인증) 쿼리문
    @Override
    public boolean findByEmailName(String memEmail, String memName) {
        return queryFactory
                .selectFrom(member)
                .where(member.memEmail.eq(memEmail).and(member.memName.eq(memName)))
                .fetch().isEmpty();     // 조회 안될경우 true 리턴
    }

    // 아이디 찾기(휴대전화 인증) 쿼리문
    @Override
    public boolean findByPhoneName(String memCellPhone, String memName) {
        return queryFactory
                .selectFrom(member)
                .where(member.memCellphone.eq(memCellPhone).and(member.memName.eq(memName)))
                .fetch().isEmpty();     // 조회 안될경우 true 리턴
    }

    // 비밀번호 찾기(이메일 인증) 쿼리문
    @Override
    public boolean findByEmailId(String memEmail, String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memEmail.eq(memEmail).and(member.memId.eq(memId)))
                .fetch().isEmpty();     // 조회 안될경우 true 리턴
    }

    // 비밀번호 찾기(휴대전화 인증) 쿼리문
    @Override
    public boolean findByPhoneId(String memCellphone, String memId) {
        return queryFactory
                .selectFrom(member)
                .where(member.memCellphone.eq(memCellphone).and(member.memId.eq(memId)))
                .fetch().isEmpty();     // 조회 안될경우 true 리턴
    }

    // 회원 이름으로 조회하는 쿼리문
    @Override
    public MemberEntity findByMemName(String memName) {
        return queryFactory
                .selectFrom(member)
                .where(member.memName.eq(memName))
                .fetchOne();
    }

    // 비밀번호 재설정 쿼리문
    @Override
    public long updateMemPw(String memPw, String memUUID) {
        return queryFactory
                .update(member)
                .set(member.memPw, memPw)
                .where(member.memUUID.eq(memUUID))
                .execute();
    }


    // Social
    // 소셜 로그인 관련 쿼리문
    @Override
    public MemberEntity findBySocialPi(String provider, String socialPi) {
        switch (provider) {
            case "google" -> {
                return queryFactory.selectFrom(member)
                        .where(member.memGooglePi.eq(socialPi))
                        .fetchOne();
            }
            case "naver" -> {
                return queryFactory.selectFrom(member)
                        .where(member.memNaverPi.eq(socialPi))
                        .fetchOne();
            }
            case "kakao" -> {
                return queryFactory.selectFrom(member)
                        .where(member.memKakaoPi.eq(socialPi))
                        .fetchOne();
            }
        }
        return null;
    }

    // 소셜 연동 처리 쿼리문
    @Override
    public long updateSocial(Boolean linking, String provider, String socialPi, String memUUID) {
        log.info("넘어오는 값 확인(repositoryCustomImpl.updateSocial)   linking: {}, provider: {}, socialPi: {}, memUUID: {}", linking, provider, socialPi, memUUID);
        if (linking) {
            log.info("소셜 연동 관련 쿼리문 작동 확인(repositoryCustomImpl.updateSocial)");
            switch (provider) {
                case "google" -> {
                    log.info("소셜 연동 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - google");
                    return queryFactory.update(member)
                            .set(member.memSocialGoogle, "Y")
                            .set(member.memGooglePi, socialPi)
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
                case "naver" -> {
                    log.info("소셜 연동 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - naver");
                    return queryFactory.update(member)
                            .set(member.memSocialNaver, "Y")
                            .set(member.memNaverPi, socialPi)
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
                case "kakao" -> {
                    log.info("소셜 연동 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - kakao");
                    return queryFactory.update(member)
                            .set(member.memSocialKakao, "Y")
                            .set(member.memKakaoPi, socialPi)
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
            }
        } else {
            log.info("소셜 연동해제 관련 쿼리문 작동 확인(repositoryCustomImpl.updateSocial)");
            switch (provider) {
                case "google" -> {
                    log.info("소셜 연동해제 관련 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - google");
                    return queryFactory.update(member)
                            .set(member.memSocialGoogle, "N")
                            .set(member.memGooglePi, Expressions.nullExpression(String.class))
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
                case "naver" -> {
                    log.info("소셜 연동해제 관련 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - naver");
                    return queryFactory.update(member)
                            .set(member.memSocialNaver, "N")
                            .set(member.memNaverPi, Expressions.nullExpression(String.class))
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
                case "kakao" -> {
                    log.info("소셜 연동해제 관련 쿼리문 작동 확인(repositoryCustomImpl.updateSocial) - kakao");
                    return queryFactory.update(member)
                            .set(member.memSocialKakao, "N")
                            .set(member.memKakaoPi, Expressions.nullExpression(String.class))
                            .where(member.memUUID.eq(memUUID))
                            .execute();
                }
            }
        }
        return 0;
    }

    // 페이스 로그인 용 쿼리문
    @Override
    public MemberEntity findByProfile(String memSeniorProfile) {
        return queryFactory
                .selectFrom(member)
                .where(member.memSeniorProfile.eq(memSeniorProfile))
                .fetchOne();
    }




    // management
    // 검색 처리용 쿼리문(담당자용)
    @Override
    public List<MemberEntity> selectAllSenior(Pageable pageable, Search search) {
        List<MemberEntity> list = new ArrayList<>();
        switch (search.getAction()) {
            case "all" -> {
                list = queryFactory
                        .selectFrom(member)
                        .where(member.memType.eq("SENIOR"))
                        .orderBy(member.memEnrollDate.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
                log.info("조회하는 값 확인(전체)(selectAllSenior) : {}", list);
            }
            case "이름" -> {
                list = queryFactory
                        .selectFrom(member)
                        .where(member.memType.eq("SENIOR").and(member.memName.like("%" + search.getKeyword() + "%")))
                        .orderBy(member.memEnrollDate.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
                log.info("조회하는 값 확인(이름)(selectAllSenior) : {}", list);
            }
            case "성별" -> {
                list = queryFactory
                        .selectFrom(member)
                        .where(member.memType.eq("SENIOR").and(extractGenderCondition(member.memRnn, search.getKeyword())))
                        .orderBy(member.memEnrollDate.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
                log.info("조회하는 값 확인(성별)(selectAllSenior) : {}", list);
            }
            case "나이" -> {
                int minAge = Integer.parseInt(search.getKeyword());
                int maxAge;
                if (minAge == 100) {
                    maxAge = 99999;
                } else {
                    maxAge = minAge + 9;
                }
                list = queryFactory
                        .selectFrom(member)
                        .where(member.memType.eq("SENIOR").and(calculateAgeCondition(member.memRnn, minAge, maxAge)))
                        .orderBy(member.memEnrollDate.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
                log.info("조회하는 값 확인(나이)(selectAllSenior) : {}", list);
            }
            case "주소" -> {
                list = queryFactory
                        .selectFrom(member)
                        .where(member.memType.eq("SENIOR").and(member.memAddress.like("%" + search.getKeyword() + "%")))
                        .orderBy(member.memEnrollDate.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();
                log.info("조회하는 값 확인(주소)(selectAllSenior) : {}", list);
            }
        }
        return list;
    }

    // 어르신 전체목록 카운트하는 쿼리문
    public long selectAllSeniorCount(){
        return queryFactory
                .selectFrom(member)
                .where(member.memType.eq("SENIOR"))
                .fetchCount();
    }

    // 어르신 이름으로 카운트하는 쿼리문
    @Override
    public long selectSeniorNameCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.eq("SENIOR").and(member.memName.like("%" + keyword + "%")))
                .fetchCount();
    }

    // 어르신 성별로 카운트하는 쿼리문
    @Override
    public long selectSeniorGenderCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.eq("SENIOR").and(extractGenderCondition(member.memRnn, keyword)))
                .fetchCount();
    }

    // 어르신 나이로 카운트하는 쿼리문
    @Override
    public long selectSeniorAgeCount(String keyword) {
        int minAge = Integer.parseInt(keyword);
        int maxAge = minAge + 9;
        return queryFactory
                .selectFrom(member)
                .where(member.memType.eq("SENIOR").and(calculateAgeCondition(member.memRnn, minAge, maxAge)))
                .fetchCount();
    }

    // 어르신 주소로 카운트하는 쿼리문
    @Override
    public long selectSeniorAddressCount(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.memType.eq("SENIOR").and(member.memAddress.like("%" + keyword + "%")))
                .fetchCount();
    }

    // 가족 계정 승인처리 쿼리문
    @Override
    public long updateApproval(String memUUID, String status) {
        log.info("승인처리 작동확인 (updateApproval)");
        switch (status) {
            case "승인" -> {
                log.info("승인처리 작동확인 (updateApproval) : {}", status);
                return queryFactory
                        .update(member)
                        .set(member.memFamilyApproval, "APPROVED")
                        .where(member.memUUID.eq(memUUID))
                        .execute();
            }
            case "반려" -> {
                log.info("반려처리 작동확인 (updateApproval) : {}", status);
                return queryFactory
                    .update(member)
                    .set(member.memFamilyApproval, "REJECTED")
                    .where(member.memUUID.eq(memUUID))
                    .execute();
            }
            default -> {
                return 0;
            }
        }

    }


}



