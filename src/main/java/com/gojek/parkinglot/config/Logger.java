package com.gojek.parkinglot.config;


import com.gojek.parkinglot.errors.ErrorCode;
import com.gojek.parkinglot.exceptions.PlatformException;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Logger {
    org.slf4j.Logger logger;
    private Map<String, String> context;

    public Logger(Class classname, Map<String,String> contextMap){
        this.logger = LoggerFactory.getLogger(classname);
        if (contextMap != null) {
            Set<Map.Entry<String, String>> entrySet = contextMap.entrySet();
            Iterator iterator = entrySet.iterator();

            while(iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)iterator.next();
                try {
                    System.setProperty((String)entry.getKey(), (String)entry.getValue());
                } catch (SecurityException exception) {
                    this.logger.error("Failed to set system property [{}, {}] due to {} ", new Object[]{(String)entry.getKey(), (String)entry.getValue(), exception.getMessage()});
                }
            }
        }
    }

    public void setContext(String contextKey, String contextValue) {
        MDC.put(contextKey, contextValue);
        if (this.context != null) {
            this.context.put(contextKey, contextValue);
        } else {
            this.context = new HashMap();
            this.context.put(contextKey, contextValue);
        }

    }

    public void unsetContext() {
        if (this.context != null) {
            Set<String> keySet = this.context.keySet();
            Iterator iterator = keySet.iterator();

            while(iterator.hasNext()) {
                String key = (String)iterator.next();
                MDC.remove(key);
            }
        }
    }

    public void error(PlatformException platformException) {
        MDC.put("code", platformException.getErrorCode());
        this.logger.error(platformException.getMessage(), platformException);
        MDC.remove("code");
    }

    public void error(ErrorCode errorCode, Throwable throwable) {
        MDC.put("code", errorCode.getErrorCode());
        this.logger.error(errorCode.getMessage(), throwable);
        MDC.remove("code");
    }

    public void debug(String message) { this.logger.debug(message); }
    public void debug(String message, Throwable throwable) { this.logger.debug(message, throwable); }


    public void info(String message) {
        this.logger.info(message);
    }
    public void info(String message, Throwable throwable) {
        this.logger.info(message, throwable);
    }


    public void warn(String message) {
        this.logger.warn(message);
    }
    public void warn(String message, Throwable throwable) {
        this.logger.warn(message, throwable);
    }

}
