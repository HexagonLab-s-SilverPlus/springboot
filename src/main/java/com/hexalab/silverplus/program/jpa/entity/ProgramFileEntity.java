package com.hexalab.silverplus.program.jpa.entity;

import com.hexalab.silverplus.program.model.dto.ProgramFile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SENIOR_PROGRAM_FILE")
@Entity
public class ProgramFileEntity {
    @Id
    @Column(name = "SNR_FILE_ID")
    private UUID snrFileId;   //SNR_FILE_ID	VARCHAR2(100 BYTE)
    @Column(name = "SNR_FILE_OG_NAME")
    private String snrFileOGName;   //SNR_FILE_OG_NAME	VARCHAR2(1000 BYTE)
    @Column(name = "SNR_FILE_NAME")
    private String snrFileName; //SNR_FILE_NAME	VARCHAR2(1000 BYTE)
    @Column(name = "SNR_PROGRAM_ID")
    private UUID snrProgramId;    //SNR_PROGRAM_ID	VARCHAR2(100 BYTE)

    public ProgramFile toDto() {
        return ProgramFile.builder()
                .snrFileId(snrFileId)
                .snrFileOGName(snrFileOGName)
                .snrFileName(snrFileName)
                .snrProgramId(snrProgramId)
                .build();
    }

}//ProgramFileEntity end
