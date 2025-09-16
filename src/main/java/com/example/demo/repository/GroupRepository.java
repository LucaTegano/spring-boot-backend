package com.example.demo.repository;

import com.example.demo.model.Group;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Finds all groups where the given user is the owner.
     *
     * @param owner The user who owns the groups.
     * @return A list of groups owned by the user.
     */
    List<Group> findByOwner(User owner);

    /**
     * Finds all groups where the given user is a member.
     *
     * @param user The user who is a member of the groups.
     * @return A list of groups where the user is a member.
     */
    List<Group> findByMembersContains(User user);
}