package com.helpdesk.helpdeskplatform.messaging;

import com.helpdesk.helpdeskplatform.service.SlaBreachService;
import com.helpdesk.helpdeskplatform.service.SlaTimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlaChecker {

    private final SlaTimerService slaTimerService;
    private final SlaBreachService slaBreachService;

    @Scheduled(fixedDelay = 1000)
    public void checkOverDue(){
        for(String member : slaTimerService.findOverdue(Instant.now().getEpochSecond(), 50)){
            Long ticketId = null;
            boolean done;
            try{
                ticketId = Long.valueOf(member);
                boolean recorded = slaBreachService.recordBreachIfStillOpen(ticketId);
                if(recorded){
                    log.warn("SLA breached for ticket {}", ticketId);
                }
                done = true;
            } catch (DataIntegrityViolationException e){
                done = true;
            } catch (Exception e){
                log.warn("Could not check overdue ticket {} yet, will retry: {}", member, e.toString());
                done = false;
            }

            if(done && ticketId != null){
                slaTimerService.cancel(ticketId);
            }
        }
    }

}
