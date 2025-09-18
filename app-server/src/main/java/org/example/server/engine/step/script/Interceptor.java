package org.example.server.engine.step.script;

import org.example.server.engine.MobileContext;

public interface Interceptor {

    void beforeExecution(MobileContext mobileContext);

    void afterExecution(MobileContext mobileContext);

    void beforeKeyword(MobileContext mobileContext, String keyword);

    void afterKeyword(MobileContext mobileContext, String keyword);
}