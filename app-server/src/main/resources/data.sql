-- 清空表数据
DELETE FROM transaction;
DELETE FROM screenshot;
DELETE FROM statement;
DELETE FROM test_record;
DELETE FROM test_case;
DELETE FROM device;

-- 插入示例设备数据
INSERT INTO device (name, type, uuid, status, create_time, update_time) VALUES 
('iPhone 12', 'mobile', 'uuid-1234', 'available', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Samsung Galaxy S21', 'mobile', 'uuid-5678', 'available', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入示例测试用例数据
INSERT INTO test_case (title, dsl, create_time, update_time) VALUES 
('Login Test', '# Login test scenario
navigate(''https://example.com/login'')
input(''username'', ''testuser'')
input(''password'', ''password123'')
click(''loginButton'')', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('Search Test', '# Search test scenario
navigate(''https://example.com'')
input(''searchBox'', ''test query'')
click(''searchButton'')', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入示例测试记录数据
INSERT INTO test_record (test_case_id, test_case_name, status, start_time, end_time, duration, create_time, update_time) VALUES 
(1, 'Login Test', 'PASSED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 5000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'Search Test', 'PASSED', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), 3000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入示例语句执行结果数据
INSERT INTO statement (test_record_id, statement, screenshot_id, status, duration, start_time, end_time, create_time, update_time) VALUES 
(1, 'navigate(''https://example.com/login'')', NULL, 'PASSED', 1000, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(1, 'input(''username'', ''testuser'')', NULL, 'PASSED', 500, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'navigate(''https://example.com'')', NULL, 'PASSED', 800, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入示例截图数据
INSERT INTO screenshot (file_path, file_name, description, create_time, update_time) VALUES 
('/screenshots/login_page.png', 'login_page.png', 'Login page screenshot', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('/screenshots/dashboard.png', 'dashboard.png', 'Dashboard screenshot', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- 插入示例网络事务数据
INSERT INTO transaction (url, method, headers, content_type, request, response, duration, create_time, update_time) VALUES 
('https://api.example.com/login', 'POST', 'Content-Type: application/json', 'application/json', '{"username":"testuser","password":"password123"}', '{"token":"abc123","expiresIn":3600}', 1200, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('https://api.example.com/user', 'GET', 'Authorization: Bearer abc123', 'application/json', '', '{"id":1,"name":"testuser"}', 800, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());