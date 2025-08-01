package org.example.server.engine;

public interface Interceptor {
    /**
     * 在DSL语句执行前调用
     * @param context 执行上下文
     * @param statement 当前要执行的语句
     */
    void beforeStatement(Context context, String statement);

    /**
     * 在DSL语句执行后调用
     * @param context 执行上下文
     * @param statement 已执行的语句
     */
    void afterStatement(Context context, String statement);

    /**
     * 在测试用例执行前调用
     * @param context 执行上下文
     */
    void beforeExecution(Context context);

    /**
     * 在测试用例执行后调用
     * @param context 执行上下文
     */
    void afterExecution(Context context);
}