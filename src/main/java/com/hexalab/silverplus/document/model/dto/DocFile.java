package com.hexalab.silverplus.document.model.dto;

import com.hexalab.silverplus.document.jpa.entity.DocFileEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocFile {
    private String dfId;
    private String dfOriginalName;
    private String dfRename;
    private String docId;

    // DTO -> Entity 변환
    public DocFileEntity toEntity() {
        return DocFileEntity.builder()
                .dfId(this.dfId)
                .dfOriginalName(this.dfOriginalName)
                .dfRename(this.dfRename)
                .docId(this.docId)
                .build();
    }
}
