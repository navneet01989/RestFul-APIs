package com.scalable.apis.demo.service;

import com.scalable.apis.demo.dto.NotesDto;
import com.scalable.apis.demo.dto.NotesRequest;
import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.entity.Note;
import com.scalable.apis.demo.entity.User;
import com.scalable.apis.demo.exception.EmptyDataException;
import com.scalable.apis.demo.exception.UnAuthorizedAccessException;
import com.scalable.apis.demo.exception.UserNotFoundException;
import com.scalable.apis.demo.repository.NoteRepository;
import com.scalable.apis.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotesServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotesService notesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateNote() {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet())));

        NotesRequest notesRequest = new NotesRequest();
        notesRequest.setTitle("Test Title");
        notesRequest.setDescription("Test Description");

        CustomResponse response = notesService.createNote(notesRequest);

        assertEquals("Note created successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(noteRepository, times(1)).save(any());
    }

    @Test
    void testGetNotes() {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet())));

        CustomResponse response = notesService.getNotes();

        assertEquals("data retrieved successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetNote() throws UnAuthorizedAccessException {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user);
        Set<Note> notes = new HashSet<>(user.getUserNotes());
        notes.add(note);
        user.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        CustomResponse response = notesService.getNote("1");

        assertEquals("data retrieved successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateNote() throws EmptyDataException, UnAuthorizedAccessException {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user);
        Set<Note> notes = new HashSet<>(user.getUserNotes());
        notes.add(note);
        user.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        NotesRequest notesRequest = new NotesRequest();
        notesRequest.setTitle("Updated Title");
        notesRequest.setDescription("Updated Description");

        CustomResponse response = notesService.updateNote("1", notesRequest);

        assertEquals("Note updated successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Updated Title", note.getTitle());
        assertEquals("Updated Description", note.getDescription());
        verify(noteRepository, times(1)).save(any());
    }

    @Test
    void testUpdateNoteEmptyDataException() {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user);
        Set<Note> notes = new HashSet<>(user.getUserNotes());
        notes.add(note);
        user.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        NotesRequest notesRequest = new NotesRequest();

        assertThrows(EmptyDataException.class, () -> notesService.updateNote("1", notesRequest));
        verify(noteRepository, never()).save(any());
    }

    @Test
    void testDeleteNote() throws UnAuthorizedAccessException {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user);
        Set<Note> notes = new HashSet<>(user.getUserNotes());
        notes.add(note);
        user.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        CustomResponse response = notesService.deleteNote("1");

        assertEquals("Note deleted successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(userRepository, times(1)).save(any());
        verify(noteRepository, times(1)).delete(any());
    }

    @Test
    void testShareNote() throws UnAuthorizedAccessException, UserNotFoundException {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user1 = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        User user2 = new User(2L, "user2", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user1);
        Set<Note> notes = new HashSet<>(user1.getUserNotes());
        notes.add(note);
        user1.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(user2));

        CustomResponse response = notesService.shareNote("1", "user2");

        assertEquals("Note shared successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, user2.getShareList().size());
        assertNull(note.getSharedToUsers());
        verify(userRepository, times(1)).save(any());
        verify(noteRepository, times(1)).save(any());
    }

    @Test
    void testSearchNote() {
        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auth.getName()).thenReturn("user1");
        User user = new User(1L, "user1", "password", Collections.emptySet(), Collections.emptySet());
        Note note = new Note();
        note.setId("1");
        note.setTitle("Test Title");
        note.setDescription("Test Description");
        note.setOwner(user);
        Set<Note> notes = new HashSet<>(user.getUserNotes());
        notes.add(note);
        user.setUserNotes(notes);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        CustomResponse response = notesService.searchNote("Test");

        assertEquals("Matching notes found successfully", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getData() instanceof Set<?>);
        assertEquals(1, ((Set<?>) response.getData()).size());
    }
}