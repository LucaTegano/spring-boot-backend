package com.example.demo.repository;

import com.example.demo.model.PersonalTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Long> {

    /**
     * Finds all tasks owned by a specific user, identified by their ID.
     * The return type is List<PersonalTask>. If the user has no tasks,
     * this method will correctly return an empty list.
     *
     * @param userId The ID of the user to find tasks for.
     * @return A list of tasks for the given user; never null.
     */
    List<PersonalTask> findByOwner_Id(Long userId);

}