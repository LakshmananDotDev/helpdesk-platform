package com.helpdesk.helpdeskplatform.service;

import com.helpdesk.helpdeskplatform.dto.request.CommentCreateRequest;
import com.helpdesk.helpdeskplatform.dto.response.CommentResponse;
import com.helpdesk.helpdeskplatform.entity.Comment;
import com.helpdesk.helpdeskplatform.entity.Ticket;
import com.helpdesk.helpdeskplatform.entity.User;
import com.helpdesk.helpdeskplatform.exception.ResourceNotFoundException;
import com.helpdesk.helpdeskplatform.mapper.CommentMapper;
import com.helpdesk.helpdeskplatform.repository.CommentRepository;
import com.helpdesk.helpdeskplatform.repository.TicketRepository;
import com.helpdesk.helpdeskplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse addComment(Long organizationId, Long userId, Long ticketId, CommentCreateRequest request){
        Ticket ticket = ticketRepository.findByIdAndOrganizationId(ticketId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(user);
        comment.setBody(request.getBody());

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long organizationId, Long ticketId){
        ticketRepository.findByIdAndOrganizationId(ticketId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }
}
