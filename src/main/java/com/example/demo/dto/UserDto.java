package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String picture;
    // We can include a list of Task DTOs, not the full Task Entities
    private List<PersonalTaskDto> personalTasks;
}