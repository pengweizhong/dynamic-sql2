-- Dynamic-SQL2 测试模型；MySQL 8.0.16+，在专用空测试库执行。
-- 不创建/切换数据库，不删除现有对象；重复建表会报错，重置请显式运行 00-reset.sql。
SET NAMES utf8mb4;
SET SESSION time_zone = '+00:00';

CREATE TABLE ds_department (
    id BIGINT PRIMARY KEY,
    parent_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_department_parent FOREIGN KEY (parent_id) REFERENCES ds_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NULL,
    login_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    version_no INT NOT NULL DEFAULT 0,
    deleted SMALLINT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT uq_account_login UNIQUE (login_name),
    CONSTRAINT ck_account_status CHECK (status IN ('ACTIVE','DISABLED')),
    CONSTRAINT fk_account_department FOREIGN KEY (department_id) REFERENCES ds_department(id),
    INDEX ix_account_department (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 主键同时是外键：一对零或一。
CREATE TABLE ds_account_profile (
    account_id BIGINT PRIMARY KEY,
    birthday DATE NULL,
    biography TEXT NULL,
    avatar BLOB NULL,
    CONSTRAINT fk_profile_account FOREIGN KEY (account_id) REFERENCES ds_account(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_role (
    id BIGINT PRIMARY KEY,
    role_code VARCHAR(40) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 复合主键用于测试数据库约束与关联；不预设框架已经支持复合主键 CRUD。
CREATE TABLE ds_account_role (
    account_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    granted_at DATETIME(6) NOT NULL,
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_ar_account FOREIGN KEY (account_id) REFERENCES ds_account(id),
    CONSTRAINT fk_ar_role FOREIGN KEY (role_id) REFERENCES ds_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_product (
    id BIGINT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(19,4) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    CONSTRAINT ck_product_price CHECK (price >= 0),
    CONSTRAINT ck_product_stock CHECK (stock >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_purchase_order (
    id BIGINT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    ordered_at DATETIME(6) NOT NULL,
    paid_at DATETIME(6) NULL,
    CONSTRAINT fk_order_account FOREIGN KEY (account_id) REFERENCES ds_account(id),
    CONSTRAINT ck_order_status CHECK (status IN ('NEW','PAID','CANCELLED')),
    INDEX ix_order_account_time (account_id, ordered_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_order_item (
    id BIGINT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    line_no INT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    discount_amount DECIMAL(19,4) NOT NULL DEFAULT 0,
    CONSTRAINT uq_item_line UNIQUE (order_id, line_no),
    CONSTRAINT ck_item_quantity CHECK (quantity > 0),
    CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES ds_purchase_order(id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES ds_product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 通用类型语义：移植时保持列名与样本 id，允许物理类型随数据库变化。
CREATE TABLE ds_type_sample (
    id BIGINT PRIMARY KEY,
    sample_name VARCHAR(40) NOT NULL UNIQUE,
    small_value SMALLINT NULL,
    int_value INT NULL,
    long_value BIGINT NULL,
    decimal_value DECIMAL(38,10) NULL,
    float_value FLOAT NULL,
    double_value DOUBLE NULL,
    flag_value SMALLINT NULL,
    char_value CHAR(8) NULL,
    varchar_value VARCHAR(512) NULL,
    text_value TEXT NULL,
    binary_value BINARY(8) NULL,
    varbinary_value VARBINARY(256) NULL,
    blob_value BLOB NULL,
    date_value DATE NULL,
    time_value TIME(6) NULL,
    datetime_value DATETIME(6) NULL,
    timestamp_value TIMESTAMP(6) NULL DEFAULT NULL,
    default_value VARCHAR(40) NOT NULL DEFAULT 'default-value',
    CONSTRAINT ck_type_flag CHECK (flag_value IN (0,1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- MySQL 专属类型：未来按 capability 单独启用，不强迫其他数据库逐列模拟。
CREATE TABLE ds_mysql_type_sample (
    id BIGINT PRIMARY KEY,
    tiny_value TINYINT NULL,
    tiny_boolean TINYINT(1) NULL,
    medium_value MEDIUMINT NULL,
    unsigned_tiny TINYINT UNSIGNED NULL,
    unsigned_small SMALLINT UNSIGNED NULL,
    unsigned_medium MEDIUMINT UNSIGNED NULL,
    unsigned_int INT UNSIGNED NULL,
    unsigned_big BIGINT UNSIGNED NULL,
    bit_flag BIT(1) NULL,
    bit_mask BIT(8) NULL,
    bit_wide BIT(64) NULL,
    year_value YEAR NULL,
    duration_value TIME(6) NULL,
    enum_value ENUM('NEW','ACTIVE','CLOSED') NULL,
    set_value SET('READ','WRITE','ADMIN') NULL,
    json_value JSON NULL,
    tiny_text TINYTEXT NULL,
    medium_text MEDIUMTEXT NULL,
    long_text LONGTEXT NULL,
    tiny_blob TINYBLOB NULL,
    medium_blob MEDIUMBLOB NULL,
    long_blob LONGBLOB NULL,
    base_amount DECIMAL(19,4) NULL,
    doubled_amount DECIMAL(20,4) GENERATED ALWAYS AS (base_amount * 2) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE ds_mysql_spatial_sample (
    id BIGINT PRIMARY KEY,
    point_value POINT NULL,
    line_value LINESTRING NULL,
    polygon_value POLYGON NULL,
    multi_point_value MULTIPOINT NULL,
    multi_line_value MULTILINESTRING NULL,
    multi_polygon_value MULTIPOLYGON NULL,
    geometry_value GEOMETRY NULL,
    collection_value GEOMETRYCOLLECTION NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
