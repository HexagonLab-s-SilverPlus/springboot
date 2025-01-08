package com.hexalab.silverplus.medical.model.dto;

import com.hexalab.silverplus.medical.jpa.entity.MedicalEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medical {
    private String mediId;              //MEDI_ID	VARCHAR2(100 BYTE)
    private Timestamp mediDiagDate;     //MEDI_DIAG_DATE	TIMESTAMP(6)
    private String mediDiseaseName;     //MEDI_DISEASE_NAME	VARCHAR2(255 BYTE)
    private Timestamp mediLastTreatDate;    //MEDI_LAST_TREAT_DATE	TIMESTAMP(6)
    private String mediPrivacy;         //MEDI_PRIVACY	CHAR(1 BYTE)
    private Timestamp mediCreatedAt;    //MEDI_CREATED_AT	TIMESTAMP(6)
    private Timestamp mediUpdatedAt;    //MEDI_UPDATED_AT	TIMESTAMP(6)
    private String mediSnrUUID;         //MEDI_SNR_UUID	VARCHAR2(100 BYTE)
    private String mediMgrUUID;         //MEDI_MGR_UUID	VARCHAR2(100 BYTE)

    public MedicalEntity toEntity() {
        return MedicalEntity.builder()
                .mediId(mediId)
                .mediDiagDate(mediDiagDate)
                .mediDiseaseName(mediDiseaseName)
                .mediLastTreatDate(mediLastTreatDate)
                .mediPrivacy(mediPrivacy)
                .mediCreatedAt(mediCreatedAt)
                .mediUpdatedAt(mediUpdatedAt)
                .mediSnrUUID(mediSnrUUID)
                .mediMgrUUID(mediMgrUUID)
                .build();
    }

}//Medical end
