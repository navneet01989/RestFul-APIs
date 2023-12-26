package com.scalable.apis.demo.service;

import com.scalable.apis.demo.dto.UserDto;
import com.scalable.apis.demo.exception.EmptyDataException;
import com.scalable.apis.demo.dto.NotesDto;
import com.scalable.apis.demo.dto.NotesRequest;
import com.scalable.apis.demo.dto.CustomResponse;
import com.scalable.apis.demo.entity.Note;
import com.scalable.apis.demo.entity.User;
import com.scalable.apis.demo.exception.UnAuthorizedAccessException;
import com.scalable.apis.demo.exception.UserNotFoundException;
import com.scalable.apis.demo.repository.NoteRepository;
import com.scalable.apis.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@Service
public class NotesService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public CustomResponse createNote(NotesRequest notesRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Note note = new Note();
        note.setTitle(notesRequest.getTitle());
        note.setDescription(notesRequest.getDescription());
        note.setOwner(user);
        noteRepository.save(note);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("Note created successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    public CustomResponse getNotes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        List<NotesDto> notesDtos = new ArrayList<>();
        for (Note note : user.getUserNotes()) {
            Set<UserDto> userDtos = new HashSet<>();
            for (User userForDto : note.getSharedToUsers()) {
                userDtos.add(new UserDto(userForDto.getUsername()));
            }
            notesDtos.add(new NotesDto(note.getId(), note.getTitle(), note.getDescription(), userDtos));
        }
        CustomResponse customResponse = new CustomResponse();
        customResponse.setData(notesDtos);
        customResponse.setMessage("data retrieved successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    public CustomResponse getNote(String id) throws UnAuthorizedAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Note note = user
                .getUserNotes()
                .stream()
                .filter(noteIterator -> noteIterator.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UnAuthorizedAccessException("Unauthorized access to note or note not found"));
        Set<UserDto> userDtos = new HashSet<>();
        if (note.getSharedToUsers() != null && !note.getSharedToUsers().isEmpty()) {
            for (User userForDto : note.getSharedToUsers()) {
                userDtos.add(new UserDto(userForDto.getUsername()));
            }
        }
        NotesDto notesDto = new NotesDto(note.getId(), note.getTitle(), note.getDescription(), userDtos);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setData(notesDto);
        customResponse.setMessage("data retrieved successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    public CustomResponse updateNote(String id, NotesRequest notesRequest) throws EmptyDataException, UnAuthorizedAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Note note = user
                .getUserNotes()
                .stream()
                .filter(noteIterator -> noteIterator.getId().equals(id)).findFirst().orElseThrow(() -> new UnAuthorizedAccessException("Unauthorized access to note or note not found"));
        boolean didAnyFieldUpdate = false;
        if (notesRequest.getTitle() != null && !notesRequest.getTitle().isEmpty()) {
            note.setTitle(notesRequest.getTitle());
            didAnyFieldUpdate = true;
        }
        if (notesRequest.getDescription() != null && !notesRequest.getDescription().isEmpty()) {
            note.setDescription(notesRequest.getDescription());
            didAnyFieldUpdate = true;
        }
        if (!didAnyFieldUpdate) {
            throw new EmptyDataException("No data provided to update");
        }
        noteRepository.save(note);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("Note updated successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    @Transactional
    public CustomResponse deleteNote(String id) throws UnAuthorizedAccessException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Note note = user
                .getUserNotes()
                .stream()
                .filter(noteIterator -> noteIterator.getId().equals(id)).findFirst().orElseThrow(() -> new UnAuthorizedAccessException("Unauthorized access to note or note not found"));
        user.getUserNotes().remove(note);
        userRepository.save(user);
        noteRepository.delete(note);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("Note deleted successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    @Transactional
    public CustomResponse shareNote(String id, String username) throws UnAuthorizedAccessException, UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Note noteToBeShared = user
                .getUserNotes()
                .stream()
                .filter(noteIterator -> noteIterator.getId().equals(id)).findFirst().orElseThrow(() -> new UnAuthorizedAccessException("Unauthorized access to noteToBeShared or noteToBeShared not found"));
        User shareNoteToUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        Set<Note> noteShareList = new HashSet<>(shareNoteToUser.getShareList());
        noteShareList.add(noteToBeShared);
        shareNoteToUser.setShareList(noteShareList);
        userRepository.save(shareNoteToUser);
        if (noteToBeShared.getSharedToUsers() != null && !noteToBeShared.getSharedToUsers().isEmpty()) {
            Set<User> shareToUser = new HashSet<>(noteToBeShared.getSharedToUsers());
            shareToUser.add(shareNoteToUser);
            noteToBeShared.setSharedToUsers(shareToUser);
        } else {
            Set<User> shareToUser = new HashSet<>();
            shareToUser.add(shareNoteToUser);
            noteToBeShared.setSharedToUsers(shareToUser);
        }
        noteRepository.save(noteToBeShared);
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("Note shared successfully");
        customResponse.setStatus(OK);
        return customResponse;
    }

    public CustomResponse searchNote(String param) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).get();
        Set<Note> note = user
                .getUserNotes()
                .stream()
                .filter(noteIterator -> noteIterator.getTitle().contains(param) || noteIterator.getDescription().contains(param)).collect(Collectors.toSet());
        CustomResponse customResponse = new CustomResponse();
        if (note.isEmpty()) {
            customResponse.setMessage("No matching results");
        } else {
            customResponse.setMessage("Matching notes found successfully");
        }
        customResponse.setStatus(OK);
        customResponse.setData(note);
        return customResponse;
    }
}

