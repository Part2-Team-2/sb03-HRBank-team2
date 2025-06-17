DROP TABLE IF EXISTS change_log_diff CASCADE;
DROP TABLE IF EXISTS employee_change_log CASCADE;
DROP TABLE IF EXISTS backup CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS binary_content CASCADE;
DROP TABLE IF EXISTS department CASCADE;

-- ========================
-- DEPARTMENT TABLE
-- ========================
CREATE TABLE department
(
    id               SERIAL PRIMARY KEY,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ,
    name             VARCHAR(100) NOT NULL,
    description      VARCHAR(500) NOT NULL,
    established_date DATE         NOT NULL,
    CONSTRAINT uk_department_name UNIQUE (name)
);


-- ========================
-- BINARY_CONTENT TABLE
-- ========================
CREATE TABLE binary_content
(
    id           SERIAL PRIMARY KEY,
    created_at   TIMESTAMPTZ  NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size         BIGINT       NOT NULL
);


-- ========================
-- EMPLOYEE TABLE
-- ========================
CREATE TABLE employee
(
    id            SERIAL PRIMARY KEY,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ,
    emp_no        VARCHAR(100) NOT NULL,
    name          VARCHAR(10)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    position      VARCHAR(50)  NOT NULL,
    hire_date     DATE         NOT NULL,
    memo          VARCHAR(500),
    status        VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'RESIGNED')),
    department_id BIGINT       NOT NULL,
    profile_id    BIGINT,
    CONSTRAINT uk_emp_no UNIQUE (emp_no),
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES department (id) ON DELETE CASCADE,
    CONSTRAINT fk_profile FOREIGN KEY (profile_id) REFERENCES binary_content (id) ON DELETE SET NULL
);


-- ========================
-- BACKUP TABLE
-- ========================
CREATE TABLE backup
(
    id                SERIAL PRIMARY KEY,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,
    started_at_from   TIMESTAMPTZ  NOT NULL,
    started_at_to     TIMESTAMPTZ  NOT NULL,
    employee_ip       VARCHAR(45) NOT NULL,
    backup_status     VARCHAR(15)  NOT NULL CHECK (backup_status IN ('IN_PROGRESS', 'COMPLETED', 'SKIPPED', 'FAILED')),
    binary_content_id BIGINT,
    CONSTRAINT fk_backup_binary FOREIGN KEY (binary_content_id) REFERENCES binary_content (id) ON DELETE SET NULL
);


-- ========================
-- EMPLOYEE_CHANGE_LOG TABLE
-- ========================
CREATE TABLE employee_change_log
(
    id              SERIAL PRIMARY KEY,
    employee_number VARCHAR(100) NOT NULL,
    type            VARCHAR(10)  NOT NULL CHECK (type IN ('CREATED', 'UPDATED', 'DELETED')),
    memo            TEXT,
    ip_address      VARCHAR(45)  NOT NULL,
    at              TIMESTAMPTZ  NOT NULL
);


-- ========================
-- CHANGE_LOG_DIFF TABLE
-- ========================
CREATE TABLE change_log_diff
(
    id            SERIAL PRIMARY KEY,
    property_name VARCHAR(50) NOT NULL,
    before_value  VARCHAR(255),
    after_value   VARCHAR(255),
    change_log_id BIGINT      NOT NULL,
    CONSTRAINT fk_change_log_id FOREIGN KEY (change_log_id) REFERENCES employee_change_log (id) ON DELETE CASCADE
);