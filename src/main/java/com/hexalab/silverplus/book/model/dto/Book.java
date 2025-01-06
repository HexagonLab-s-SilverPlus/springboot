package com.hexalab.silverplus.book.model.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.hexalab.silverplus.book.jpa.entity.BookEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    private String bookNum;         // BOOK_NUM	VARCHAR2(100 BYTE)
    private String bookTitle;       // BOOK_TITLE	VARCHAR2(250 BYTE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp bookCreateAt; // BOOK_CREATED_AT	TIMESTAMP(6)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Timestamp bookUpdateAt; // BOOK_UPDATED_AT	TIMESTAMP(6)
    private String bookDetail;      // BOOK_DETAIL	VARCHAR2(1000 BYTE)
    private String bookImage;       // BOOK_IMAGE	VARCHAR2(1000 BYTE)
    private String bookCreatedBy;   // BOOK_CREATED_BY	VARCHAR2(50 BYTE)
    private String bookUpdatedBy;   // BOOK_UPDATED_BY	VARCHAR2(50 BYTE)

    public BookEntity toEntity() {
        return BookEntity.builder().
                bookNum(bookNum).
                bookTitle(bookTitle).
                bookCreateAt(bookCreateAt).
                bookUpdateAt(bookUpdateAt).
                bookDetail(bookDetail).
                bookImage(bookImage).
                bookCreatedBy(bookCreatedBy).
                bookUpdatedBy(bookUpdatedBy).
                build();
    }
}
