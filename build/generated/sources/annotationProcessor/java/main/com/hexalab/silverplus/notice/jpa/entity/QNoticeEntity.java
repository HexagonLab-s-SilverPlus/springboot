package com.hexalab.silverplus.notice.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNoticeEntity is a Querydsl query type for NoticeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeEntity extends EntityPathBase<NoticeEntity> {

    private static final long serialVersionUID = 85555440L;

    public static final QNoticeEntity noticeEntity = new QNoticeEntity("noticeEntity");

    public final StringPath notContent = createString("notContent");

    public final DateTimePath<java.sql.Timestamp> notCreateAt = createDateTime("notCreateAt", java.sql.Timestamp.class);

    public final StringPath notCreateBy = createString("notCreateBy");

    public final DateTimePath<java.sql.Timestamp> notDeleteAt = createDateTime("notDeleteAt", java.sql.Timestamp.class);

    public final StringPath notDeleteBy = createString("notDeleteBy");

    public final ComparablePath<java.util.UUID> notId = createComparable("notId", java.util.UUID.class);

    public final StringPath notReadCount = createString("notReadCount");

    public final StringPath notTitle = createString("notTitle");

    public final DateTimePath<java.sql.Timestamp> notUpdateAt = createDateTime("notUpdateAt", java.sql.Timestamp.class);

    public final StringPath notUpdateBy = createString("notUpdateBy");

    public QNoticeEntity(String variable) {
        super(NoticeEntity.class, forVariable(variable));
    }

    public QNoticeEntity(Path<? extends NoticeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNoticeEntity(PathMetadata metadata) {
        super(NoticeEntity.class, metadata);
    }

}

