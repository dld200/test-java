package org.example.server.engine;

public interface Interceptor {

    void beforeExecution(Context context);

    void afterExecution(Context context);

    void beforeKeyword(Context context, String keyword);

    void afterKeyword(Context context, String keyword);
}