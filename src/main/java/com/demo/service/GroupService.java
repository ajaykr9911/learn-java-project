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

        return groupRepository.save(group);
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

        // 2. fetch group
        ChatGroup group = groupRepository
                .findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // 3. send to all members
        for (String memberId : group.getMembers()) {
            messagingTemplate.convertAndSend(
                    "/topic/group/" + request.getGroupId(),
                    msg
            );
        }
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
}