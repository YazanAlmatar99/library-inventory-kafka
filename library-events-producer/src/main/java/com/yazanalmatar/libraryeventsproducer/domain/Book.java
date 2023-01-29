package com.yazanalmatar.libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Validated
@Builder
public class Book {
    @NotNull(message="book id cannot be null")
    private Integer bookId;
    @NotNull(message="book name cannot be null")
    private String bookName;
    @NotNull(message="book author cannot be null")
    private String bookAuthor;
}
