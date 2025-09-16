package com.example.demo.controller;

import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupTaskDto;
import com.example.demo.dto.NoteDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.Group;
import com.example.demo.model.GroupTask;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupDto> getAllGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return groupService.getAllGroupsForUser(userDetails.getUsername()).stream()
                .map(GroupDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public GroupDto createGroup(@RequestBody CreateGroupRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return new GroupDto(groupService.createGroup(request.getName(), userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public GroupDto getGroupById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return new GroupDto(groupService.getGroupById(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/join")
    public GroupDto joinGroup(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return new GroupDto(groupService.joinGroup(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        groupService.leaveGroup(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/tasks")
    public List<GroupTaskDto> getGroupTasks(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return groupService.getGroupTasks(id, userDetails.getUsername()).stream()
                .map(GroupTaskDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/tasks")
    public GroupTaskDto createGroupTask(@PathVariable Long id, @RequestBody CreateTaskRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return new GroupTaskDto(groupService.createGroupTask(id, request.getText(), userDetails.getUsername()));
    }

    @PutMapping("/{id}/tasks/{taskId}")
    public GroupTaskDto updateGroupTask(@PathVariable Long id, @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return new GroupTaskDto(groupService.updateGroupTask(id, taskId, request.getText(), request.isCompleted(), userDetails.getUsername()));
    }

    @DeleteMapping("/{id}/tasks/{taskId}")
    public ResponseEntity<?> deleteGroupTask(@PathVariable Long id, @PathVariable Long taskId, @AuthenticationPrincipal UserDetails userDetails) {
        groupService.deleteGroupTask(id, taskId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public Set<UserDto> getGroupMembers(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return groupService.getGroupMembers(id, userDetails.getUsername()).stream()
                .map(UserDto::new)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}/notes")
    public List<NoteDto> getGroupNotes(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return groupService.getGroupNotes(id, userDetails.getUsername()).stream()
                .map(NoteDto::new)
                .collect(Collectors.toList());
    }

    // Request DTOs
    public static class CreateGroupRequest {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CreateTaskRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class UpdateTaskRequest {
        private String text;
        private boolean completed;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}