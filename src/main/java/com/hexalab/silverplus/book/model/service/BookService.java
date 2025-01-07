package com.hexalab.silverplus.book.model.service;

import com.hexalab.silverplus.book.jpa.entity.BookEntity;
import com.hexalab.silverplus.book.jpa.repository.BookRepository;
import com.hexalab.silverplus.book.model.dto.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    // 책 등록
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

    // 책정보 전체 갯수
    public int selectAllBookListCount() {
        return (int)bookRepository.count();
    }

    // 책정보 전체 조회
    public ArrayList<Book> selectAllBookList(Pageable pageable) {
        Page<BookEntity> bookEntities = bookRepository.findAll(pageable);
        ArrayList<Book> books = new ArrayList<>();
        for (BookEntity bookEntity : bookEntities) {
            books.add(bookEntity.toDto());
        }
        return books;
    }

    public int selectSearchBookListCount(String keyword) {
        return (int)bookRepository.selectSearchBookListCount(keyword);
    }

    public ArrayList<Book> selectSearchBookList(String keyword, Pageable pageable) {
        ArrayList<Book> books = new ArrayList<Book>();
        List<BookEntity> bookEntities = bookRepository.selectSearchBookList(keyword,pageable);
        for (BookEntity bookEntity : bookEntities) {
            books.add(bookEntity.toDto());
        }
        return books;
    }

    public Book selectBook(String bookNum) {
        return bookRepository.findById(bookNum).get().toDto();
    }
}
