package com.hexalab.silverplus.program.model.dto;

import com.hexalab.silverplus.program.jpa.entity.ProgramFileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  ProgramFile {
    private String snrFileId;   //SNR_FILE_ID	VARCHAR2(100 BYTE)
    private String snrFileOGName;   //SNR_FILE_OG_NAME	VARCHAR2(1000 BYTE)
    private String snrFileName; //SNR_FILE_NAME	VARCHAR2(1000 BYTE)
    private String snrProgramId;    //SNR_PROGRAM_ID	VARCHAR2(100 BYTE)

    public ProgramFileEntity toEntity() {
        return ProgramFileEntity.builder()
                .snrFileId(snrFileId)
                .snrFileOGName(snrFileOGName)
                .snrFileName(snrFileName)
                .snrProgramId(snrProgramId)
                .build();
    }

}//ProgramFile end
