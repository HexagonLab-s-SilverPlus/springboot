package com.hexalab.silverplus.book.jpa.repository;


import com.hexalab.silverplus.book.jpa.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String>,BookRepositoryCustom {
}
