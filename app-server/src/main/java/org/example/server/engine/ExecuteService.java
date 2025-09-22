package org.example.server.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.*;
import org.example.server.dao.TestCaseRunDao;
import org.example.server.dao.TestDeviceDao;
import org.example.server.dao.TestStepRunDao;
import org.example.server.engine.step.IStep;
import org.example.server.util.JsonTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExecuteService {

    @Autowired
    private TestCaseRunDao testCaseRunDao;

    @Autowired
    private TestStepRunDao testStepRunDao;

    @Autowired
    private TestDeviceDao testDeviceDao;

    @Autowired
    private Map<String, IStep> stepMap;

    public void execute(TestCase testCase, Map<String, Object> runtimeParams, Long deviceId) {
        ExecuteContext context = new ExecuteContext();

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
                .status("running")
                .startTime(new java.util.Date())
                .build();
        testCaseRunDao.save(testCaseRun);

        if (deviceId != null) {
            Optional<TestDevice> testDevice = testDeviceDao.findById(deviceId);
            if (testDevice.isEmpty()) {
                testCaseRun.setOutput("Error: Device not found");
                testCaseRunDao.save(testCaseRun);
            } else {
                MobileContext mobileContext = MobileContext.builder()
                        .testCase(testCase)
//                        .options(params)
//                        .variables(params)
                        .testDevice(testDevice.get())
                        .deviceId(testDevice.get().getUuid())
                        .bundleId(testDevice.get().getInfo())
                        .build();
                context.setMobileContext(mobileContext);
            }
        }

        for (TestStep testStep : testCase.getSteps()) {
            TestStepRun testStepRun = TestStepRun.builder()
                    .testCaseRunId(testCaseRun.getId())
                    .testStepId(testStep.getId())
                    .testStepName(testStep.getName())
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
                testStepRun.setStatus("failed");
                testStepRun.setOutput("Error: " + e.getMessage());
                testCaseRun.setOutput("Error: " + e.getMessage());
                failed = true;
                break;
            } finally {
                testStepRun.setEndTime(new Date());
                testStepRunDao.save(testStepRun);
            }
        }
        if (failed) {
            testCaseRun.setStatus("failed");
        } else {
            testCaseRun.setStatus("success");
        }
        testCaseRun.setEndTime(new Date());
        testCaseRunDao.save(testCaseRun);
    }
}