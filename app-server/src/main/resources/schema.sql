e的r-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS screenshot;
DROP TABLE IF EXISTS statement;
DROP TABLE IF EXISTS test_record;
DROP TABLE IF EXISTS test_case;
DROP TABLE IF EXISTS device;

-- 创建device表
CREATE TABLE device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(255),
    uuid VARCHAR(255),
    status VARCHAR(50),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 创建test_case表
CREATE TABLE test_case (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    dsl TEXT,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 创建test_record表
CREATE TABLE test_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_case_id BIGINT,
    test_case_name VARCHAR(255),
    status VARCHAR(50), -- PASSED, FAILED, RUNNING, PENDING
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration BIGINT,
    error_message TEXT,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 创建statement表
CREATE TABLE statement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_record_id BIGINT,
    statement TEXT,
    screenshot_id BIGINT,
    status VARCHAR(50), -- PASSED, FAILED, SKIPPED
    duration BIGINT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 创建screenshot表
CREATE TABLE screenshot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_path VARCHAR(500),
    file_name VARCHAR(255),
    description VARCHAR(500),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

-- 创建transaction表
CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500),
    method VARCHAR(10),
    headers TEXT,
    content_type VARCHAR(100),
    request TEXT,
    response TEXT,
    duration BIGINT,
    create_time TIMESTAMP,
    update_time TIMESTAMP
);