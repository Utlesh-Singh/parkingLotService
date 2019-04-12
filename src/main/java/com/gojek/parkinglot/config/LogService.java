package com.gojek.parkinglot.config;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private Map<String, String> context = new HashMap();

    public LogService() {
    }

    @PostConstruct
    public void init() {
            this.context.put("app_name", "gojek-parking-service");
            this.context.put("application_version","1.0.0-SNAPSHOT");
    }

    public Logger getLogger(Class className) {
        Logger logger = new Logger(className, this.context);
        return logger;
    }
}
