package com.scalable.apis.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes", indexes = @Index(columnList = "title, description"))
public class Note implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String description;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "notes_shared_to_users",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> sharedToUsers;
}
