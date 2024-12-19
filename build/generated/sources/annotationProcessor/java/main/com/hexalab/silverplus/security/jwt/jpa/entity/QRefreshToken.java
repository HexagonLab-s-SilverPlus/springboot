package com.hexalab.silverplus.security.jwt.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefreshToken is a Querydsl query type for RefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefreshToken extends EntityPathBase<RefreshToken> {

    private static final long serialVersionUID = -759558924L;

    public static final QRefreshToken refreshToken = new QRefreshToken("refreshToken");

    public final DateTimePath<java.time.LocalDateTime> tokenCreatedAt = createDateTime("tokenCreatedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> tokenExpDate = createDateTime("tokenExpDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> tokenExpIn = createNumber("tokenExpIn", Long.class);

    public final StringPath tokenMemUuid = createString("tokenMemUuid");

    public final StringPath tokenStatus = createString("tokenStatus");

    public final StringPath tokenUuid = createString("tokenUuid");

    public final StringPath tokenValue = createString("tokenValue");

    public QRefreshToken(String variable) {
        super(RefreshToken.class, forVariable(variable));
    }

    public QRefreshToken(Path<? extends RefreshToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefreshToken(PathMetadata metadata) {
        super(RefreshToken.class, metadata);
    }

}

