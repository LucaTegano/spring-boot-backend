package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.PersonalTask;
import com.example.demo.service.PersonalTaskService;

import java.util.List;

@RestController
@RequestMapping("/api/personal-tasks")
public class PersonalTaskController {

    @Autowired
    private PersonalTaskService personalTaskService;

    @GetMapping
    public List<PersonalTask> getTasks(@AuthenticationPrincipal UserDetails userDetails) {
        return personalTaskService.getTasksForUser(userDetails.getUsername());
    }

    @PostMapping
    public PersonalTask createTask(@RequestBody PersonalTask task, @AuthenticationPrincipal UserDetails userDetails) {
        return personalTaskService.createTask(task, userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public PersonalTask updateTask(@PathVariable Long id, @RequestBody PersonalTask taskDetails, @AuthenticationPrincipal UserDetails userDetails) {
        return personalTaskService.updateTask(id, taskDetails, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        personalTaskService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}