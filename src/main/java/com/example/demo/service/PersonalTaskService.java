package com.example.demo.service;

import com.example.demo.model.PersonalTask;
import com.example.demo.model.User;
import com.example.demo.repository.PersonalTaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalTaskService {

    @Autowired
    private PersonalTaskRepository personalTaskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PersonalTask> getTasksForUser(String username) {
        // FIX: Look up user by username, which is provided by the authenticated principal.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return personalTaskRepository.findByOwner_Id(user.getId());
    }

    public PersonalTask createTask(PersonalTask task, String username) {
        // FIX: Look up user by username.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        task.setOwner(user);
        return personalTaskRepository.save(task);
    }

    public PersonalTask updateTask(Long taskId, PersonalTask taskDetails, String username) {
        // FIX: Look up user by username.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        PersonalTask task = personalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to update this task");
        }

        task.setText(taskDetails.getText());
        task.setCompleted(taskDetails.isCompleted());

        return personalTaskRepository.save(task);
    }

    public void deleteTask(Long taskId, String username) {
        // FIX: Look up user by username.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        PersonalTask task = personalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("User not authorized to delete this task");
        }

        personalTaskRepository.delete(task);
    }
}