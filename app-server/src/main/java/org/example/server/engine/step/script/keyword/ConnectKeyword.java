package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.server.engine.MobileContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectKeyword implements Keyword {
    @Override
    public String getName() {
        return "connect";
    }

    @Override
    public Object execute(MobileContext context, Object... args) {
        String deviceId = args[0].toString();
//        String bundleId = args[1].toString();
        return context.getAutomation().connect(deviceId);
//        return context.getAutomation().launch(bundleId);
    }
}