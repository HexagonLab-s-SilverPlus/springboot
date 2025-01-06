package com.hexalab.silverplus.book.jpa.entity;

import com.hexalab.silverplus.book.model.dto.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="BOOK")
@Entity
public class BookEntity {
    @Id
    @Column(name="BOOK_NUM")
    private String bookNum;         // BOOK_NUM	VARCHAR2(100 BYTE)
    @Column(name="BOOK_TITLE")
    private String bookTitle;       // BOOK_TITLE	VARCHAR2(250 BYTE)
    @Column(name="BOOK_CREATED_AT")
    private Timestamp bookCreateAt; // BOOK_CREATED_AT	TIMESTAMP(6)
    @Column(name="BOOK_UPDATED_AT")
    private Timestamp bookUpdateAt; // BOOK_UPDATED_AT	TIMESTAMP(6)
    @Column(name="BOOK_DETAIL")
    private String bookDetail;      // BOOK_DETAIL	VARCHAR2(1000 BYTE)
    @Column(name="BOOK_IMAGE")
    private String bookImage;       // BOOK_IMAGE	VARCHAR2(1000 BYTE)
    @Column(name="BOOK_CREATED_BY")
    private String bookCreatedBy;   // BOOK_CREATED_BY	VARCHAR2(50 BYTE)
    @Column(name="BOOK_UPDATED_BY")
    private String bookUpdatedBy;   // BOOK_UPDATED_BY	VARCHAR2(50 BYTE)

    public Book toDto(){
        return Book.builder()
                .bookNum(bookNum)
                .bookTitle(bookTitle)
                .bookCreateAt(bookCreateAt)
                .bookUpdateAt(bookUpdateAt)
                .bookDetail(bookDetail)
                .bookImage(bookImage)
                .bookCreatedBy(bookCreatedBy)
                .bookUpdatedBy(bookUpdatedBy)
                .build();
    }
}
