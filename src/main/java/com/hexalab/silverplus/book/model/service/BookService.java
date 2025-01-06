package com.hexalab.silverplus.book.model.service;

import com.hexalab.silverplus.book.jpa.repository.BookRepository;
import com.hexalab.silverplus.book.model.dto.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    // 북 등록
    public int bookInsert(Book book) {
        try {
            bookRepository.save(book.toEntity());
            return 1;
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return 0;
        }
    }
}
