CREATE TABLE USERS
(
    USER_ID           NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1 PRIMARY KEY, -- 自增列
    NAME              VARCHAR2(100)                    NOT NULL,                                   -- 用户姓名
    GENDER            VARCHAR2(10)                     NOT NULL,                                   -- 性别（替代 ENUM 类型）
    REGISTRATION_DATE DATE NOT NULL,                                                               -- 注册日期
    EMAIL             VARCHAR2(150),                                                               -- 邮箱
    PHONE_NUMBER      VARCHAR2(15),                                                                -- 电话号码
    ACCOUNT_BALANCE   NUMBER(10, 2)                    DEFAULT 0.00,                               -- 用户账户余额
    DETAILS           CLOB DEFAULT NULL,                                                           -- 用户详细信息（可存储 JSON）
    STATUS            VARCHAR2(10) DEFAULT 'ACTIVE'                                                -- 用户状态（Active/Inactive）
);

-- 添加表注释
COMMENT ON TABLE USERS IS '用户表';
-- 添加列注释
COMMENT ON COLUMN USERS.NAME IS '用户姓名';
COMMENT ON COLUMN USERS.GENDER IS '性别';
COMMENT ON COLUMN USERS.REGISTRATION_DATE IS '注册日期';
COMMENT ON COLUMN USERS.EMAIL IS '邮箱';
COMMENT ON COLUMN USERS.PHONE_NUMBER IS '电话号码';
COMMENT ON COLUMN USERS.ACCOUNT_BALANCE IS '用户账户余额';
COMMENT ON COLUMN USERS.DETAILS IS '用户详细信息（JSON 存储地址、兴趣等）';
COMMENT ON COLUMN USERS.STATUS IS '用户状态';

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Alice Johnson', 'Female', TO_DATE('2023-05-10', 'YYYY-MM-DD'), 'alice@example.com', '1234567890', 150.00,
        '{"address": "123 Main St", "interests": ["reading", "traveling"]}', 'Active');

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Bob Smith', 'Male', TO_DATE('2022-11-22', 'YYYY-MM-DD'), 'bob@example.com', '0987654321', 75.50,
        '{"address": "456 Elm St", "interests": ["sports", "music"]}', 'Inactive');

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Charlie Davis', 'Male', TO_DATE('2021-01-15', 'YYYY-MM-DD'), 'charlie@example.com', '1231231234', 500.00,
        '{"address": "789 Oak St", "interests": ["coding", "gaming"]}', 'Active');

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Dana Lee', 'Female', TO_DATE('2020-09-08', 'YYYY-MM-DD'), 'dana@example.com', '3213214321', 0.00,
        '{"address": "321 Pine St", "interests": ["photography", "art"]}', 'Active');

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Eve Williams', 'Other', TO_DATE('2024-02-28', 'YYYY-MM-DD'), 'eve@example.com', '5556667777', 200.75,
        '{"address": "654 Birch St", "interests": ["fitness", "yoga"]}', 'Active');

INSERT INTO dynamic_sql2.users
(name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES ('Jerry', 'Other', TO_DATE('2024-02-01', 'YYYY-MM-DD'), 'jerry@example.com', '111222333', 1290.00, '{}', 'Active');