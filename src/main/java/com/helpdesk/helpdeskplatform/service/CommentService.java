package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.request.CommentCreateRequest;
import com.helpdesk.helpdeskplatform.dto.response.CommentResponse;
import com.helpdesk.helpdeskplatform.entity.Comment;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.entity.User;
import com.helpdesk.helpdeskplatform.exception.ResourceNotFoundException;
import com.helpdesk.helpdeskplatform.mapper.CommentMapper;
import com.helpdesk.helpdeskplatform.repository.CommentRepository;
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import com.helpdesk.helpdeskplatform.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final TicketService ticketService;

    @Transactional
    public CommentResponse addComment(AuthenticatedUser actor, Long ticketId, CommentCreateRequest request) {
        Ticket ticket = ticketService.loadVisibleTicket(actor, ticketId);
        User author = userRepository.findById(actor.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + actor.userId()));

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setBody(request.getBody());

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(AuthenticatedUser actor, Long ticketId) {
        ticketService.loadVisibleTicket(actor, ticketId);
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }
}