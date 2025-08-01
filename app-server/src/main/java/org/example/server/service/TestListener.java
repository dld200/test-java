package org.example.server.service;

import lombok.extern.slf4j.Slf4j;
import org.example.common.domain.Screenshot;
import org.example.common.domain.Statement;
import org.example.common.domain.TestResult;
import org.example.common.domain.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 测试执行监听器的默认实现
 * 负责保存测试记录、网络记录和截图记录
 */
@Slf4j
@Service
public class TestListener {

    // 使用线程安全的列表存储各种记录
    private final List<TestResult> testResults = new CopyOnWriteArrayList<>();
    private final List<Statement> statements = new CopyOnWriteArrayList<>();
    private final List<Screenshot> screenshots = new CopyOnWriteArrayList<>();
    private final List<Transaction> transactions = new CopyOnWriteArrayList<>();

    public void onTestStart(TestResult testResult) {
        log.info("Test started: {}", testResult.getTestCaseName());
        // 可以在这里添加测试开始时的处理逻辑
    }

    
    public void onTestComplete(TestResult testResult) {
        log.info("Test completed: {} with status: {}", testResult.getTestCaseName(), testResult.getStatus());
        // 保存测试结果
        testResults.add(testResult);
    }

    
    public void onStatementStart(Statement statement) {
        log.info("Statement started: {}", statement.getStatement());
        // 可以在这里添加语句开始执行时的处理逻辑
    }

    
    public void onStatementComplete(Statement statement) {
        log.info("Statement completed: {} with status: {}", statement.getStatement(), statement.getStatus());
        // 保存语句执行结果
        statements.add(statement);
    }

    
    public void onScreenshotTaken(Screenshot screenshot) {
        log.info("Screenshot taken: {}", screenshot.getFileName());
        // 保存截图记录
        screenshots.add(screenshot);
    }

    
    public void onNetworkRecorded(Transaction transaction) {
        log.info("Network recorded: {}", transaction.getUrl());
        // 保存网络记录
        transactions.add(transaction);
    }

    
    public void onTestError(TestResult testResult, Throwable throwable) {
        log.error("Test error in: {} with error: {}", testResult.getTestCaseName(), throwable.getMessage());
        // 可以在这里添加错误处理逻辑
    }

    // Getter方法，用于获取各种记录列表
    public List<TestResult> getTestRecords() {
        return new ArrayList<>(testResults);
    }

    public List<Statement> getStatementResults() {
        return new ArrayList<>(statements);
    }

    public List<Screenshot> getScreenshotRecords() {
        return new ArrayList<>(screenshots);
    }

    public List<Transaction> getNetworkRecords() {
        return new ArrayList<>(transactions);
    }

    // 清空所有记录的方法
    public void clearAllRecords() {
        testResults.clear();
        statements.clear();
        screenshots.clear();
        transactions.clear();
    }
}