package com.hexalab.silverplus.document.jpa.entity;

import com.hexalab.silverplus.document.model.dto.DocFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "DOC_FILE")
@Entity
public class DocFileEntity {
    @Id
    @Column(name = "DF_ID")
    private String dfId;

    @Column(name = "DF_ORIGINALNAME", nullable = false)
    private String dfOriginalName;

    @Column(name = "DF_RENAME", nullable = false)
    private String dfRename;

    @Column(name = "DOC_ID", nullable = false)
    private String docId;


    // Entity -> DTO 변환
    public DocFile toDto(DocFileEntity entity) {
        return DocFile.builder()
                .dfId(entity.getDfId())
                .dfOriginalName(entity.getDfOriginalName())
                .dfRename(entity.getDfRename())
                .docId(entity.getDocId())
                .build();
    }
}
