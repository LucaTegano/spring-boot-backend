package com.example.demo.repository;

import com.example.demo.model.Trash;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrashRepository extends JpaRepository<Trash, Long> {

    /**
     * Finds all trashed items owned by a specific user, ordered by deletedAt
     * descending
     * 
     * @param owner The user who owns the trashed items
     * @return A list of trashed items for the given user
     */
    List<Trash> findByOwnerOrderByDeletedAtDesc(User owner);

    /**
     * Finds a specific trashed item by its ID and owner
     * 
     * @param id    The ID of the trashed item
     * @param owner The user who owns the trashed item
     * @return The trashed item if found
     */
    Trash findByIdAndOwner(Long id, User owner);
}