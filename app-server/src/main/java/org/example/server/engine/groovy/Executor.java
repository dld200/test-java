package org.example.server.engine.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.groovy.keyword.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Executor {

    @Autowired
    private static final List<Keyword> keywords = new ArrayList<>();

    static {
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

    public MobileContext execute(MobileContext mobileContext) {
        mobileContext.setResult("success");
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeExecution(mobileContext);
        }
        try {
            executeDsl(mobileContext.getTestCase().getScript(), mobileContext);
        } catch (Throwable e) {
            log.error("Test execution failed: ", e);
            mobileContext.setResult(e.getMessage());
        } finally {
            for (Interceptor interceptor : interceptors) {
                interceptor.afterExecution(mobileContext);
            }
        }
        return mobileContext;
    }

    private void executeDsl(String dsl, MobileContext mobileContext) {
        Binding binding = new Binding();

        // 将上下文中的变量添加到绑定中
        if(mobileContext.getVariables() != null) {
            mobileContext.getVariables().forEach(binding::setVariable);
        }

        // 添加一些常用的内置函数
        binding.setVariable("ctx", mobileContext);
        binding.setVariable("log", System.out);

        // 添加关键字作为可调用函数到绑定中
        for (Keyword key : keywords) {
            // 创建一个闭包，将关键字包装成可以在Groovy中直接调用的函数
            Closure<Object> closure = new Closure<Object>(this) {
                public Object call(Object... args) {
                    for (Interceptor interceptor : interceptors) {
                        interceptor.beforeKeyword(mobileContext, key.getName());
                    }
                    Object object = key.execute(mobileContext, args);
                    for (Interceptor interceptor : interceptors) {
                        interceptor.afterKeyword(mobileContext, key.getName());
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