package org.example.server.engine.keywords;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.device.Automation;
import org.example.mobile.device.impl.IosSimulatorAutomation;
import org.example.server.engine.Context;
import org.example.server.engine.Keyword;

@Slf4j
public class SetupKeyword implements Keyword {
    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public Object execute(Context context, Object... args) {
        context.setDeviceId(args[0].toString());
        context.setBundleId(args[1].toString());
        Automation automation = new IosSimulatorAutomation();
        context.setAutomation(automation);
        automation.setup(context.getDeviceId(), context.getBundleId());
        return null;
    }
}