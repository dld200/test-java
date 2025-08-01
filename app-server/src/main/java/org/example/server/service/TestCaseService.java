package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.*;
import org.example.server.engine.Executor;
import org.example.server.engine.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TestCaseService  {
    
    // 模拟数据存储
    private Map<Long, TestCase> testCaseRepository = new ConcurrentHashMap<>();
    private Map<Long, TestResult> testRecordRepository = new ConcurrentHashMap<>();
    private Map<Long, List<TestResult>> testCaseRecordsMap = new ConcurrentHashMap<>();
    
    // 测试执行监听器
    @Autowired
    private TestListener executionListener;
    
    // 模拟ID生成器
    private long testCaseIdCounter = 1;
    private long testRecordIdCounter = 1;
    private long statementResultIdCounter = 1;
    private long screenshotIdCounter = 1;
    private long networkRecordIdCounter = 1;
    
    
    public TestResult executeTestCase(TestCase testCase) {
        log.info("Executing test case: {}", testCase.getTitle());
        
        // 创建测试记录对象
        TestResult testResult = new TestResult();
        testResult.setId(testRecordIdCounter++);
        testResult.setTestCaseId(testCase.getId());
        testResult.setTestCaseName(testCase.getTitle());
        testResult.setStartTime(new Date());
        testResult.setStatus("RUNNING");
        
        // 通知监听器测试开始
        executionListener.onTestStart(testResult);
        
        try {
            // 创建DSL执行器并执行测试用例
            Executor executor = new Executor();
            Context context = new Context(testCase);
            Context resultContext = executor.execute(context);
            
            // 收集执行结果
            List<Statement> statements = collectStatementResults(testResult.getId(), resultContext);
            List<Screenshot> screenshots = collectScreenshotRecords(testResult.getId());
            List<Transaction> transactions = collectNetworkRecords(testResult.getId());
            
            // 通知监听器各种记录的生成
            for (Statement statement : statements) {
                executionListener.onStatementComplete(statement);
            }
            
            for (Screenshot screenshot : screenshots) {
                executionListener.onScreenshotTaken(screenshot);
            }
            
            for (Transaction transaction : transactions) {
                executionListener.onNetworkRecorded(transaction);
            }
            
            // 设置测试记录
            testResult.setStatements(statements);
//            testResult.setScreenshots(screenshots);
//            testResult.setNetworks(transactions);
            testResult.setStatus("PASSED");
        } catch (Exception e) {
            testResult.setStatus("FAILED");
            testResult.setErrorMessage(e.getMessage());
            // 通知监听器测试出错
            executionListener.onTestError(testResult, e);
            log.error("Test execution failed", e);
        } finally {
            testResult.setEndTime(new Date());
            if (testResult.getStartTime() != null) {
                testResult.setDuration(testResult.getEndTime().getTime() - testResult.getStartTime().getTime());
            }
            
            // 通知监听器测试完成
            executionListener.onTestComplete(testResult);
            
            // 保存测试记录
            saveTestRecord(testResult);
        }
        
        return testResult;
    }

    
    public TestResult executeTestCase(Long testCaseId) {
        TestCase testCase = testCaseRepository.get(testCaseId);
        if (testCase == null) {
            throw new IllegalArgumentException("Test case not found with id: " + testCaseId);
        }
        return executeTestCase(testCase);
    }
    
    
    public TestResult getTestRecord(Long testRecordId) {
        return testRecordRepository.get(testRecordId);
    }
    
    
    public List<TestResult> getTestRecordsByTestCase(Long testCaseId) {
        return testCaseRecordsMap.getOrDefault(testCaseId, new ArrayList<>());
    }
    
    
    public TestResult saveTestRecord(TestResult testResult) {
        testRecordRepository.put(testResult.getId(), testResult);
        
        // 将测试记录关联到测试用例
        List<TestResult> records = testCaseRecordsMap.computeIfAbsent(
            testResult.getTestCaseId(), k -> new ArrayList<>());
        records.add(testResult);
        
        return testResult;
    }

    
    public TestResult getTestResult(Long id) {
        return null;
    }

    
    public Iterable<Object> getTestResultsByTestCase(Long id) {
        return null;
    }

    private List<Statement> collectStatementResults(Long testRecordId, Context context) {
        List<Statement> statements = new ArrayList<>();
        
        // 模拟收集语句执行结果
        // 在实际应用中，这会从执行器中获取真实的语句执行信息
        Statement statement = new Statement();
        statement.setId(statementResultIdCounter++);
        statement.setTestRecordId(testRecordId);
        statement.setStatement("Sample statement execution");
        statement.setStatus("PASSED");
        statement.setStartTime(new Date(System.currentTimeMillis() - 1000));
        statement.setEndTime(new Date());
        statement.setDuration(1000);
        statements.add(statement);
        return statements;
    }
    
    private List<Screenshot> collectScreenshotRecords(Long testRecordId) {
        List<Screenshot> screenshots = new ArrayList<>();
        
        // 模拟收集截图记录
        Screenshot screenshot = new Screenshot();
        screenshot.setId(screenshotIdCounter++);
        screenshot.setFilePath("/screenshots/sample_screenshot.png");
        screenshot.setFileName("sample_screenshot.png");
        screenshot.setCreateTime(new Date());
        screenshot.setDescription("Sample screenshot");
        
        screenshots.add(screenshot);
        return screenshots;
    }
    
    private List<Transaction> collectNetworkRecords(Long testRecordId) {
        List<Transaction> transactions = new ArrayList<>();
        
        // 模拟收集网络记录
        Transaction transaction = new Transaction();
        transaction.setUrl("https://api.example.com/test");
        transaction.setMethod("GET");
        transaction.setHeaders("Content-Type: application/json");
        transaction.setRequest("");
        transaction.setResponse("{\"message\": \"success\"}");
        transaction.setDuration(250);
        transaction.setCreateTime(new Date());
        transactions.add(transaction);
        return transactions;
    }
    
    // 辅助方法：添加测试用例到仓库
    public void addTestCase(TestCase testCase) {
        if (testCase.getId() == null) {
            testCase.setId(testCaseIdCounter++);
        }
        testCaseRepository.put(testCase.getId(), testCase);
    }

}