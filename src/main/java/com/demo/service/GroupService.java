package com.demo.service;

import com.demo.model.ChatGroup;
import com.demo.model.GroupMessage;
import com.demo.model.dto.CreateGroupRequest;
import com.demo.model.dto.GroupMessageRequest;
import com.demo.repo.GroupMessageRepository;
import com.demo.repo.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ✅ CREATE GROUP
    public ChatGroup createGroup(CreateGroupRequest request) {

        ChatGroup group = new ChatGroup();
        group.setName(request.getName());
        group.setCreatedBy(request.getCreatedBy());
        group.setMembers(request.getMembers());
        group.setCreatedAt(LocalDateTime.now());

        ChatGroup savedGroup = groupRepository.save(group);

        for (String memberId : savedGroup.getMembers()) {
            messagingTemplate.convertAndSendToUser(
                    memberId,
                    "/queue/group-created",
                    savedGroup
            );
        }
        return savedGroup;
    }

    // ✅ SEND GROUP MESSAGE
    public void processGroupMessage(GroupMessageRequest request) {

        // 1. save message
        GroupMessage msg = new GroupMessage();
        msg.setGroupId(request.getGroupId());
        msg.setSenderId(request.getSenderId());
        msg.setSenderName(request.getSenderName());
        msg.setContent(request.getContent());
        msg.setTimestamp(LocalDateTime.now());

        groupMessageRepository.save(msg);

        messagingTemplate.convertAndSend("/topic/group/" + request.getGroupId(), msg);
    }
    // ✅ GET GROUP MESSAGES (PAGINATION)
    public List<GroupMessage> getGroupMessages(String groupId, int page) {

        Pageable pageable = PageRequest.of(
                page,
                10,
                Sort.by("timestamp").descending()
        );

        return groupMessageRepository
                .findByGroupId(groupId, pageable)
                .getContent();
    }

    public void deleteGroup(String groupId) {
        ChatGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        List<String> memberIds = group.getMembers(); // save before delete

        // Delete all messages first, then the group
        groupMessageRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);

        // Notify every member so their UI removes it immediately
        Map<String, Object> payload = Map.of(
                "type", "GROUP_DELETED",
                "groupId", groupId
        );
        memberIds.forEach(memberId ->
                messagingTemplate.convertAndSendToUser(
                        memberId,
                        "/queue/group-deleted",
                        payload
                )
        );
    }
}