package org.example.server.engine;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executor {
    private List<Interceptor> interceptors = new ArrayList<>();
    ;
    private Map<String, Keyword> keywords = new HashMap<>();

    public Executor() {
        registerDefaultKeywords();
    }

    private void registerDefaultKeywords() {
        registerKeyword(new DefaultKeywords.NavigateKeyword());
        registerKeyword(new DefaultKeywords.ClickKeyword());
        registerKeyword(new DefaultKeywords.InputKeyword());
        registerKeyword(new DefaultKeywords.AssertKeyword());
        registerKeyword(new DefaultKeywords.WaitKeyword());
        registerKeyword(new DefaultKeywords.ScreenshotKeyword());
        registerKeyword(new DefaultKeywords.LogKeyword());
    }

    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public void removeInterceptor(Interceptor interceptor) {
        this.interceptors.remove(interceptor);
    }

    public void registerKeyword(Keyword keyword) {
        this.keywords.put(keyword.getName(), keyword);
    }

    public Context execute(Context context) {
        // 执行前拦截
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeExecution(context);
        }

        try {
            Object result = executeDsl(context.getTestCase().getDsl(), context);
            context.setResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Error executing DSL for test case: " + context.getTestCase().getId(), e);
        } finally {
            // 执行后拦截
            for (Interceptor interceptor : interceptors) {
                interceptor.afterExecution(context);
            }
        }
        return context;
    }

    private Object executeDsl(String dsl, Context context) {
        Binding binding = new Binding();

        // 将上下文中的变量添加到绑定中
        context.getVariables().forEach(binding::setVariable);

        // 添加一些常用的内置函数
        binding.setVariable("ctx", context);
//        binding.setVariable("log", System.out);

        // 添加关键字作为可调用函数到绑定中
        for (Map.Entry<String, Keyword> entry : keywords.entrySet()) {
            Keyword keyword = entry.getValue();
            // 创建一个闭包，将关键字包装成可以在Groovy中直接调用的函数
            Closure<Object> closure = new Closure<Object>(this) {
                public Object call(Object... args) {
                    return keyword.execute(context, args);
                }
            };
            binding.setVariable(entry.getKey(), closure);
        }

        GroovyShell shell = new GroovyShell(binding);

        // 简化处理：将DSL按行分割，逐行执行（实际应用中可能需要更复杂的解析）
        String[] statements = dsl.split("\n");
        Object result = null;

        for (String statement : statements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("#")) { // 忽略空行和注释
                // 执行前拦截
                for (Interceptor interceptor : interceptors) {
                    interceptor.beforeStatement(context, trimmedStatement);
                }
                try {
                    // todo： 引擎现场还在吗
                    result = shell.evaluate(trimmedStatement);
                } catch (Throwable e) {

                } finally {
                    // 执行后拦截
                    for (Interceptor interceptor : interceptors) {
                        interceptor.afterStatement(context, trimmedStatement);
                    }
                }
            }
        }

        return result;
    }
}