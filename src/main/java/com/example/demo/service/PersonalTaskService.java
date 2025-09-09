package com.example.demo.service;

import com.example.demo.model.PersonalTask;
import com.example.demo.model.User;
import com.example.demo.repository.PersonalTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalTaskService {

    @Autowired
    private PersonalTaskRepository personalTaskRepository;

    @Autowired
    private UserService userService;

    public List<PersonalTask> getAllTasksForUser(String username) {
        User owner = userService.getUserByUsername(username);
        return personalTaskRepository.findByOwner_Id(owner.getId());
    }

    public PersonalTask createTask(PersonalTask taskRequest, String username) {
        User owner = userService.getUserByUsername(username);
        taskRequest.setOwner(owner);
        return personalTaskRepository.save(taskRequest);
    }

    public PersonalTask updateTask(Long taskId, PersonalTask taskRequest, String username) {
        PersonalTask task = getTaskAndVerifyOwner(taskId, username);
        task.setText(taskRequest.getText());
        task.setCompleted(taskRequest.isCompleted());
        return personalTaskRepository.save(task);
    }

    public void deleteTask(Long taskId, String username) {
        PersonalTask taskToDelete = getTaskAndVerifyOwner(taskId, username);
        personalTaskRepository.delete(taskToDelete);
    }

    private PersonalTask getTaskAndVerifyOwner(Long taskId, String username) {
        User owner = userService.getUserByUsername(username);
        PersonalTask task = personalTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        if (!task.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("User not authorized for this task");
        }

        return task;
    }
}