package com.yazanalmatar.libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Validated
@Data
@Builder
public class LibraryEvent {
    private Integer libraryEventId;
    @NotNull
    @Valid
    private Book book;
    private LibraryEventType libraryEventType;
}
