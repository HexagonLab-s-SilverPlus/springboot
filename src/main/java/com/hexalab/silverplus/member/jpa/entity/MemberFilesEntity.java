package com.hexalab.silverplus.member.jpa.entity;

import com.hexalab.silverplus.member.model.dto.MemberFiles;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "MEMBER_FILES")
@Entity
public class MemberFilesEntity {

    @Id
    @Column(name = "MF_ID")
    private UUID mfId;
    @Column(name = "MF_MEM_UUID")
    private String mfMemUUID;
    @Column(name = "MF_ORIGINAL_NAME")
    private String mfOriginalName;
    @Column(name = "MF_RENAME")
    private String mfRename;

    public void prPersist() {
        mfId = UUID.randomUUID();
    }

    public MemberFiles toDto() {
        return MemberFiles.builder()
                .mfId(mfId)
                .mfMemUUID(mfMemUUID)
                .mfOriginalName(mfOriginalName)
                .mfRename(mfRename)
                .build();
    }
}
