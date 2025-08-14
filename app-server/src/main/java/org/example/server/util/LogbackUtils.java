package org.example.server.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.example.server.engine.MdcAppender;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogbackUtils {

    public static void on() {
        MDC.put("logToMemory", "true");
    }

    public static void off() {
        MDC.remove("logToMemory");
    }

    public static MdcAppender getMdcAppender() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        return (MdcAppender) rootLogger.getAppender("MDC_MEMORY");
    }

    public static String getLogs() {
        MdcAppender appender = getMdcAppender();
        StringBuilder sb = new StringBuilder();
        if (appender != null) {
            appender.getEvents().forEach(event -> {
                sb.append(event.getFormattedMessage()).append("\n");
            });
            appender.clear();
        }
        return sb.toString();
    }
}