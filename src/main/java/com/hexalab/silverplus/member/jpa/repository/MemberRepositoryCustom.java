package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import com.hexalab.silverplus.member.model.dto.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

    long selectCheckId(String memId);
    Member findByMemId(String memId);
}
