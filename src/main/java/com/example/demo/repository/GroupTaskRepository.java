package com.example.demo.repository;

import com.example.demo.model.GroupTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupTaskRepository extends JpaRepository<GroupTask, Long> {

    /**
     * Finds all tasks associated with a specific group ID.
     * The return type is List<GroupTask>. If no tasks are found for the group,
     * this method will correctly return an empty list.
     *
     * @param groupId The ID of the group to find tasks for.
     * @return A list of tasks for the given group; never null.
     */
    List<GroupTask> findByGroup_Id(Long groupId);

}