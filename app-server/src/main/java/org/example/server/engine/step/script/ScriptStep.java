package org.example.server.engine.step.script;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.TestStep;
import org.example.server.engine.ExecuteContext;
import org.example.server.engine.StepFactory;
import org.example.server.engine.step.IStep;
import org.example.server.engine.step.script.keyword.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class ScriptStep implements IStep {
    @Autowired
    private static final List<Keyword> keywords = new ArrayList<>();

    private static Interceptor interceptor = new DefaultInterceptor();

    private String script;

    static {
        StepFactory.registerStep("script", ScriptStep.class);

        keywords.add(new SetupKeyword());
        keywords.add(new ClickKeyword());
        keywords.add(new InputKeyword());
        keywords.add(new ScreenshotKeyword());
        keywords.add(new SwipeKeyword());
    }

    @Override
    public String getName() {
        return "script";
    }

    @Override
    public String execute(TestStep testStep, ExecuteContext context) {
        Object result = new Object();
        interceptor.beforeExecution(context.getMobileContext());
        try {
            Binding binding = new Binding();

            // todo: 将上下文中的变量添加到绑定中
//            if (context.getMobileContext().getVariables() != null) {
//                context.getMobileContext().getVariables().forEach(binding::setVariable);
//            }

            // 添加一些常用的内置函数
            binding.setVariable("ctx", context.getMobileContext());
            binding.setVariable("log", System.out);

            // 添加关键字作为可调用函数到绑定中
            for (Keyword key : keywords) {
                // 创建一个闭包，将关键字包装成可以在Groovy中直接调用的函数
                Closure<Object> closure = new Closure<Object>(this) {
                    public Object call(Object... args) {
                        interceptor.beforeKeyword(context.getMobileContext(), key.getName());
                        Object object = key.execute(context.getMobileContext(), args);
                        interceptor.afterKeyword(context.getMobileContext(), key.getName());
                        return object;
                    }
                };
                binding.setVariable(key.getName(), closure);
            }

            GroovyShell shell = new GroovyShell(binding);
            result = shell.evaluate(script);
        } catch (Throwable e) {
            log.error("Test execution failed: ", e);
            context.getMobileContext().setResult(e.getMessage());
        } finally {
            interceptor.afterExecution(context.getMobileContext());
        }
        return result.toString();
    }
}
