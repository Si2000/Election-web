package com.election.backendjava.api;

import com.election.backendjava.dto.CommentResponseDTO;
import com.election.backendjava.model.Topic;
import com.election.backendjava.service.CommentService;
import com.election.backendjava.service.TopicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final TopicService topicService;
    private final CommentService commentService;

    public ProfileController(TopicService topicService, CommentService commentService) {
        this.topicService = topicService;
        this.commentService = commentService;
    }

    @GetMapping("/{userId}/topics")
    public List<Topic> getUserTopics(@PathVariable Long userId) {
        return topicService.getTopicsByUserId(userId);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentResponseDTO> getUserComments(@PathVariable Long userId) {
        return commentService.getCommentsByUserId(userId);
    }
}

