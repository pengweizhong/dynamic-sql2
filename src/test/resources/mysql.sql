-- 创建 Users 表
CREATE TABLE users
(
    user_id           INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',                   -- 用户 ID
    name              VARCHAR(100)                     NOT NULL COMMENT '用户姓名',       -- 用户姓名
    gender            ENUM ('Male', 'Female', 'Other') NOT NULL COMMENT '性别（枚举类型）', -- 枚举类型：性别
    sex               tinyint(1)                       NULL COMMENT '性别（数字类型）',     -- 数字类型：性别
    registration_date DATE                             NOT NULL COMMENT '注册日期',       -- 注册日期
    email             VARCHAR(150) COMMENT '邮箱',                                        -- 邮箱
    phone_number      VARCHAR(15) COMMENT '电话号码',                                     -- 电话号码
    account_balance   DECIMAL(10, 2)              DEFAULT 0.00 COMMENT '用户账户余额',    -- 用户账户余额
    details           JSON COMMENT '用户详细信息（JSON 存储地址、兴趣等）',                  -- 额外信息（JSON）
    status            ENUM ('Active', 'Inactive') DEFAULT 'Active' COMMENT '用户状态'     -- 用户状态
) COMMENT = '用户表';

INSERT INTO dynamic_sql2.users
(user_id, name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES (1, 'Alice Johnson', 'Female', '2023-05-10', 'alice@example.com', '1234567890', 150.00,
        '{"address": "123 Main St", "interests": ["reading", "traveling"]}', 'Active');
INSERT INTO dynamic_sql2.users
(user_id, name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES (2, 'Bob Smith', 'Male', '2022-11-22', 'bob@example.com', '0987654321', 75.50,
        '{"address": "456 Elm St", "interests": ["sports", "music"]}', 'Inactive');
INSERT INTO dynamic_sql2.users
(user_id, name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES (3, 'Charlie Davis', 'Male', '2021-01-15', 'charlie@example.com', '1231231234', 500.00,
        '{"address": "789 Oak St", "interests": ["coding", "gaming"]}', 'Active');
INSERT INTO dynamic_sql2.users
(user_id, name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES (4, 'Dana Lee', 'Female', '2020-09-08', 'dana@example.com', '3213214321', 0.00,
        '{"address": "321 Pine St", "interests": ["photography", "art"]}', 'Active');
INSERT INTO dynamic_sql2.users
(user_id, name, gender, registration_date, email, phone_number, account_balance, details, status)
VALUES (5, 'Eve Williams', 'Other', '2024-02-28', 'eve@example.com', '5556667777', 200.75,
        '{"address": "654 Birch St", "interests": ["fitness", "yoga"]}', 'Active');
INSERT INTO dynamic_sql2.users (user_id, name, gender, registration_date, email, phone_number, account_balance, details,
                                status)
VALUES (6, 'Jerry', 'Other', '2024-02-01', 'jerry@example.com', '111222333', 1290.00, '{}', 'Active');

-- 创建 Categories 表
CREATE TABLE categories
(
    category_id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类 ID', -- 分类 ID
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',         -- 分类名称
    description   TEXT COMMENT '分类描述'                           -- 分类描述
) COMMENT = '分类表';

INSERT INTO dynamic_sql2.categories
    (category_id, category_name, description)
VALUES (1, 'Electronics', 'Devices and gadgets including smartphones, laptops, and accessories');
INSERT INTO dynamic_sql2.categories
    (category_id, category_name, description)
VALUES (2, 'Books', 'A wide variety of books including fiction, non-fiction, and textbooks');
INSERT INTO dynamic_sql2.categories
    (category_id, category_name, description)
VALUES (3, 'Clothing', 'Men and women apparel including shirts, pants, and accessories');
INSERT INTO dynamic_sql2.categories
    (category_id, category_name, description)
VALUES (4, 'Home & Kitchen', 'Home appliances, kitchen tools, and furniture');
INSERT INTO dynamic_sql2.categories
    (category_id, category_name, description)
VALUES (5, 'Sports', 'Sports equipment, apparel, and accessories for various activities');

-- 创建 Products 表
CREATE TABLE products
(
    product_id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '产品 ID',                          -- 产品 ID
    product_name VARCHAR(150)   NOT NULL COMMENT '产品名称',                                -- 产品名称
    price        DECIMAL(10, 2) NOT NULL COMMENT '产品价格',                                -- 产品价格
    stock        INT            NOT NULL COMMENT '产品库存',                                -- 产品库存
    category_id  INT COMMENT '外键，关联 Categories 表',                                     -- 外键，关联 Categories 表
    attributes   JSON COMMENT '产品属性（JSON 存储颜色、尺寸等）',                             -- 产品属性（JSON）
    created_at   DATE           NOT NULL COMMENT '产品创建日期',                            -- 产品创建日期
    is_available BOOLEAN DEFAULT TRUE COMMENT '是否上架',                                   -- 是否上架
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (category_id) -- 外键约束
) COMMENT = '产品表';

INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (1, 'iPhone 14', 999.99, 50, 1, '{"color": "black", "storage": "128GB"}', '2023-01-15', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (2, 'MacBook Pro', 1999.99, 30, 1, '{"color": "silver", "storage": "256GB"}', '2023-03-10', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (3, 'Harry Potter and the Philosopher''s Stone', 12.99, 200, 2,
        '{"author": "J.K. Rowling", "format": "hardcover"}',
        '2022-09-05', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (4, 'Nike Running Shoes', 85.50, 100, 3, '{"size": "10", "color": "blue"}', '2023-06-01', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (5, 'Coffee Maker', 49.99, 150, 4, '{"type": "drip", "brand": "Breville"}', '2023-04-18', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (6, 'iPhone 14', 999.99, 50, 1, '{"color": "black", "storage": "128GB"}', '2023-01-15', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (7, 'MacBook Pro', 1999.99, 30, 1, '{"color": "silver", "storage": "256GB"}', '2023-03-10', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (8, 'Harry Potter and the Philosopher''s Stone', 12.99, 200, 2,
        '{"author": "J.K. Rowling", "format": "hardcover"}',
        '2022-09-05', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (9, 'Nike Running Shoes', 85.50, 100, 3, '{"size": "10", "color": "blue"}', '2023-06-01', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (10, 'Coffee Maker', 49.99, 150, 4, '{"type": "drip", "brand": "Breville"}', '2023-04-18', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (11, 'iPhone 14', 999.99, 50, 1, '{"color": "black", "storage": "128GB"}', '2023-01-15', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (12, 'MacBook Pro', 1999.99, 30, 1, '{"color": "silver", "storage": "256GB"}', '2023-03-10', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (13, 'Harry Potter and the Philosopher''s Stone', 12.99, 200, 2,
        '{"author": "J.K. Rowling", "format": "hardcover"}',
        '2022-09-05', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (14, 'Nike Running Shoes', 85.50, 100, 3, '{"size": "10", "color": "blue"}', '2023-06-01', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (15, 'Coffee Maker', 49.99, 150, 4, '{"type": "drip", "brand": "Breville"}', '2023-04-18', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (16, 'iPhone 14', 999.99, 50, 1, '{"color": "black", "storage": "128GB"}', '2023-01-15', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (17, 'MacBook Pro', 1999.99, 30, 1, '{"color": "silver", "storage": "256GB"}', '2023-03-10', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (18, 'Harry Potter and the Philosopher''s Stone', 12.99, 200, 2,
        '{"author": "J.K. Rowling", "format": "hardcover"}',
        '2022-09-05', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (19, 'Nike Running Shoes', 85.50, 100, 3, '{"size": "10", "color": "blue"}', '2023-06-01', 1);
INSERT INTO dynamic_sql2.products
(product_id, product_name, price, stock, category_id, `attributes`, created_at, is_available)
VALUES (20, 'Coffee Maker', 49.99, 150, 4, '{"type": "drip", "brand": "Breville"}', '2023-04-18', 1);

-- 创建 Orders 表
CREATE TABLE orders
(
    order_id         INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单 ID',                                      -- 订单 ID
    user_id          INT COMMENT '外键，关联 Users 表',                                                      -- 外键，关联 Users 表
    order_date       DATE                                                    NOT NULL COMMENT '订单日期',   -- 订单日期
    total_amount     DECIMAL(10, 2)                                          NOT NULL COMMENT '订单总金额', -- 订单总金额
    payment_method   ENUM ('Credit Card', 'PayPal', 'Bank Transfer', 'Cash') NOT NULL COMMENT '支付方式',   -- 支付方式
    payment_status   ENUM ('Pending', 'Completed', 'Failed', 'Refunded')     NOT NULL COMMENT '支付状态',   -- 支付状态
    shipping_address JSON COMMENT '发货地址（JSON 存储）',                                                    -- 发货地址（JSON）
    status           ENUM ('Pending', 'Shipped', 'Delivered', 'Cancelled')   NOT NULL COMMENT '订单状态',   -- 订单状态
    order_details    JSON COMMENT '订单详情（JSON 存储购买的产品、数量等）',                                   -- 订单详情（JSON）
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)                                  -- 外键约束
) COMMENT = '订单表';

INSERT INTO dynamic_sql2.orders
(order_id, user_id, order_date, total_amount, payment_method, payment_status, shipping_address, status, order_details)
VALUES (1, 1, '2024-03-10', 1150.99, 'Credit Card', 'Completed',
        '{"zip": "10001", "city": "New York", "address": "123 Main St"}',
        'Delivered', '{"items": [{"product": "iPhone 14", "quantity": 1}, {"product": "MacBook Pro", "quantity": 1}]}');
INSERT INTO dynamic_sql2.orders
(order_id, user_id, order_date, total_amount, payment_method, payment_status, shipping_address, status, order_details)
VALUES (2, 2, '2023-12-01', 12.99, 'PayPal', 'Completed',
        '{"zip": "90001", "city": "Los Angeles", "address": "456 Elm St"}',
        'Shipped', '{"items": [{"product": "Harry Potter and the Philosopher''s Stone", "quantity": 1}]}');
INSERT INTO dynamic_sql2.orders
(order_id, user_id, order_date, total_amount, payment_method, payment_status, shipping_address, status, order_details)
VALUES (3, 3, '2024-01-15', 85.50, 'Bank Transfer', 'Pending',
        '{"zip": "60601", "city": "Chicago", "address": "789 Oak St"}',
        'Pending', '{"items": [{"product": "Nike Running Shoes", "quantity": 1}]}');
INSERT INTO dynamic_sql2.orders
(order_id, user_id, order_date, total_amount, payment_method, payment_status, shipping_address, status, order_details)
VALUES (4, 4, '2023-11-20', 99.99, 'Credit Card', 'Completed',
        '{"zip": "94101", "city": "San Francisco", "address": "321 Pine St"}', 'Cancelled',
        '{"items": [{"product": "Coffee Maker", "quantity": 2}]}');
INSERT INTO dynamic_sql2.orders
(order_id, user_id, order_date, total_amount, payment_method, payment_status, shipping_address, status, order_details)
VALUES (5, 5, '2024-02-28', 500.00, 'Cash', 'Completed', '{"zip": "33101", "city": "Miami", "address": "654 Birch St"}',
        'Delivered', '{"items": [{"product": "iPhone 14", "quantity": 1}]}');



CREATE TABLE temp_user
(
    id   INT PRIMARY KEY,
    name VARCHAR(20),
    age  INT
);

CREATE TABLE temp_dept
(
    id       INT PRIMARY KEY,
    name     VARCHAR(20),
    location VARCHAR(50)
);



CREATE TABLE `t_department`
(
    `id`           int                                                          NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `team_id`      int                                                          NOT NULL COMMENT '团队id',
    `dept_name`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
    `dept_desc`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '部门描述',
    `parent_id`    int                                                          NOT NULL DEFAULT '0' COMMENT '上级部门id，0表示顶级部门',
    `is_delete`    tinyint(1)                                                   NOT NULL DEFAULT '0' COMMENT '是否删除 0 未删除 1 已删除',
    `create_id`    int                                                          NOT NULL COMMENT '创建人id',
    `update_id`    int                                                          NOT NULL COMMENT '更新人id',
    `create_time`  timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp                                                    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `dept_tag`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci         DEFAULT NULL,
    `dept_ldap_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT 'ladp中的id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='部门表';