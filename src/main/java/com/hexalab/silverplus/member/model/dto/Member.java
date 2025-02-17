package com.hexalab.silverplus.member.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data       // getter, setter, ToString, Equals, HashCode 자동생성
@AllArgsConstructor     // 매개변수 있는 생성자 자동생성
@NoArgsConstructor      // 매개변수 없는 생성자 자동생성
@Builder        // 자동 build 어노테이션
public class Member implements java.io.Serializable {

    @NotBlank
    private String memUUID;    // MEM_UUID	VARCHAR2(100BYTE)
    private String memId;       // MEM_ID	VARCHAR2(50 BYTE)
    @NotBlank
    private String memPw;       // MEM_PW	VARCHAR2(50 BYTE)
    @NotBlank
    private String memName;     // MEM_NAME	VARCHAR2(50 BYTE)
    @NotBlank
    private String memType;     // MEM_TYPE	VARCHAR2(30 BYTE)
    private String memEmail;        // MEM_EMAIL	VARCHAR2(50 BYTE)
    @NotBlank
    private String memAddress;      // MEM_ADDRESS	VARCHAR2(300 BYTE)
    private String memCellphone;        // MEM_CELLPHONE	VARCHAR2(50 BYTE)
    private String memPhone;        // MEM_PHONE	VARCHAR2(50 BYTE)
    private String memRnn;      // MEM_RNN	VARCHAR2(50 BYTE)
    private String memOrgName;      // MEM_GOV_CODE	VARCHAR2(50 BYTE)
    @NotBlank
    private String memStatus;       // MEM_STATUS	VARCHAR2(50 BYTE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp memEnrollDate;     // MEM_ENROLL_DATE	TIMESTAMP(6)
    @Null
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Timestamp memChangeStatus;       //MEM_CHANGE_STATUS	TIMESTAMP(6)
    private String memFamilyApproval;       // MEM_FAMILY_APPROVAL	CHAR(1 BYTE)
    private String memSocialKakao;      // MEM_SOCIAL_KAKAO	CHAR(1 BYTE)
    private String memKakaoEmail;       // MEM_KAKAO_EMAIL	VARCHAR2(50 BYTE)
    private String memSocialNaver;      // MEM_SOCIAL_NAVER	CHAR(1 BYTE)
    private String memNaverEmail;       // MEM_NAVER_EMAIL	VARCHAR2(50 BYTE)
    private String memSocialGoogle;     // MEM_SOCIAL_GOOGLE	CHAR(1 BYTE)
    private String memGoogleEmail;      // MEM_GOOGLE_EMAIL	VARCHAR2(50 BYTE)
    private String memUUIDFam;      // MEM_UUID_FAM	VARCHAR2(100 BYTE)
    private String memUUIDMgr;      // MEM_UUID_MGR	VARCHAR2(100 BYTE)
    private String memGooglePi;
    private String memKakaoPi;
    private String memNaverPi;
    private String memSeniorProfile;
    private String memSenFamRelationship;

    public MemberEntity toEntity() {
        return MemberEntity.builder()
                .memUUID(memUUID)
                .memId(memId)
                .memPw(memPw)
                .memName(memName)
                .memType(memType)
                .memEmail(memEmail)
                .memAddress(memAddress)
                .memCellphone(memCellphone)
                .memPhone(memPhone)
                .memRnn(memRnn)
                .memOrgName(memOrgName)
                .memStatus(memStatus)
                .memEnrollDate(memEnrollDate)
                .memChangeStatus(memChangeStatus)
                .memFamilyApproval(memFamilyApproval)
                .memSocialKakao(memSocialKakao)
                .memKakaoEmail(memKakaoEmail)
                .memSocialNaver(memSocialNaver)
                .memNaverEmail(memNaverEmail)
                .memSocialGoogle(memSocialGoogle)
                .memGoogleEmail(memGoogleEmail)
                .memUUIDFam(memUUIDFam)
                .memUUIDMgr(memUUIDMgr)
                .memGooglePi(memGooglePi)
                .memKakaoPi(memKakaoPi)
                .memNaverPi(memNaverPi)
                .memSeniorProfile(memSeniorProfile)
                .memSenFamRelationship(memSenFamRelationship)
                .build();
    }
}
