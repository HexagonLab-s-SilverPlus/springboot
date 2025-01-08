package com.hexalab.silverplus.member.jpa.repository;

import com.hexalab.silverplus.member.jpa.entity.MemberFilesEntity;
import com.hexalab.silverplus.member.model.dto.MemberFiles;

import java.util.List;

public interface MemberFilesRepositoryCustom {
    List<MemberFilesEntity> findByMemUuid(String memUuid);
    MemberFilesEntity findByProfileMemUuid(String memUuid);
}
