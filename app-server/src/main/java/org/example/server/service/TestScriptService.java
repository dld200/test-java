//package org.example.server.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.example.common.domain.*;
//import org.example.common.domain.Record;
//import org.example.mobile.automation.IosSimulatorAutomation;
//import org.example.server.dto.DebugReq;
//import org.example.server.engine.groovy.MobileContext;
//import org.example.server.engine.groovy.DefaultInterceptor;
//import org.example.server.engine.groovy.Executor;
//import org.example.server.util.LogbackUtil;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Service
//public class TestScriptService {
//
//    // 模拟数据存储
//    private Map<Long, TestCase> testCaseRepository = new ConcurrentHashMap<>();
//    private Map<Long, TestResult> testRecordRepository = new ConcurrentHashMap<>();
//    private Map<Long, List<TestResult>> testCaseRecordsMap = new ConcurrentHashMap<>();
//
//    // 模拟ID生成器
//    private long testCaseIdCounter = 1;
//    private long testRecordIdCounter = 1;
//    private long statementResultIdCounter = 1;
//    private long screenshotIdCounter = 1;
//    private long networkRecordIdCounter = 1;
//
//    public String debug(DebugReq req) {
//
//        // 内存日志记录
//        LogbackUtil.on();
//
//        // 创建执行器
//        Executor executor = new Executor();
//
//        // 添加拦截器
//        executor.addInterceptor(new DefaultInterceptor());
//
//        // 设置执行选项
//        Map<String, Object> options = new HashMap<>();
//        options.put("sleep.before.keyword", 800);
//        options.put("sleep.before.execution", 500);
//
//        // 执行测试用例
//        MobileContext mobileContext = new MobileContext();
//        mobileContext.setOptions(options);
//        mobileContext.setVariables(req.getVariables());
//        mobileContext.setAutomation(new IosSimulatorAutomation());
//        TestCase testCase = TestCase.builder().script(req.getScript()).build();
//        mobileContext.setTestCase(testCase);
//
//        String logs = "";
//        try {
//            MobileContext result = executor.execute(mobileContext);
//        } catch (Throwable e) {
//            log.error("Test execution failed: ", e);
//        } finally {
//            logs = LogbackUtil.getLogs();
//            LogbackUtil.off();
//        }
//        return logs;
//    }
//
//    public TestResult executeTestCase(Long testCaseId) {
//        TestCase testCase = testCaseRepository.get(testCaseId);
//        if (testCase == null) {
//            throw new IllegalArgumentException("Test case not found with id: " + testCaseId);
//        }
////        return executeTestCase(testCase);
//        return null;
//    }
//
//
//    public TestResult getTestRecord(Long testRecordId) {
//        return testRecordRepository.get(testRecordId);
//    }
//
//
//    public List<TestResult> getTestRecordsByTestCase(Long testCaseId) {
//        return testCaseRecordsMap.getOrDefault(testCaseId, new ArrayList<>());
//    }
//
//
//    public TestResult saveTestRecord(TestResult testResult) {
//        testRecordRepository.put(testResult.getId(), testResult);
//
//        // 将测试记录关联到测试用例
//        List<TestResult> records = testCaseRecordsMap.computeIfAbsent(
//                testResult.getTestCaseId(), k -> new ArrayList<>());
//        records.add(testResult);
//
//        return testResult;
//    }
//
//
//    public TestResult getTestResult(Long id) {
//        return null;
//    }
//
//
//    public Iterable<Object> getTestResultsByTestCase(Long id) {
//        return null;
//    }
//
//    private List<Record> collectNetworkRecords(Long testRecordId) {
//        List<Record> records = new ArrayList<>();
//
//        // 模拟收集网络记录
//        Record record = new Record();
//        record.setUrl("https://api.example.com/test");
//        record.setMethod("GET");
//        record.setHeaders("Content-Type: application/json");
//        record.setRequest("");
//        record.setResponse("{\"message\": \"success\"}");
//        record.setDuration(250);
//        record.setCreateTime(new Date());
//        records.add(record);
//        return records;
//    }
//
//    // 辅助方法：添加测试用例到仓库
//    public void addTestCase(TestCase testCase) {
//        if (testCase.getId() == null) {
//            testCase.setId(testCaseIdCounter++);
//        }
//        testCaseRepository.put(testCase.getId(), testCase);
//    }
//}