package com.yazanalmatar.libraryeventsproducer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazanalmatar.libraryeventsproducer.controller.LibraryEventsController;
import com.yazanalmatar.libraryeventsproducer.domain.Book;
import com.yazanalmatar.libraryeventsproducer.domain.LibraryEvent;
import com.yazanalmatar.libraryeventsproducer.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    LibraryEventProducer libraryEventProducer;
    @Test
    void postLibraryEvent() throws Exception {
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Yazan")
                .bookName("Kafka using spring boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(new Book())
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventProducer.sendLibraryEvent_Approach2(isA(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(post("/v1/library-event")
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }
    //@TODO:
//    @Test
//    void postLibraryEvent_4xx() throws Exception {
//        Book book = Book.builder()
//                .bookId(123)
//                .bookAuthor("Yazan")
//                .bookName("Kafka using spring boot")
//                .build();
//
//        LibraryEvent libraryEvent = LibraryEvent.builder()
//                .libraryEventId(null)
//                .book(null)
//                .build();
//        String json = objectMapper.writeValueAsString(libraryEvent);
//        doNothing().when(libraryEventProducer).sendLibraryEvent_Approach2(isA(LibraryEvent.class));
//
//        mockMvc.perform(post("/v1/library-event")
//                        .content(json).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is4xxClientError());
//    }
}
