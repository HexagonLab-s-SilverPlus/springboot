package com.hexalab.silverplus.dashboard.model.dto;


import com.hexalab.silverplus.dashboard.jpa.entity.DashBoardEntity;
import jakarta.validation.constraints.NotBlank;
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
public class DashBoard {

    private String taskId;
    private String taskContent;
    private String taskStatus;
    private Timestamp taskDate;
    private String memUuid;

    public DashBoardEntity toEntity() {
        return DashBoardEntity.builder()
                .taskId(taskId)
                .taskContent(taskContent)
                .taskStatus(taskStatus)
                .taskDate(taskDate)
                .memUuid(memUuid)
                .build();
    }

}
