
-- Index Use Cases

-- Use Case 1: Index on Enrollment(student_id)
-- Use Case: As a student, I want to quickly retrieve all courses I am enrolled in to view my current academic plan.
-- Why Index Helps: Adding an index on Enrollment(student_id) speeds up SELECT queries filtering by student ID, which are common when displaying student-specific course details.

-- SQL:
CREATE INDEX idx_enrollment_student_id ON Enrollment(student_id);


-- Use Case 2: Index on Timetable(faculty_id)
-- Use Case: As a faculty member, I want to load my weekly teaching schedule promptly when I log in to the system.
-- Why Index Helps: Faculty dashboards often fetch timetable data using faculty_id. Indexing this column helps return results faster for timetable-related queries.

-- SQL:
CREATE INDEX idx_timetable_faculty_id ON Timetable(faculty_id);



-- View Use Cases

-- Use Case 1: View - StudentCourseView
-- Use Case: As a student, I want to see all my enrolled courses along with course name, faculty, and department in a single screen.
-- Benefit: Encapsulates a multi-table join in a single logical view, so application developers or reporting tools can use SELECT * FROM StudentCourseView instead of writing complex joins.

-- SQL:
CREATE VIEW StudentCourseView AS
SELECT
s.student_id,
s.name AS student_name,
c.course_id,
c.course_name,
c.department,
f.name AS faculty_name
FROM Enrollment e
JOIN Student s ON s.student_id = e.student_id
JOIN Course c ON c.course_id = e.course_id
JOIN Faculty f ON f.faculty_id = c.faculty_id;


-- Use Case 2: View - FacultyTimetableView
-- Use Case: As a faculty member, I want to view my weekly timetable with course, time, and room in an organized way.
-- Benefit: Simplifies access to multiple timetable attributes without the need to perform joins in every faculty portal page or backend logic.

-- SQL:
CREATE VIEW FacultyTimetableView AS
SELECT
f.faculty_id,
f.name AS faculty_name,
c.course_name,
t.day_of_week,
t.start_time,
t.end_time,
t.room_no
FROM Timetable t
JOIN Course c ON c.course_id = t.course_id
JOIN Faculty f ON f.faculty_id = t.faculty_id;
