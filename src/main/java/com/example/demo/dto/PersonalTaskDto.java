package com.example.demo.dto; // Create a new 'dto' package

import lombok.Data;

@Data // A shortcut for @Getter, @Setter, @ToString, @EqualsAndHashCode
public class PersonalTaskDto {
    private Long id;
    private String text;
    private boolean completed;
    // Notice there is no 'User' object here, to prevent the loop.
    // You could include a userId or username if needed.
    // private Long userId;
}