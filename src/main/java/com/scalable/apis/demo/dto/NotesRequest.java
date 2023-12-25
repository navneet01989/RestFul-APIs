package com.scalable.apis.demo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NotesRequest {

    private String title;
    private String description;
}
