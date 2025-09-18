package org.example.server.engine.step.script.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.automation.Automation;

@Slf4j
public class SetupKeyword implements Keyword {
    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public Object execute(Automation automation, Object... args) {
        String deviceId = args[0].toString();
        String bundleId = args[1].toString();
        return automation.setup(deviceId, bundleId);
    }
}