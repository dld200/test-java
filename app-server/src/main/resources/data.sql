-- 插入示例device数据
INSERT INTO device (name, type, uuid, status) VALUES
('iPhone 12', 'iOS', 'iPhone12-001', 'available'),
('Samsung Galaxy S21', 'Android', 'GalaxyS21-001', 'available'),
('iPad Pro', 'iOS', 'iPadPro-001', 'maintenance');

-- 插入示例app数据
INSERT INTO app (name, package_name, version, description) VALUES
('Twitter', 'com.twitter.android', '8.8.0', 'Twitter for Android'),
('WeChat', 'com.tencent.mm', '8.0.20', 'WeChat - Instant messaging app'),
('TikTok', 'com.zhiliaoapp.musically', '19.9.4', 'TikTok - Short video app');

-- 插入示例transaction数据
INSERT INTO transaction (device_id, app_id, type, status, start_time, end_time) VALUES
(1, 1, 'test', 'completed', '2023-01-01 10:00:00', '2023-01-01 10:05:00'),
(2, 2, 'test', 'completed', '2023-01-01 11:00:00', '2023-01-01 11:10:00'),
(1, 3, 'test', 'failed', '2023-01-01 12:00:00', '2023-01-01 12:03:00');

-- 插入示例statement数据
INSERT INTO statement (transaction_id, type, content) VALUES
(1, 'click', 'Click on login button'),
(1, 'input', 'Input username'),
(2, 'click', 'Click on send message button'),
(2, 'input', 'Input message content'),
(3, 'swipe', 'Swipe up on feed');

-- 插入示例screenshot数据
INSERT INTO screenshot (statement_id, file_path) VALUES
(1, '/screenshots/1.png'),
(2, '/screenshots/2.png'),
(3, '/screenshots/3.png'),
(4, '/screenshots/4.png'),
(5, '/screenshots/5.png');

-- 插入示例test_case数据
INSERT INTO test_case (app_id, name, description) VALUES
(1, 'Login Test', 'Test the login functionality'),
(2, 'Send Message Test', 'Test sending a message'),
(3, 'Video Playback Test', 'Test video playback functionality');

-- 插入示例test_result数据
INSERT INTO test_result (test_case_id, device_id, status, start_time, end_time, log) VALUES
(1, 1, 'passed', '2023-01-01 10:00:00', '2023-01-01 10:05:00', 'Login successful'),
(2, 2, 'passed', '2023-01-01 11:00:00', '2023-01-01 11:10:00', 'Message sent successfully'),
(3, 1, 'failed', '2023-01-01 12:00:00', '2023-01-01 12:03:00', 'Video playback failed due to network error');