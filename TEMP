

-- 窗口函数：返回每一行的结果，且不减少行数（不会像 GROUP BY 那样合并行），通常用于排名、累计和移动平均等操作。


-- 获取每个组内的行排名，比如 ROW_NUMBER() 或 RANK()。
SELECT
  ROW_NUMBER() OVER (PARTITION BY gender ORDER BY birth_date DESC) AS rank1,
  first_name ,
  gender ,
  birth_date
FROM student;

-- 学生按照班级排名
-- 	•	ROW_NUMBER()：对每个班级中的学生按照出生日期（降序）唯一排序。
-- 	•	RANK()：类似于 ROW_NUMBER()，但如果两名学生出生日期相同，会跳过排名。
-- 	•	DENSE_RANK()：与 RANK() 相似，但不会跳过排名。
SELECT
  student_id,
  first_name,
  class_id,
  birth_date,
  ROW_NUMBER() OVER (PARTITION BY class_id ORDER BY birth_date DESC) AS row_number_rank,
  RANK() OVER (PARTITION BY class_id ORDER BY birth_date DESC) AS rank1,
  DENSE_RANK() OVER (PARTITION BY class_id ORDER BY birth_date DESC) AS dense_rank1
FROM student;


-- 学生的考试成绩累计求和和平均值
-- 	•	SUM(score) OVER (PARTITION BY student_id)：为每个学生累计所有科目的成绩。
-- 	•	AVG(score) OVER (PARTITION BY student_id)：为每个学生计算科目的平均成绩。
SELECT
  s.student_id,
  first_name,
  subject_id,
  score,
  SUM(score) OVER (PARTITION BY student_id ORDER BY subject_id) AS cumulative_score,
  AVG(score) OVER (PARTITION BY student_id ORDER BY subject_id) AS average_score
FROM student s
JOIN exam_score es ON s.student_id = es.student_id;
-- 当不知道选择哪个列时：SQL 错误 [1052] [23000]: Column 'student_id' in field list is ambiguous


-- 班级内学生的滚动总成绩（累计求和）
-- 	ROWS BETWEEN 2 PRECEDING AND CURRENT ROW：计算当前行和之前两行的成绩之和，实现一个滚动窗口。
-- 通过 ROWS BETWEEN 窗口帧，计算班级内学生成绩的滚动累计分数。即考虑当前行及之前的几行，计算累计的成绩。
SELECT
  s.student_id,
  s.class_id,
  first_name,
  subject_id,
  score,
  SUM(score) OVER (PARTITION BY class_id ORDER BY score DESC ROWS BETWEEN 2 PRECEDING AND CURRENT ROW) AS rolling_sum
FROM student s
JOIN exam_score es ON s.student_id = es.student_id;


-- 学生在每科考试中的百分排名
-- PERCENT_RANK()：按成绩在每个科目中的百分比排名（0 到 1 之间）。
SELECT
  s.student_id,
  s.first_name,
  es.subject_id,
  es.score,
  PERCENT_RANK() OVER (PARTITION BY subject_id ORDER BY score DESC) AS percent_rank1
FROM exam_score es join student s on es.student_id =s.student_id ;

-- 学生每科成绩的差值（与前一名学生比较）
-- 	•	LAG()：获取前一名学生的成绩。
-- 	•	LEAD()：获取下一名学生的成绩。
-- 	•	score - LAG(score)：当前成绩与前一名学生成绩的差异。
SELECT
  s.student_id,
  s.first_name,
  es.subject_id,
  es.score,
  LAG(score) OVER (PARTITION BY subject_id ORDER BY score DESC) AS previous_score,
  LEAD(score) OVER (PARTITION BY subject_id ORDER BY score DESC) AS next_score,
  score - LAG(score) OVER (PARTITION BY subject_id ORDER BY score DESC) AS score_diff_with_previous
FROM exam_score es join student s on es.student_id =s.student_id ;

-- 班级内学生的成绩排名和分位数
-- NTILE(4)：将学生的成绩按四分位进行分组，每个组中人数相近。类似第XX梯队的感觉。
SELECT
  s.student_id,
  s.class_id,
  first_name,
  subject_id,
  score,
  NTILE(4) OVER (PARTITION BY class_id ORDER BY score DESC) AS quartile_rank
FROM student s
JOIN exam_score es ON s.student_id = es.student_id;

-- 班级内教师的聘用日期排名
-- 	•	ROW_NUMBER()：按聘用日期对每个部门中的教师进行排名。
SELECT
  teacher_id,
  first_name,
  department,
  hire_date,
  ROW_NUMBER() OVER (PARTITION BY department ORDER BY hire_date ASC) AS hire_rank
FROM teacher;

-- 计算学生分数的累计和（在整个结果集中不分组）
-- 	•	SUM(score)：计算分数的累计和。
-- 	•	OVER (ORDER BY score DESC)：根据 score 进行降序排列，然后在整个结果集中执行累计和的计算。没有 PARTITION BY，因此该窗口函数在整个表中的数据上操作，而不是在特定分组内。
-- 	•	ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW：表示从结果集的第一行到当前行（含当前行）之间的分数进行累计。
SELECT
  s.student_id,
  s.first_name,
  score,
  SUM(score) OVER (ORDER BY score DESC ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_score
FROM exam_score es join student s on es.student_id =s.student_id ;
