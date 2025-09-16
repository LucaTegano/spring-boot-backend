package com.example.demo.dto;

import com.example.demo.model.GroupTask;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupTaskDto {
    private Long id;
    private String text;
    private boolean completed;
    private Long groupId;

    public GroupTaskDto(GroupTask task) {
        this.id = task.getId();
        this.text = task.getText();
        this.completed = task.isCompleted();
        if (task.getGroup() != null) {
            this.groupId = task.getGroup().getId();
        }
    }
}