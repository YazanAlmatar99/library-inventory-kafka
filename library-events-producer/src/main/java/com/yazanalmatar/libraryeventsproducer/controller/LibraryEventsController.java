package com.yazanalmatar.libraryeventsproducer.controller;
import com.yazanalmatar.libraryeventsproducer.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yazanalmatar.libraryeventsproducer.domain.LibraryEventType;
import com.yazanalmatar.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {
    @Autowired
    LibraryEventProducer libraryEventProducer;
    @PostMapping("/v1/library-event")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@Valid @RequestBody  LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/v1/library-event")
    public ResponseEntity<?> putLibraryEvent(@Valid @RequestBody  LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        if(libraryEvent.getLibraryEventId()==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass libraryEventId, it cannot be null.");
        }
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEvent_Approach2(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }
}
