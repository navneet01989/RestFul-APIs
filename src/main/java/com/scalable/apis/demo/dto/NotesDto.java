package com.scalable.apis.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class NotesDto {
    private String id;
    private String title;
    private String description;
    private Set<UserDto> shareList;
}
