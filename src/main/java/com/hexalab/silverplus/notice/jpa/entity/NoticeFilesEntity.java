package com.hexalab.silverplus.notice.jpa.entity;

import com.hexalab.silverplus.notice.model.dto.NoticeFiles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="NOTICE_FILES")
@Entity
public class NoticeFilesEntity {

    @Id
    @Column(name="NF_ID")
    private UUID nfId;                  // NF_ID	VARCHAR2(100 BYTE)
    @Column(name="NF_NOT_ID")
    private UUID nfNotId;             // NF_NOT_ID	VARCHAR2(100 BYTE)
    @Column(name="NF_ORIGINAL_NAME")
    private String nfOriginalName;      // NF_ORIGINAL_NAME	VARCHAR2(1000 BYTE)
    @Column(name="NF_RENAME")
    private String nfRename;            // NF_RENAME	VARCHAR2(1000 BYTE)

    public NoticeFiles toDto(){
        return NoticeFiles.builder()
                .nfId(nfId)
                .nfNotId(nfNotId)
                .nfOreginalName(nfOriginalName)
                .nfRename(nfRename)
                .build();
    }
}
