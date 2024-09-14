-- 1. 创建 teacher 表并添加注释
CREATE TABLE teacher (
                         teacher_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '教师唯一ID',
                         first_name VARCHAR(50) NOT NULL COMMENT '教师名字',
                         last_name VARCHAR(50) NOT NULL COMMENT '教师姓氏',
                         gender VARCHAR(10) NOT NULL COMMENT '教师性别',
                         birth_date DATE COMMENT '教师出生日期',
                         hire_date DATE NOT NULL COMMENT '教师聘用日期',
                         department VARCHAR(50) COMMENT '教师所在的部门'
) COMMENT='教师信息表';

-- 插入 teacher 表的测试数据
INSERT INTO teacher (first_name, last_name, gender, birth_date, hire_date, department)
VALUES
    ('John', 'Doe', 'Male', '1980-05-20', '2005-09-01', 'Mathematics'),
    ('Jane', 'Smith', 'Female', '1985-02-14', '2010-08-15', 'Science'),
    ('Emily', 'Johnson', 'Female', '1990-12-05', '2015-01-12', 'Languages'),
    ('Michael', 'Williams', 'Male', '1978-11-25', '2003-06-23', 'History'),
    ('Sarah', 'Brown', 'Female', '1987-09-10', '2011-03-30', 'Physical Education');

-- 2. 创建 class 表并添加注释
CREATE TABLE class (
                       class_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '班级唯一ID',
                       class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
                       grade_level INT NOT NULL COMMENT '年级',
                       class_teacher_id INT NOT NULL COMMENT '班主任ID',
                       FOREIGN KEY (class_teacher_id) REFERENCES teacher(teacher_id) ON DELETE CASCADE
) COMMENT='班级信息表';

-- 插入 class 表的测试数据
INSERT INTO class (class_name, grade_level, class_teacher_id)
VALUES
    ('Class 1A', 10, 1),
    ('Class 2B', 11, 2),
    ('Class 3C', 12, 3),
    ('Class 4D', 9, 4),
    ('Class 5E', 8, 5);

-- 3. 创建 student 表并添加注释
CREATE TABLE student (
                         student_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '学生唯一ID',
                         first_name VARCHAR(50) NOT NULL COMMENT '学生名字',
                         last_name VARCHAR(50) NOT NULL COMMENT '学生姓氏',
                         gender VARCHAR(10) NOT NULL COMMENT '学生性别',
                         birth_date DATE NOT NULL COMMENT '学生出生日期',
                         enrollment_date DATE NOT NULL COMMENT '入学日期',
                         class_id INT NOT NULL COMMENT '班级ID',
                         FOREIGN KEY (class_id) REFERENCES class(class_id) ON DELETE CASCADE
) COMMENT='学生信息表';

-- 插入 student 表的测试数据
INSERT INTO student (first_name, last_name, gender, birth_date, enrollment_date, class_id)
VALUES
    ('Alice', 'Walker', 'Female', '2006-03-12', '2021-09-01', 1),
    ('Bob', 'Jones', 'Male', '2005-07-22', '2020-09-01', 1),
    ('Charlie', 'Miller', 'Male', '2004-11-02', '2019-09-01', 2),
    ('Daisy', 'Wilson', 'Female', '2003-09-16', '2018-09-01', 3),
    ('Ethan', 'Davis', 'Male', '2007-01-29', '2021-09-01', 4),
    ('Fiona', 'Garcia', 'Female', '2006-04-15', '2021-09-01', 5);

-- 4. 创建 subject 表并添加注释
CREATE TABLE subject (
                         subject_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '科目唯一ID',
                         subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
                         subject_code VARCHAR(10) NOT NULL COMMENT '科目代码',
                         teacher_id INT NOT NULL COMMENT '任课教师ID',
                         FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id) ON DELETE CASCADE
) COMMENT='科目信息表';

