package com.scalable.apis.demo.controller;

import com.scalable.apis.demo.exception.EmptyDataException;
import com.scalable.apis.demo.dto.NotesRequest;
import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.exception.NoteNotFoundException;
import com.scalable.apis.demo.exception.UnAuthorizedAccessException;
import com.scalable.apis.demo.exception.UserNotFoundException;
import com.scalable.apis.demo.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @GetMapping
    public ResponseEntity<CustomResponse> getNotes() {
        CustomResponse customResponse = notesService.getNotes();
        return new ResponseEntity<>(customResponse, customResponse.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> getNote(@PathVariable("id") String id) {
        try {
            CustomResponse customResponse = notesService.getNote(id);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (UnAuthorizedAccessException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponse> updateNote(@PathVariable("id") String id, @RequestBody NotesRequest notesRequest) {
        try {
            CustomResponse customResponse = notesService.updateNote(id, notesRequest);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (EmptyDataException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnAuthorizedAccessException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<CustomResponse> createNote(@RequestBody NotesRequest notesRequest) {
        CustomResponse customResponse = notesService.createNote(notesRequest);
        return new ResponseEntity<>(customResponse, customResponse.getStatus());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteNote(@PathVariable("id") String id) {
        try {
            CustomResponse customResponse = notesService.deleteNote(id);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (UnAuthorizedAccessException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}/share/{username}")
    public ResponseEntity<CustomResponse> shareNote(@PathVariable("id") String id, @PathVariable("username") String username) {
        try {
            CustomResponse customResponse = notesService.shareNote(id, username);
            return new ResponseEntity<>(customResponse, customResponse.getStatus());
        } catch (UnAuthorizedAccessException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException e) {
            return returnErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<CustomResponse> searchNote(@RequestParam(name = "param") String param) {
        CustomResponse customResponse = notesService.searchNote(param);
        return new ResponseEntity<>(customResponse, customResponse.getStatus());
    }

    private ResponseEntity<CustomResponse> returnErrorResponse(String message, HttpStatus httpStatus) {
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(message);
        customResponse.setStatus(httpStatus);
        return new ResponseEntity<>(customResponse, httpStatus);
    }
}
