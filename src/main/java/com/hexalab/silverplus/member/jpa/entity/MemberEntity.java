package com.hexalab.silverplus.member.jpa.entity;

import com.hexalab.silverplus.member.model.dto.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "MEMBER")
@Entity
public class MemberEntity {

    @Id
    @Column(name = "MEM_UUID", nullable = false)
    private String memUUID;    // MEM_UUID	VARCHAR2(100BYTE)
    @Column(name = "MEM_ID")
    private String memId;       // MEM_ID	VARCHAR2(50 BYTE)
    @Column(name = "MEM_PW", nullable = false)
    private String memPw;       // MEM_PW	VARCHAR2(50 BYTE)
    @Column(name = "MEM_NAME", nullable = false)
    private String memName;     // MEM_NAME	VARCHAR2(50 BYTE)
    @Column(name = "MEM_TYPE", nullable = false)
    private String memType;     // MEM_TYPE	VARCHAR2(30 BYTE)
    @Column(name = "MEM_EMAIL")
    private String memEmail;        // MEM_EMAIL	VARCHAR2(50 BYTE)
    @Column(name = "MEM_ADDRESS", nullable = false)
    private String memAddress;      // MEM_ADDRESS	VARCHAR2(300 BYTE)
    @Column(name = "MEM_CELLPHONE")
    private String memCellphone;        // MEM_CELLPHONE	VARCHAR2(50 BYTE)
    @Column(name = "MEM_PHONE")
    private String memPhone;        // MEM_PHONE	VARCHAR2(50 BYTE)
    @Column(name = "MEM_RNN", nullable = false)
    private String memRnn;      // MEM_RNN	VARCHAR2(50 BYTE)
    @Column(name = "MEM_GOV_CODE")
    private String memGovCode;      // MEM_GOV_CODE	VARCHAR2(50 BYTE)
    @Column(name = "MEM_STATUS", nullable = false, columnDefinition = "ACTIVE")
    private String memStatus;       // MEM_STATUS	VARCHAR2(50 BYTE)
    @Column(name = "MEM_ENROLL_DATE", nullable = false)
    private Date memEnrollDate;     // MEM_ENROLL_DATE	TIMESTAMP(6)
    @Column(name = "MEM_CHANGE_STATUS")
    private Date memChangeStatus;       //MEM_CHANGE_STATUS	TIMESTAMP(6)
    @Column(name = "MEM_FAMILY_APPROVAL")
    private String memFamilyApproval;       // MEM_FAMILY_APPROVAL	CHAR(1 BYTE)
    @Column(name = "MEM_SOCIAL_KAKAO", columnDefinition = "N")
    private String memSocialKakao;      // MEM_SOCIAL_KAKAO	CHAR(1 BYTE)
    @Column(name = "MEM_KAKAO_EMAIL")
    private String memKakaoEmail;       // MEM_KAKAO_EMAIL	VARCHAR2(50 BYTE)
    @Column(name = "MEM_SOCIAL_NAVER", columnDefinition = "N")
    private String memSocialNaver;      // MEM_SOCIAL_NAVER	CHAR(1 BYTE)
    @Column(name = "MEM_NAVER_EMAIL")
    private String memNaverEmail;       // MEM_NAVER_EMAIL	VARCHAR2(50 BYTE)
    @Column(name = "MEM_SOCIAL_GOOGLE", columnDefinition = "N")
    private String memSocialGoogle;     // MEM_SOCIAL_GOOGLE	CHAR(1 BYTE)
    @Column(name = "MEM_GOOGLE_EMAIL")
    private String memGoogleEmail;      // MEM_GOOGLE_EMAIL	VARCHAR2(50 BYTE)
    @Column(name = "MEM_UUID_FAM")
    private String memUUIDFam;      // MEM_UUID_FAM	VARCHAR2(100 BYTE)
    @Column(name = "MEM_UUID_MGR")
    private String memUUIDMgr;      // MEM_UUID_MGR	VARCHAR2(100 BYTE)

    @PrePersist
    public void prePersist() {
        memUUID = UUID.randomUUID().toString();
        memEnrollDate = new java.sql.Date(System.currentTimeMillis());
    }

    public Member toDto() {
        return Member.builder()
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
                .memGovCode(memGovCode)
                .memStatus(memStatus)
                .memFamilyApproval(memFamilyApproval)
                .memSocialKakao(memSocialKakao)
                .memKakaoEmail(memKakaoEmail)
                .memSocialNaver(memSocialNaver)
                .memNaverEmail(memNaverEmail)
                .memSocialGoogle(memSocialGoogle)
                .memGoogleEmail(memGoogleEmail)
                .memUUIDFam(memUUIDFam)
                .memUUIDMgr(memUUIDMgr)
                .build();
    }
}
