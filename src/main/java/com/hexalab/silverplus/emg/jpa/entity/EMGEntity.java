package com.hexalab.silverplus.emg.jpa.entity;

import com.hexalab.silverplus.emg.model.dto.EMG;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="EMERGENCY_LOG")  //매핑할 테이블 이름 지정함
@Entity //JPA 가 관리함, 테이블의 컬럼과 DTO 클래스의 프로퍼티를 매핑하는 역할을
public class EMGEntity {

    @Id
    @Column(name="EMG_LOG_ID")
    private String emgLogId;            // 긴급 상황 고유 ID
    @Column(name="EMG_DETECTED_MOTION")
    private String emgDetectedMotion;   // 감지된 모션
    @Column(name="EMG_CAP_PATH")
    private String emgCapPath;          // 캡쳐된 이미지 경로
    @Column(name="EMG_ALERT_SENT_TO")
    private String emgAlertSentTo;      // 알림 발송 대상
    @Column(name="EMG_CREATED_AT")
    private Timestamp emgCreatedAt;     // 긴급 상황 발생 시간
    @Column(name="EMG_USER_UUID")
    private String emgUserUUID;         // 긴급상황 대상유저
    @Column(name="EMG_CANCEL")
    private String emgCancel;           // 취소여부
    @Column(name="EMG_CANCLE_AT")
    private Timestamp emgCancelAt;         // 취소시간
    @Column(name="EMG_F_PHONE")
    private String emgFPhone;           // 보호자연락처
    @Column(name="EMG_S_PHONE")
    private String emgSPhone;           // 보호소연락처
    @Column(name="EMG_SESS_ID")
    private String emgSessId;           // 메시지 고유 ID

    public EMG toDto() {
        return EMG.builder()
                .emgLogId(emgLogId)
                .emgDetectedMotion(emgDetectedMotion)
                .emgCapPath(emgCapPath)
                .emgAlertSentTo(emgAlertSentTo)
                .emgCreatedAt(emgCreatedAt)
                .emgUserUUID(emgUserUUID)
                .emgCancel(emgCancel)
                .emgCancelAt(emgCancelAt)
                .emgFPhone(emgFPhone)
                .emgSPhone(emgSPhone)
                .emgSessId(emgSessId)
                .build();
    }
}
