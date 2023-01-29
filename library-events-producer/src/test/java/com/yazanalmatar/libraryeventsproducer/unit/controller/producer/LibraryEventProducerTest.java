package com.yazanalmatar.libraryeventsproducer.unit.controller.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazanalmatar.libraryeventsproducer.domain.Book;
import com.yazanalmatar.libraryeventsproducer.domain.LibraryEvent;
import com.yazanalmatar.libraryeventsproducer.producer.LibraryEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibraryEventProducerTest {
    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    LibraryEventProducer eventProducer;

    @Test
    void sendLibraryEvent_approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Yazan")
                .bookName("Kafka using spring boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(new Book())
                .build();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                throw new RuntimeException("exception calling kafka");
            }
        });

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        assertThrows(Exception.class,()->eventProducer.sendLibraryEvent_Approach2(libraryEvent).get());
    }


    @Test
    void sendLibraryEvent_approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Yazan")
                .bookName("Kafka using spring boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(new Book())
                .build();
       String record =  objectMapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer,String> producerRecord = new ProducerRecord("library-events",libraryEvent.getLibraryEventId(),record);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events",1),1,2342321,System.currentTimeMillis(),1,2);
        SendResult<Integer,String> sendResult = new SendResult<>(producerRecord,recordMetadata);

        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "success";
            }
        });

        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
         eventProducer.sendLibraryEvent_Approach2(libraryEvent).get();
    }


}
