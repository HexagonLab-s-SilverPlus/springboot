package com.hexalab.silverplus.security.jwt.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// entity 겸 dto 역할 두가지를 수행
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name="REFRESH_TOKEN")
public class RefreshToken {

    @Id
    @Column(length = 36)
    private String tokenUuid;    // import java.util.UUID

//    @ManyToOne(fetch = FetchType.LAZY)
//    @Column(name="USERID", referencedColumnName="USERID", nullable = false)
//    private String userId;

    @Column(name = "TOKEN_MEM_UUID", nullable = false)
    private String tokenMemUuid;
    @Column(name = "TOKEN_VALUE", nullable = false)
    private String tokenValue;
    @Column(name = "TOKEN_CREATED_AT", nullable = false)
    private LocalDateTime tokenCreatedAt;
    @Column(name = "TOKEN_EXP_IN", nullable = false)
    private Long tokenExpIn;
    @Column(name = "TOKEN_EXP_DATE", nullable = false)
    private LocalDateTime tokenExpDate;
    @Column(name = "TOKEN_STATUS", length = 50)
    private String tokenStatus;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (tokenCreatedAt == null) tokenCreatedAt = now;
        if (tokenExpDate == null) tokenExpDate = now.plusSeconds(tokenExpIn / 1000);
        // 예를 들어 expiresIn 이 밀리초 단위라면, 날짜로 변환함
    }
}
