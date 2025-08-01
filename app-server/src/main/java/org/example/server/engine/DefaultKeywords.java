package org.example.server.engine;

import lombok.extern.slf4j.Slf4j;

/**
 * 示例DSL关键字实现
 */
@Slf4j
public class DefaultKeywords {
    
    /**
     * navigate关键字 - 模拟导航到指定URL
     */
    public static class NavigateKeyword implements Keyword {
        @Override
        public String getName() {
            return "navigate";
        }

        @Override
        public Object execute(Context context, Object... args) {
            if (args.length == 0) {
                throw new IllegalArgumentException("navigate keyword requires URL argument");
            }
            String url = args[0].toString();
            log.info("Navigating to: {}", url);
            context.setVariable("currentUrl", url);
            return "Navigated to " + url;
        }
    }

    /**
     * click关键字 - 模拟点击元素
     */
    public static class ClickKeyword implements Keyword {
        @Override
        public String getName() {
            return "click";
        }

        @Override
        public Object execute(Context context, Object... args) {
            if (args.length == 0) {
                throw new IllegalArgumentException("click keyword requires element identifier");
            }
            String element = args[0].toString();
            log.info("Clicking element: {}", element);
            context.setVariable("lastClicked", element);
            return "Clicked " + element;
        }
    }

    /**
     * input关键字 - 模拟在元素中输入文本
     */
    public static class InputKeyword implements Keyword {
        @Override
        public String getName() {
            return "input";
        }

        @Override
        public Object execute(Context context, Object... args) {
            if (args.length < 2) {
                throw new IllegalArgumentException("input keyword requires element identifier and text");
            }
            String element = args[0].toString();
            String text = args[1].toString();
            log.info("Inputting '{}' into element: {}", text, element);
            context.setVariable("input." + element, text);
            return "Input '" + text + "' into " + element;
        }
    }

    /**
     * assert关键字 - 模拟断言
     */
    public static class AssertKeyword implements Keyword {
        @Override
        public String getName() {
            return "assert";
        }

        @Override
        public Object execute(Context context, Object... args) {
            if (args.length < 2) {
                throw new IllegalArgumentException("assert keyword requires actual and expected values");
            }
            Object actual = args[0];
            Object expected = args[1];
            
            if (actual == null ? expected == null : actual.equals(expected)) {
                log.info("Assertion passed: {} == {}", actual, expected);
                return true;
            } else {
                log.info("Assertion failed: {} != {}", actual, expected);
                throw new AssertionError("Assertion failed: expected " + expected + " but got " + actual);
            }
        }
    }

    /**
     * wait关键字 - 模拟等待
     */
    public static class WaitKeyword implements Keyword {
        @Override
        public String getName() {
            return "wait";
        }

        @Override
        public Object execute(Context context, Object... args) {
            long milliseconds = 1000; // 默认等待1秒
            if (args.length > 0) {
                try {
                    milliseconds = Long.parseLong(args[0].toString());
                } catch (NumberFormatException e) {
                    // 使用默认值
                }
            }
            
            try {
                log.info("Waiting for {} milliseconds", milliseconds);
                Thread.sleep(milliseconds);
                return "Waited for " + milliseconds + " milliseconds";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Wait interrupted";
            }
        }
    }
    
    /**
     * screenshot关键字 - 模拟截图
     */
    public static class ScreenshotKeyword implements Keyword {
        @Override
        public String getName() {
            return "screenshot";
        }

        @Override
        public Object execute(Context context, Object... args) {
            String name = "screenshot";
            if (args.length > 0) {
                name = args[0].toString();
            }
            log.info("Taking screenshot: {}", name);
            context.setVariable("lastScreenshot", name);
            return "Screenshot taken: " + name;
        }
    }
    
    /**
     * log关键字 - 输出日志
     */
    public static class LogKeyword implements Keyword {
        @Override
        public String getName() {
            return "log";
        }

        @Override
        public Object execute(Context context, Object... args) {
            if (args.length > 0) {
                log.info("DSL Log: {}", args[0]);
                return "Logged: " + args[0];
            }
            return "Empty log";
        }
    }
}