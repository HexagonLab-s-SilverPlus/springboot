package com.hexalab.silverplus.member.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = -445161228L;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final StringPath memAddress = createString("memAddress");

    public final StringPath memCellphone = createString("memCellphone");

    public final DatePath<java.sql.Date> memChangeStatus = createDate("memChangeStatus", java.sql.Date.class);

    public final StringPath memEmail = createString("memEmail");

    public final DatePath<java.sql.Date> memEnrollDate = createDate("memEnrollDate", java.sql.Date.class);

    public final StringPath memFamilyApproval = createString("memFamilyApproval");

    public final StringPath memGoogleEmail = createString("memGoogleEmail");

    public final StringPath memGovCode = createString("memGovCode");

    public final StringPath memId = createString("memId");

    public final StringPath memKakaoEmail = createString("memKakaoEmail");

    public final StringPath memName = createString("memName");

    public final StringPath memNaverEmail = createString("memNaverEmail");

    public final StringPath memPhone = createString("memPhone");

    public final StringPath memPw = createString("memPw");

    public final StringPath memRnn = createString("memRnn");

    public final StringPath memSocialGoogle = createString("memSocialGoogle");

    public final StringPath memSocialKakao = createString("memSocialKakao");

    public final StringPath memSocialNaver = createString("memSocialNaver");

    public final StringPath memStatus = createString("memStatus");

    public final StringPath memType = createString("memType");

    public final StringPath memUUID = createString("memUUID");

    public final StringPath memUUIDFam = createString("memUUIDFam");

    public final StringPath memUUIDMgr = createString("memUUIDMgr");

    public QMemberEntity(String variable) {
        super(MemberEntity.class, forVariable(variable));
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberEntity(PathMetadata metadata) {
        super(MemberEntity.class, metadata);
    }

}

