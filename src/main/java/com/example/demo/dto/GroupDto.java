package com.example.demo.dto;

import com.example.demo.model.Group;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class GroupDto {
    private Long id;
    private String name;
    private String subject;
    private UserDto owner;
    private List<UserDto> members;
    private List<GroupTaskDto> tasks;
    private List<NoteDto> notes;

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.subject = ""; // Set appropriate value if you have a subject field

        if (group.getOwner() != null) {
            this.owner = new UserDto(group.getOwner());
        }

        if (group.getMembers() != null) {
            this.members = group.getMembers().stream()
                    .map(UserDto::new)
                    .collect(Collectors.toList());
        }

        if (group.getTasks() != null) {
            this.tasks = group.getTasks().stream()
                    .map(GroupTaskDto::new)
                    .collect(Collectors.toList());
        }

        if (group.getNotes() != null) {
            this.notes = group.getNotes().stream()
                    .map(NoteDto::new)
                    .collect(Collectors.toList());
        }
    }
}