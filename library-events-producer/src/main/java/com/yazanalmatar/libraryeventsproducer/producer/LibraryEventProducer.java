package com.yazanalmatar.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yazanalmatar.libraryeventsproducer.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {
    @Autowired
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    String topic = "library-events";

    //async method
    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        CompletableFuture<SendResult<Integer, String>> completableFuture =  kafkaTemplate.sendDefault(key,value);
        completableFuture.thenApply(result->{handleSuccess(key, value,result);return result;});
        completableFuture.exceptionally(throwable -> {handleFailure(throwable);
            return null;
        });

    }

    //sync method
    public SendResult<Integer,String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws ExecutionException, InterruptedException, JsonProcessingException, TimeoutException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer,String> sendResult = null;
        try {
            sendResult=  kafkaTemplate.sendDefault(key,value).get(1, TimeUnit.SECONDS); // wait for 1 second then timeout if no response
        } catch (InterruptedException | ExecutionException e) {
            log.error("ExecutionException/InterruptedException: Error sending the message and the exception is {} ",e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            log.error("Execution: Error sending the message and the exception is {} ",e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return sendResult;
    }

    //async method
    public CompletableFuture<SendResult<Integer, String>>  sendLibraryEvent_Approach2(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer,String> producerRecord =  buildProducerRecord(key,value,topic);
        CompletableFuture<SendResult<Integer, String>> completableFuture =  kafkaTemplate.send(producerRecord);
        completableFuture.thenApply(result->{handleSuccess(key, value,result);return result;});
        completableFuture.exceptionally(throwable -> {handleFailure(throwable);
            return null;
        });
        return completableFuture;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source","scanner".getBytes()));
        return new ProducerRecord(topic,null,key,value,recordHeaders);
    }

    private void handleFailure(Throwable ex) {
        log.error("Error sending the message and the exception is {} ",ex.getMessage());
    }

    private void handleSuccess(Integer key,String value,SendResult result) {
        log.info("Message Sent for the key: {} and the value is : {}, partition is : {}",key,value,result.getRecordMetadata().partition());
    }
}
