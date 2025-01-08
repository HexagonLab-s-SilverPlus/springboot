package com.hexalab.silverplus.medical.jpa.entity;

import com.hexalab.silverplus.medical.model.dto.Medical;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "MEDICAL")
@Entity
public class MedicalEntity {
    @Id
    @Column(name = "MEDI_ID")
    private String mediId;              //MEDI_ID	VARCHAR2(100 BYTE)
    @Column(name = "MEDI_DIAG_DATE")
    private Timestamp mediDiagDate;     //MEDI_DIAG_DATE	TIMESTAMP(6)
    @Column(name = "MEDI_DISEASE_NAME")
    private String mediDiseaseName;     //MEDI_DISEASE_NAME	VARCHAR2(255 BYTE)
    @Column(name = "MEDI_LAST_TREAT_DATE")
    private Timestamp mediLastTreatDate;    //MEDI_LAST_TREAT_DATE	TIMESTAMP(6)
    @Column(name = "MEDI_PRIVACY")
    private String mediPrivacy;         //MEDI_PRIVACY	CHAR(1 BYTE)
    @Column(name = "MEDI_CREATED_AT")
    private Timestamp mediCreatedAt;    //MEDI_CREATED_AT	TIMESTAMP(6)
    @Column(name = "MEDI_UPDATED_AT")
    private Timestamp mediUpdatedAt;    //MEDI_UPDATED_AT	TIMESTAMP(6)
    @Column(name = "MEDI_SNR_UUID")
    private String mediSnrUUID;         //MEDI_SNR_UUID	VARCHAR2(100 BYTE)
    @Column(name = "MEDI_MGR_UUID")
    private String mediMgrUUID;         //MEDI_MGR_UUID	VARCHAR2(100 BYTE)

    public Medical toDto() {
        return Medical.builder()
                .mediId(this.mediId)
                .mediDiagDate(this.mediDiagDate)
                .mediDiseaseName(this.mediDiseaseName)
                .mediLastTreatDate(this.mediLastTreatDate)
                .mediPrivacy(this.mediPrivacy)
                .mediCreatedAt(this.mediCreatedAt)
                .mediUpdatedAt(this.mediUpdatedAt)
                .mediSnrUUID(this.mediSnrUUID)
                .mediMgrUUID(this.mediMgrUUID)
                .build();
    }//toDto end

}//MedicalEntity end
