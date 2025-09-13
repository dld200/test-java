package org.example.server.engine.groovy.keyword;

import lombok.extern.slf4j.Slf4j;
import org.example.mobile.device.Automation;
import org.example.mobile.device.impl.IosSimulatorAutomation;
import org.example.server.engine.groovy.MobileContext;
import org.example.server.engine.groovy.Keyword;

@Slf4j
public class SetupKeyword implements Keyword {
    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public Object execute(MobileContext mobileContext, Object... args) {
        mobileContext.setDeviceId(args[0].toString());
        mobileContext.setBundleId(args[1].toString());
        Automation automation = new IosSimulatorAutomation();
        mobileContext.setAutomation(automation);
        automation.setup(mobileContext.getDeviceId(), mobileContext.getBundleId());
        return null;
    }
}