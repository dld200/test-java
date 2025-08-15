package org.example.server.engine;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.keywords.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Executor {

    @Autowired
    private static final List<Keyword> keywords = new ArrayList<>();

    static {
        // 添加关键字
        keywords.add(new SetupKeyword());
        keywords.add(new ClickKeyword());
        keywords.add(new InputKeyword());
        keywords.add(new ScreenshotKeyword());
        keywords.add(new SwipeKeyword());
    }

    private final List<Interceptor> interceptors = new ArrayList<>();

    public void addInterceptor(Interceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public Context execute(Context context) {
        context.setResult("success");
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeExecution(context);
        }
        try {
            executeDsl(context.getTestCase().getScript(), context);
        } catch (Throwable e) {
            log.error("Test execution failed: ", e);
            context.setResult(e.getMessage());
        } finally {
            for (Interceptor interceptor : interceptors) {
                interceptor.afterExecution(context);
            }
        }
        return context;
    }

    private void executeDsl(String dsl, Context context) {
        Binding binding = new Binding();

        // 将上下文中的变量添加到绑定中
        if(context.getVariables() != null) {
            context.getVariables().forEach(binding::setVariable);
        }

        // 添加一些常用的内置函数
        binding.setVariable("ctx", context);
        binding.setVariable("log", System.out);

        // 添加关键字作为可调用函数到绑定中
        for (Keyword key : keywords) {
            // 创建一个闭包，将关键字包装成可以在Groovy中直接调用的函数
            Closure<Object> closure = new Closure<Object>(this) {
                public Object call(Object... args) {
                    for (Interceptor interceptor : interceptors) {
                        interceptor.beforeKeyword(context, key.getName());
                    }
                    Object object = key.execute(context, args);
                    for (Interceptor interceptor : interceptors) {
                        interceptor.afterKeyword(context, key.getName());
                    }
                    return object;
                }
            };
            binding.setVariable(key.getName(), closure);
        }

        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(dsl);
    }
}