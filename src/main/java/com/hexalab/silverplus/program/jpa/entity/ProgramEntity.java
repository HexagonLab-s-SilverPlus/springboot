package com.hexalab.silverplus.program.jpa.entity;

import com.hexalab.silverplus.program.model.dto.Program;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SENIOR_PROGRAM")
@Entity
public class ProgramEntity {
    @Id
    @Column(name = "SNR_PROGRAM_ID")
    private String snrProgramId;  //    SNR_PROGRAM_ID	VARCHAR2(100 BYTE)
    @Column(name = "SNR_TITLE")
    private String snrTitle;    //    SNR_TITLE	VARCHAR2(255 BYTE)
    @Column(name = "SNR_CONTENT")
    private String snrContent;  //    SNR_CONTENT	CLOB
    @Column(name = "SNR_STARTED_AT")
    private Timestamp snrStartedAt; //    SNR_STARTED_AT	TIMESTAMP(6)
    @Column(name = "SNR_ENDED_AT")
    private Timestamp snrEndedAt;   //    SNR_ENDED_AT	TIMESTAMP(6)
    @Column(name = "SNR_ORG_NAME")
    private String snrOrgName;  //    SNR_ORG_NAME	VARCHAR2(255 BYTE)
    @Column(name = "SNR_ORG_PHONE")
    private String snrOrgPhone; //    SNR_ORG_PHONE	VARCHAR2(25 BYTE)
    @Column(name = "SNR_MGR_EMAIL")
    private String snrMgrEmail; //    SNR_MGR_EMAIL	VARCHAR2(255 BYTE)
    @Column(name = "SNR_CREATED_AT")
    private Timestamp snrCreatedAt; //    SNR_CREATED_AT	TIMESTAMP(6)
    @Column(name = "SNR_UPDATED_AT")
    private Timestamp snrUpdatedAt; //    SNR_UPDATED_AT	TIMESTAMP(6)
    @Column(name = "SNR_MGR_NAME")
    private String snrMgrName;  //    SNR_MGR_NAME	VARCHAR2(50 BYTE)
    @Column(name = "SNR_UPDATED_BY")
    private String snrUpdatedBy;    //    SNR_UPDATED_BY	VARCHAR2(50 BYTE)
    @Column(name = "SNR_CREATED_BY")
    private String snrCreatedBy;    //    SNR_CREATED_BY	VARCHAR2(50 BYTE)
    @Column(name = "SNR_ORG_ADDRESS")
    private String snrOrgAddress;   //    SNR_ORG_ADDRESS	VARCHAR2(255 BYTE)

    public Program toDto() {
        return Program.builder()
                .snrProgramId(snrProgramId)
                .snrTitle(snrTitle)
                .snrContent(snrContent)
                .snrStartedAt(snrStartedAt)
                .snrEndedAt(snrEndedAt)
                .snrOrgName(snrOrgName)
                .snrOrgPhone(snrOrgPhone)
                .snrMgrEmail(snrMgrEmail)
                .snrCreatedAt(snrCreatedAt)
                .snrUpdatedAt(snrUpdatedAt)
                .snrMgrName(snrMgrName)
                .snrUpdatedBy(snrUpdatedBy)
                .snrCreatedBy(snrCreatedBy)
                .snrOrgAddress(snrOrgAddress)
                .build();
    }

}//ProgramEntity end
