package org.example.server.engine;

public interface Keyword {
    /**
     * 获取关键字名称
     * @return 关键字名称
     */
    String getName();

    /**
     * 执行关键字逻辑
     * @param context 执行上下文
     * @param args 关键字参数
     * @return 执行结果
     */
    Object execute(Context context, Object... args);
}