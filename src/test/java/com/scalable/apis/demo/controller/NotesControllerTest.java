package com.scalable.apis.demo.controller;

import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.dto.NotesRequest;
import com.scalable.apis.demo.exception.EmptyDataException;
import com.scalable.apis.demo.exception.UnAuthorizedAccessException;
import com.scalable.apis.demo.exception.UserNotFoundException;
import com.scalable.apis.demo.service.NotesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotesControllerTest {

    @Mock
    private NotesService notesService;

    @InjectMocks
    private NotesController notesController;

    @Test
    void testGetNotes() {
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.getNotes()).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.getNotes();

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).getNotes();
    }

    @Test
    void testGetNote() throws UnAuthorizedAccessException {
        String id = "123";
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.getNote(id)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.getNote(id);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).getNote(id);
    }

    @Test
    void testGetNoteUnAuthorizedAccessException() throws UnAuthorizedAccessException {
        String id = "123";
        when(notesService.getNote(id)).thenThrow(new UnAuthorizedAccessException("Unauthorized access"));

        ResponseEntity<CustomResponse> responseEntity = notesController.getNote(id);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        verify(notesService, times(1)).getNote(id);
    }

    @Test
    void testUpdateNote() throws EmptyDataException, UnAuthorizedAccessException {
        String id = "123";
        NotesRequest notesRequest = new NotesRequest();
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.updateNote(id, notesRequest)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.updateNote(id, notesRequest);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).updateNote(id, notesRequest);
    }

    @Test
    void testUpdateNoteEmptyDataException() throws EmptyDataException, UnAuthorizedAccessException {
        String id = "123";
        NotesRequest notesRequest = new NotesRequest();
        when(notesService.updateNote(id, notesRequest)).thenThrow(new EmptyDataException("Empty data"));

        ResponseEntity<CustomResponse> responseEntity = notesController.updateNote(id, notesRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(notesService, times(1)).updateNote(id, notesRequest);
    }

    @Test
    void testCreateNote() throws EmptyDataException {
        NotesRequest notesRequest = new NotesRequest();
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.createNote(notesRequest)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.createNote(notesRequest);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).createNote(notesRequest);
    }

    @Test
    void testDeleteNote() throws UnAuthorizedAccessException {
        String id = "123";
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.deleteNote(id)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.deleteNote(id);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).deleteNote(id);
    }

    @Test
    void testShareNote() throws UnAuthorizedAccessException, UserNotFoundException {
        String id = "123";
        String username = "testuser";
        CustomResponse expectedResponse = new CustomResponse();
        expectedResponse.setStatus(HttpStatus.OK);
        when(notesService.shareNote(id, username)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.shareNote(id, username);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).shareNote(id, username);
    }

    @Test
    void testShareNoteUnAuthorizedAccessException() throws UnAuthorizedAccessException, UserNotFoundException {
        String id = "123";
        String username = "testuser";
        when(notesService.shareNote(id, username)).thenThrow(new UnAuthorizedAccessException("Unauthorized access"));

        ResponseEntity<CustomResponse> responseEntity = notesController.shareNote(id, username);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        verify(notesService, times(1)).shareNote(id, username);
    }

    @Test
    void testShareNoteUserNotFoundException() throws UnAuthorizedAccessException, UserNotFoundException {
        String id = "123";
        String username = "nonexistentuser";
        when(notesService.shareNote(id, username)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<CustomResponse> responseEntity = notesController.shareNote(id, username);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(notesService, times(1)).shareNote(id, username);
    }

    @Test
    void testSearchNote() {
        String param = "searchParam";
        CustomResponse expectedResponse = new CustomResponse();
        when(notesService.searchNote(param)).thenReturn(expectedResponse);

        ResponseEntity<CustomResponse> responseEntity = notesController.searchNote(param);

        assertEquals(expectedResponse, responseEntity.getBody());
        assertEquals(expectedResponse.getStatus(), responseEntity.getStatusCode());
        verify(notesService, times(1)).searchNote(param);
    }
}
