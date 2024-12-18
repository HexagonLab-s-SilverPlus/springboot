package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberFilesRepository extends JpaRepository<MemberFilesEntity, UUID> {
}
