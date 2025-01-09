package com.hexalab.silverplus.faq.jpa.repository;

import com.hexalab.silverplus.faq.jpa.entity.FAQEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQEntity, String> {
}
