package com.hexalab.silverplus.member.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberFilesEntity is a Querydsl query type for MemberFilesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberFilesEntity extends EntityPathBase<MemberFilesEntity> {

    private static final long serialVersionUID = 1693620937L;

    public static final QMemberFilesEntity memberFilesEntity = new QMemberFilesEntity("memberFilesEntity");

    public final ComparablePath<java.util.UUID> mfId = createComparable("mfId", java.util.UUID.class);

    public final StringPath mfMemUUID = createString("mfMemUUID");

    public final StringPath mfOriginalName = createString("mfOriginalName");

    public final StringPath mfRename = createString("mfRename");

    public QMemberFilesEntity(String variable) {
        super(MemberFilesEntity.class, forVariable(variable));
    }

    public QMemberFilesEntity(Path<? extends MemberFilesEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberFilesEntity(PathMetadata metadata) {
        super(MemberFilesEntity.class, metadata);
    }

}

