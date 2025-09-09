package com.example.demo.dto; // Create a new 'dto' package

import com.example.demo.model.User;

import lombok.Data;

@Data // A shortcut for @Getter, @Setter, @ToString, @EqualsAndHashCode
public class PersonalTaskDto {
    private User owner;
    private String text;
    private boolean completed;
}