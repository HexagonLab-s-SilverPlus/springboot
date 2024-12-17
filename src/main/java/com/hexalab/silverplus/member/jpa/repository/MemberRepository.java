package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String>, MemberRepositoryCustom {
}
