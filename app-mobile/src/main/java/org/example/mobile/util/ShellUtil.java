package org.example.mobile.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public class ShellUtil {
    /**
     * 执行系统命令并返回标准输出字符串
     *
     * @param command 完整命令字符串，例如 "adb devices -l"
     * @return 命令执行的输出文本（多行合并成一段字符串）
     */
    public static String exec(String command) {
        log.info("Execute command: {}", command);
        try {
            Process process = Runtime.getRuntime().exec(command);

            // 读取标准输出
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                int exitCode = process.waitFor(); // 等待命令执行结束
                if (exitCode != 0) {
                    // 读取错误流
                    try (BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {
                        StringBuilder errorOutput = new StringBuilder();
                        while ((line = errorReader.readLine()) != null) {
                            errorOutput.append(line).append("\n");
                        }
                        throw new RuntimeException("Command failed with exit code " + exitCode +
                                "\nError output:\n" + errorOutput.toString());
                    }
                }
                return output.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute command: " + command, e);
        }
    }

    // 测试
    public static void main(String[] args) {
        try {
            String output = exec("adb devices -l");
            System.out.println(output);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
