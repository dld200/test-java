package org.example.server.engine.groovy;

public interface Interceptor {

    void beforeExecution(MobileContext mobileContext);

    void afterExecution(MobileContext mobileContext);

    void beforeKeyword(MobileContext mobileContext, String keyword);

    void afterKeyword(MobileContext mobileContext, String keyword);
}