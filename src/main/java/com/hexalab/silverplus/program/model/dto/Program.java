package com.hexalab.silverplus.program.model.dto;

import com.hexalab.silverplus.program.jpa.entity.ProgramEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Program {
    private UUID snrProgramId;  //    SNR_PROGRAM_ID	VARCHAR2(100 BYTE)
    private String snrTitle;    //    SNR_TITLE	VARCHAR2(255 BYTE)
    private String snrContent;  //    SNR_CONTENT	CLOB
    private Timestamp snrStartedAt; //    SNR_STARTED_AT	TIMESTAMP(6)
    private Timestamp snrEndedAt;   //    SNR_ENDED_AT	TIMESTAMP(6)
    private String snrOrgName;  //    SNR_ORG_NAME	VARCHAR2(255 BYTE)
    private String snrOrgPhone; //    SNR_ORG_PHONE	VARCHAR2(25 BYTE)
    private String snrMgrEmail; //    SNR_MGR_EMAIL	VARCHAR2(255 BYTE)
    private Timestamp snrCreatedAt; //    SNR_CREATED_AT	TIMESTAMP(6)
    private Timestamp snrUpdatedAt; //    SNR_UPDATED_AT	TIMESTAMP(6)
    private String snrMgrName;  //    SNR_MGR_NAME	VARCHAR2(50 BYTE)
    private String snrUpdatedBy;    //    SNR_UPDATED_BY	VARCHAR2(50 BYTE)
    private String snrCreatedBy;    //    SNR_CREATED_BY	VARCHAR2(50 BYTE)
    private String snrOrgAddress;   //    SNR_ORG_ADDRESS	VARCHAR2(255 BYTE)

    public ProgramEntity toEntity() {
        return ProgramEntity.builder()
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

}//Program end
