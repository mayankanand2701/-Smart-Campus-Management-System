-- Case Study: Smart Course Management System

-- ===========================
-- DML (Data Manipulation Language)
-- ===========================

-- Use Case 1: A new student joins the university
INSERT INTO Student VALUES 
(7, 'George King', 'george.k@univ.edu', '9888777666', '2002-11-05', 'Male', 'ECE', 1);

-- Use Case 2: Update year of study for a student who got promoted
UPDATE Student 
SET year_of_study = 3 
WHERE student_id = 1;


-- ===========================
-- DQL (Data Query Language)
-- ===========================

-- Use Case 1: Fetch all students from the CSE department
SELECT * 
FROM Student 
WHERE department = 'CSE';

-- Use Case 2: List all courses offered in semester 2
SELECT * 
FROM Course 
WHERE semester = 2;


-- ===========================
-- TCL (Transaction Control Language)
-- ===========================

-- Use Case 1: Insert student and rollback
BEGIN;
INSERT INTO Student VALUES (8, 'Helen Troy', 'helen.t@univ.edu', '9998887776', '2003-04-12', 'Female', 'ME', 1);
ROLLBACK;

-- Use Case 2: Update course credit and commit
BEGIN;
UPDATE Course 
SET credits = 5 
WHERE course_id = 201;
COMMIT;


-- ===========================
-- JOINs
-- ===========================

-- Use Case 1: List all CSE students along with the courses they can take (assume department-wise course allocation)
SELECT s.student_id, s.name, c.course_name, c.semester
FROM Student s
JOIN Course c ON s.department = c.department
WHERE s.department = 'CSE';

-- Use Case 2: Show each course and its faculty (faculty table assumed to exist)
-- Dummy join to show idea - Faculty table should exist for real output
SELECT c.course_name, c.credits, c.semester, f.faculty_name
FROM Course c
JOIN Faculty f ON c.faculty_id = f.faculty_id;


-- ===========================
-- Subqueries
-- ===========================

-- Use Case 1: Find students older than the average DOB
SELECT * 
FROM Student 
WHERE dob < (SELECT AVG(dob) FROM Student); -- Note: Might need casting in some DBs

-- Use Case 2: List all students enrolled in the same department as 'Bob Smith'
SELECT * 
FROM Student 
WHERE department = (SELECT department FROM Student WHERE name = 'Bob Smith');


-- ===========================
-- Aggregation
-- ===========================

-- Use Case 1: Count students in each department
SELECT department, COUNT(*) AS total_students
FROM Student
GROUP BY department;

-- Use Case 2: Average credits per department
SELECT department, AVG(credits) AS avg_credits
FROM Course
GROUP BY department;
