package com.hexalab.silverplus.emg.model.dto;

import com.hexalab.silverplus.emg.jpa.entity.EMGEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data    //@Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EMG {
    private String emgLogId;            // 긴급 상황 고유 ID
    private String emgDetectedMotion;   // 감지된 모션
    private String emgCapPath;          // 캡쳐된 이미지 경로
    private String emgAlertSentTo;      // 알림 발송 대상
    private Timestamp emgCreatedAt;     // 긴급 상황 발생 시간
    private String emgUserUUID;         // 긴급상황 대상유저
    private String emgCancel;           // 취소여부
    private Timestamp emgCancelAt;         // 취소시간
    private String emgFPhone;           // 보호자연락처
    private String emgSPhone;           // 보호소연락처
    private String emgSessId;           // 메시지 고유 ID

    public EMGEntity toEntity(){
        return EMGEntity.builder()
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
