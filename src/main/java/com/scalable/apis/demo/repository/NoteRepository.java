package com.scalable.apis.demo.repository;

import com.scalable.apis.demo.entity.Note;
import com.scalable.apis.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, String> {
    @Query("SELECT n FROM Note n INNER JOIN n.sharedToUsers ni WHERE ni.username = :username")
    List<Note> findBySharedToUser(String username);
}
