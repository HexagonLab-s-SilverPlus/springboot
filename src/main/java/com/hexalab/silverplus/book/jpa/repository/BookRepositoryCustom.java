package com.hexalab.silverplus.book.jpa.repository;

import com.hexalab.silverplus.book.jpa.entity.BookEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookRepositoryCustom {
    long selectSearchBookListCount(String keyword);
    List<BookEntity> selectSearchBookList(String keyword, Pageable pageable);
}
