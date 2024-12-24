package com.hexalab.silverplus.dashboard.jpa.entity;

import com.hexalab.silverplus.dashboard.model.dto.DashBoard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data//Getter Setter, toString, hashCode(),eauals(),ResquiredArgsConstructor 를 자동으로 생성해주는것
@AllArgsConstructor //클래스의 모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 매개변수가 없는 기본 생성자를 자동으로 생성
@Builder //, 빌더 패턴을 자동으로 생성합니다.
@Table(name = "DASHBOARD")
@Entity
public class DashBoardEntity {
    @Id
    @Column(name = "TASK_ID" ,nullable = false)
    private String taskId;
    @Column(name = "TASK_CONTENT" ,nullable = false)
    private String taskContent;
    @Column(name = "TASK_STATUS" ,nullable = false)
    private String taskStatus;
    @Column(name = "TASK_DATE" ,nullable = false)
    private Timestamp taskDate;
    @Column(name = "MEM_UUID" ,nullable = false)
    private String memUuid;



    public DashBoard toDto(){
        return DashBoard.builder()
                .taskId(taskId)
                .taskContent(taskContent)
                .taskStatus(taskStatus)
                .taskDate(taskDate)
                .memUuid(memUuid)
                .build();


    }

}
