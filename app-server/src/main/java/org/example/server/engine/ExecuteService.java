package org.example.server.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.*;
import org.example.mobile.automation.android.AndroidAutomation;
import org.example.mobile.automation.ios.IosAutomation;
import org.example.mobile.dto.Device;
import org.example.server.dao.AssetDao;
import org.example.server.dao.TestCaseRunDao;
import org.example.server.dao.TestStepRunDao;
import org.example.server.engine.step.IStep;
import org.example.server.util.JsonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExecuteService {

    @Autowired
    private TestCaseRunDao testCaseRunDao;

    @Autowired
    private TestStepRunDao testStepRunDao;

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private Map<String, IStep> stepMap;

    public TestCaseRun execute(TestCase testCase, Map<String, Object> runtimeParams, Long deviceId) {
        ExecuteContext context = new ExecuteContext();
        context.setTestCase(testCase);

        boolean failed = false;

        //传入参数和默认参数合并
        List<Map<String, Object>> testCaseParams = JSON.parseObject(testCase.getConfig(), new TypeReference<>() {
        });
        Map<String, Object> mergeParams = testCaseParams.stream()
                .collect(Collectors.toMap(m -> m.get("key").toString(),
                        m -> m.get("value")));
        mergeParams.putAll(runtimeParams);
        context.setRuntimeVariables(mergeParams);

        TestCaseRun testCaseRun = TestCaseRun.builder()
                .testCaseId(testCase.getId())
                .testCaseName(testCase.getName())
                .input(JSON.toJSONString(mergeParams))
                .steps(new ArrayList<>())
                .status("running")
                .startTime(new java.util.Date())
                .build();
        testCaseRunDao.save(testCaseRun);

        if (deviceId != null) {
            Optional<Asset> asset = assetDao.findById(deviceId);
            if (asset.isEmpty()) {
                testCaseRun.setOutput("Error: Device not found");
                testCaseRunDao.save(testCaseRun);
            } else {
                Device device = JSON.parseObject(asset.get().getInfo(), Device.class);

                MobileContext mobileContext = MobileContext.builder()
                        .testCase(testCase)
                        .options(new HashMap<>() {
                            {
                                put("sleep.before.keyword", 1000);
                            }
                        })
//                        .variables(params)
//                        .testDevice(testDevice.get())
                        .deviceId(asset.get().getUuid())
                        .bundleId("ca.snappay.snaplii.test")
                        .automation(device.getPlatform().equals("android")? new AndroidAutomation(): new IosAutomation())
                        .build();
                context.setMobileContext(mobileContext);
            }
        }

        for (TestStep testStep : testCase.getSteps()) {
            TestStepRun testStepRun = TestStepRun.builder()
                    .testCaseRun(testCaseRun)
                    .testStepId(testStep.getId())
                    .testStepName(String.format("Step%s: %s", testStep.getPosition(), testStep.getType()))
                    .status("running")
                    .startTime(new Date())
                    .build();

            try {
                IStep step = stepMap.get(testStep.getType() + "Step");
                Object output = step.execute(testStep, context.resolve(testStep.getConfig()), context);
                testStepRun.setOutput(JsonTool.toString(output));
                testStepRun.setStatus("success");
                context.setStepResult(testStep.getPosition(), JsonTool.toString(output));
            } catch (Exception e) {
                log.error("Error: ", e);
                testStepRun.setStatus("failed");
                testStepRun.setOutput("Error: " + e.getMessage());
                testCaseRun.setOutput("Error: " + e.getMessage());
                failed = true;
                break;
            } finally {
                //使用步骤结果
                testCaseRun.setOutput(testStepRun.getOutput());
                testStepRun.setEndTime(new Date());
                testStepRunDao.save(testStepRun);
                testCaseRun.getSteps().add(testStepRun);
            }
        }
        if (failed) {
            testCaseRun.setStatus("failed");
        } else {
            testCaseRun.setStatus("success");
        }
        testCaseRun.setEndTime(new Date());
        testCaseRunDao.save(testCaseRun);
        return testCaseRun;
    }
}