package org.example.server.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import org.example.server.engine.MdcAppender;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogbackUtils {

    static PatternLayout layout = new PatternLayout();

    static {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        layout.setContext(context);
        layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        layout.start();
    }

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
                sb.append(layout.doLayout(event)).append("\n");
            });
            appender.clear();
        }
        return sb.toString();
    }
}