-- 插入 subject 表的测试数据
INSERT INTO subject (subject_name, subject_code, teacher_id)
VALUES
    ('Mathematics', 'MATH101', 1),
    ('Physics', 'PHYS201', 2),
    ('English', 'ENG301', 3),
    ('History', 'HIST401', 4),
    ('Physical Education', 'PE501', 5);

-- 5. 创建 exam 表并添加注释
CREATE TABLE exam (
                      exam_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '考试唯一ID',
                      exam_name VARCHAR(50) NOT NULL COMMENT '考试名称',
                      exam_date DATE NOT NULL COMMENT '考试日期',
                      description TEXT COMMENT '考试描述'
) COMMENT='考试信息表';

-- 插入 exam 表的测试数据
INSERT INTO exam (exam_name, exam_date, description)
VALUES
    ('Midterm Exam', '2023-06-15', 'Midterm exams for all students'),
    ('Final Exam', '2023-12-20', 'Final exams for all students');

-- 6. 创建 exam_score 表并添加注释
CREATE TABLE exam_score (
                            score_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩唯一ID',
                            student_id INT NOT NULL COMMENT '学生ID',
                            exam_id INT NOT NULL COMMENT '考试ID',
                            subject_id INT NOT NULL COMMENT '科目ID',
                            score DECIMAL(5, 2) NOT NULL COMMENT '考试成绩',
                            FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
                            FOREIGN KEY (exam_id) REFERENCES exam(exam_id) ON DELETE CASCADE,
                            FOREIGN KEY (subject_id) REFERENCES subject(subject_id) ON DELETE CASCADE
) COMMENT='学生考试成绩表';

-- 插入 exam_score 表的测试数据
INSERT INTO exam_score (student_id, exam_id, subject_id, score)
VALUES
    (1, 1, 1, 85.5),  -- Alice 的数学期中考试成绩
    (1, 1, 2, 78.0),  -- Alice 的物理期中考试成绩
    (2, 1, 1, 90.0),  -- Bob 的数学期中考试成绩
    (3, 1, 3, 88.5),  -- Charlie 的英语期中考试成绩
    (4, 1, 4, 92.0),  -- Daisy 的历史期中考试成绩
    (5, 1, 5, 75.0),  -- Ethan 的体育期中考试成绩
    (6, 1, 5, 80.0),  -- Fiona 的体育期中考试成绩
    (1, 2, 1, 87.0),  -- Alice 的数学期末考试成绩
    (1, 2, 2, 82.5),  -- Alice 的物理期末考试成绩
    (2, 2, 1, 92.0),  -- Bob 的数学期末考试成绩
    (3, 2, 3, 85.0),  -- Charlie 的英语期末考试成绩
    (4, 2, 4, 94.0),  -- Daisy 的历史期末考试成绩
    (5, 2, 5, 78.0),  -- Ethan 的体育期末考试成绩
    (6, 2, 5, 83.5);  -- Fiona 的体育期末考试成绩

-- 7. 创建 class_subject 表并添加注释
CREATE TABLE class_subject (
                               class_id INT NOT NULL COMMENT '班级ID',
                               subject_id INT NOT NULL COMMENT '科目ID',
                               PRIMARY KEY (class_id, subject_id),
                               FOREIGN KEY (class_id) REFERENCES class(class_id) ON DELETE CASCADE,
                               FOREIGN KEY (subject_id) REFERENCES subject(subject_id) ON DELETE CASCADE
) COMMENT='班级科目关联表';

-- 插入 class_subject 表的测试数据
INSERT INTO class_subject (class_id, subject_id)
VALUES
    (1, 1),  -- Class 1A 学习数学
    (1, 2),  -- Class 1A 学习物理
    (2, 3),  -- Class 2B 学习英语
    (3, 4),  -- Class 3C 学习历史
    (4, 5),  -- Class 4D 学习体育
    (5, 5);  -- Class 5E 学习体育