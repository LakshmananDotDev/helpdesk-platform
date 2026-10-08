package com.helpdesk.helpdeskplatform.mapper;

import com.helpdesk.helpdeskplatform.dto.response.CommentResponse;
import com.helpdesk.helpdeskplatform.entity.Comment;
import com.helpdesk.helpdeskplatform.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        User author = comment.getAuthor();
        return new CommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                author.getId(),
                author.getEmail(),
                comment.getBody(),
                comment.getCreatedAt()
        );
    }
}