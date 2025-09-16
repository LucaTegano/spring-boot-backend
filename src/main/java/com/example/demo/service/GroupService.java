package com.example.demo.service;

import com.example.demo.model.Group;
import com.example.demo.model.GroupTask;
import com.example.demo.model.Note;
import com.example.demo.model.User;
import com.example.demo.repository.GroupRepository;
import com.example.demo.repository.GroupTaskRepository;
import com.example.demo.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupTaskRepository groupTaskRepository;
    private final NoteRepository noteRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<Group> getAllGroupsForUser(String username) {
        User user = userService.getUserByUsername(username);
        List<Group> ownedGroups = groupRepository.findByOwner(user);
        List<Group> memberGroups = groupRepository.findByMembersContains(user);

        return Stream.concat(ownedGroups.stream(), memberGroups.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public Group createGroup(String name, String username) {
        User user = userService.getUserByUsername(username);

        Group group = new Group();
        group.setName(name);
        group.setOwner(user);
        group.addMember(user);

        return groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public Group getGroupById(Long groupId, String username) {
        User user = userService.getUserByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        if (!group.getOwner().equals(user) && !group.getMembers().contains(user)) {
            throw new RuntimeException("User does not have access to this group");
        }

        return group;
    }

    @Transactional
    public Group joinGroup(Long groupId, String username) {
        User user = userService.getUserByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        if (!group.getMembers().contains(user)) {
            group.addMember(user);
            group = groupRepository.save(group);
        }

        return group;
    }

    @Transactional
    public void leaveGroup(Long groupId, String username) {
        User user = userService.getUserByUsername(username);
        Group group = getGroupEntityById(groupId, username);

        if (group.getOwner().equals(user)) {
            throw new RuntimeException("Owner cannot leave their own group");
        }

        group.removeMember(user);
        groupRepository.save(group);
    }

    @Transactional(readOnly = true)
    public List<GroupTask> getGroupTasks(Long groupId, String username) {
        getGroupEntityById(groupId, username); // Verify access
        return groupTaskRepository.findByGroup_Id(groupId);
    }

    @Transactional
    public GroupTask createGroupTask(Long groupId, String text, String username) {
        Group group = getGroupEntityById(groupId, username);

        GroupTask task = new GroupTask();
        task.setText(text);
        task.setGroup(group);
        task.setCompleted(false);

        return groupTaskRepository.save(task);
    }

    @Transactional
    public GroupTask updateGroupTask(Long groupId, Long taskId, String text, boolean completed, String username) {
        getGroupEntityById(groupId, username); // Verify access

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Task does not belong to this group");
        }

        task.setText(text);
        task.setCompleted(completed);

        return groupTaskRepository.save(task);
    }

    @Transactional
    public void deleteGroupTask(Long groupId, Long taskId, String username) {
        getGroupEntityById(groupId, username); // Verify access

        GroupTask task = groupTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        if (!task.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Task does not belong to this group");
        }

        groupTaskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public Set<User> getGroupMembers(Long groupId, String username) {
        Group group = getGroupEntityById(groupId, username);
        return group.getMembers();
    }

    @Transactional(readOnly = true)
    public List<Note> getGroupNotes(Long groupId, String username) {
        getGroupEntityById(groupId, username); // Verify access
        return noteRepository.findByGroup_Id(groupId);
    }

    private Group getGroupEntityById(Long groupId, String username) {
        User user = userService.getUserByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        if (!group.getOwner().equals(user) && !group.getMembers().contains(user)) {
            throw new RuntimeException("User does not have access to this group");
        }

        return group;
    }
}