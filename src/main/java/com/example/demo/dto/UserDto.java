package com.example.demo.dto;

import com.example.demo.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String picture;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        // this.picture = user.getPicture(); // Uncomment if picture exists in User entity
    }
}
