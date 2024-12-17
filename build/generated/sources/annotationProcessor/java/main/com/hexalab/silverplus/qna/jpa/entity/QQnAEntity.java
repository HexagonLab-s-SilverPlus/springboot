package com.hexalab.silverplus.qna.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQnAEntity is a Querydsl query type for QnAEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQnAEntity extends EntityPathBase<QnAEntity> {

    private static final long serialVersionUID = 543019916L;

    public static final QQnAEntity qnAEntity = new QQnAEntity("qnAEntity");

    public final StringPath qnaADContent = createString("qnaADContent");

    public final DateTimePath<java.sql.Timestamp> qnaADCreateAt = createDateTime("qnaADCreateAt", java.sql.Timestamp.class);

    public final StringPath qnaADCreateBy = createString("qnaADCreateBy");

    public final DateTimePath<java.sql.Timestamp> qnaADUpdateAt = createDateTime("qnaADUpdateAt", java.sql.Timestamp.class);

    public final StringPath qnaADUpdateBy = createString("qnaADUpdateBy");

    public final ComparablePath<java.util.UUID> qnaId = createComparable("qnaId", java.util.UUID.class);

    public final StringPath qnaTitle = createString("qnaTitle");

    public final StringPath qnaWContent = createString("qnaWContent");

    public final DateTimePath<java.sql.Timestamp> qnaWCreateAt = createDateTime("qnaWCreateAt", java.sql.Timestamp.class);

    public final StringPath qnaWCreateBy = createString("qnaWCreateBy");

    public final DateTimePath<java.sql.Timestamp> qnaWUpdateAt = createDateTime("qnaWUpdateAt", java.sql.Timestamp.class);

    public QQnAEntity(String variable) {
        super(QnAEntity.class, forVariable(variable));
    }

    public QQnAEntity(Path<? extends QnAEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQnAEntity(PathMetadata metadata) {
        super(QnAEntity.class, metadata);
    }

}

