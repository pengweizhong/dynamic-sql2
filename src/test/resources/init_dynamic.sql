-- 创建 Users 表
CREATE TABLE users
(
    user_id           INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',              -- 用户 ID
    name              VARCHAR(100) NOT NULL COMMENT '用户姓名',                      -- 用户姓名
    gender            ENUM('Male', 'Female', 'Other') NOT NULL COMMENT '性别',       -- 枚举类型：性别
    registration_date DATE         NOT NULL COMMENT '注册日期',                      -- 注册日期
    email             VARCHAR(150) COMMENT '邮箱',                                   -- 邮箱
    phone_number      VARCHAR(15) COMMENT '电话号码',                                -- 电话号码
    account_balance   DECIMAL(10, 2) DEFAULT 0.00 COMMENT '用户账户余额',            -- 用户账户余额
    details           JSON COMMENT '用户详细信息（JSON 存储地址、兴趣等）',             -- 额外信息（JSON）
    status            ENUM('Active', 'Inactive') DEFAULT 'Active' COMMENT '用户状态' -- 用户状态
) COMMENT = '用户表';

-- 创建 Categories 表
CREATE TABLE categories
(
    category_id   INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类 ID', -- 分类 ID
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',         -- 分类名称
    description   TEXT COMMENT '分类描述'                           -- 分类描述
) COMMENT = '分类表';

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

-- 创建 Orders 表
CREATE TABLE orders
(
    order_id         INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单 ID',                                   -- 订单 ID
    user_id          INT COMMENT '外键，关联 Users 表',                                                   -- 外键，关联 Users 表
    order_date       DATE           NOT NULL COMMENT '订单日期',                                         -- 订单日期
    total_amount     DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',                                       -- 订单总金额
    payment_method   ENUM('Credit Card', 'PayPal', 'Bank Transfer', 'Cash') NOT NULL COMMENT '支付方式', -- 支付方式
    payment_status   ENUM('Pending', 'Completed', 'Failed', 'Refunded') NOT NULL COMMENT '支付状态',     -- 支付状态
    shipping_address JSON COMMENT '发货地址（JSON 存储）',                                                 -- 发货地址（JSON）
    status           ENUM('Pending', 'Shipped', 'Delivered', 'Cancelled') NOT NULL COMMENT '订单状态',   -- 订单状态
    order_details    JSON COMMENT '订单详情（JSON 存储购买的产品、数量等）',                                -- 订单详情（JSON）
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)                               -- 外键约束
) COMMENT = '订单表';