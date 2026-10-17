package com.helpdesk.helpdeskplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "app.sla")
public class SlaProperties {

    private Map<String, Long> responseSeconds = new HashMap<>();

    public long responseSecondsFor(String priority){
        Long seconds = responseSeconds.get(priority.toLowerCase(Locale.ROOT));
        if(seconds == null){
            throw new IllegalArgumentException("No SLA configured for priority " + priority);
        }
        return seconds;
    }

}
