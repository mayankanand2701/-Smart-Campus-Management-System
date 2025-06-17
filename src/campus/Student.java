package campus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Student
{
	// instance variable
	// getters
	
	// Regular expression patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    
	public void manageStudents(Connection con, Scanner sc) {
        try {
            System.out.print("Are you an existing student? (yes/no): ");
            String response = sc.next().toLowerCase();

            if (response.equals("yes")) {
            	System.out.print("Enter your Student ID: ");
            	String studentIdInput = sc.next();
            	int studentId;

            	if (!studentIdInput.matches("\\d+")) {
            	    System.out.println("❌ Invalid Student ID. It must be numeric.");
            	    return;
            	}
            	studentId = Integer.parseInt(studentIdInput);

                String query = "SELECT * FROM Student WHERE student_id = ?";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setInt(1, studentId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("\n🎓 Student Details:");
                    System.out.println("ID      : " + rs.getInt("student_id"));
                    System.out.println("Name    : " + rs.getString("name"));
                    System.out.println("Email   : " + rs.getString("email"));

                    while (true) {
                        System.out.println("\n📘 Choose an option:");
                        System.out.println("1. View enrolled courses");
                        System.out.println("2. View assigned faculty");
                        System.out.println("3. View timetable");
                        System.out.println("4. Edit student details");
                        System.out.println("5. Register to a new course");
                        System.out.println("6. Exit");

                        System.out.print("Enter your choice: ");
                        int subChoice = sc.nextInt();
                        sc.nextLine(); // clear newline

                        switch (subChoice) {
                            case 1:
                                viewCourses(con, studentId);
                                break;
                            case 2:
                                viewFaculty(con, studentId);
                                break;
                            case 3:
                                viewTimetable(con, studentId);
                                break;
                            case 4:
                                updateDetails(con, sc, studentId);
                                break;
                            case 5:
                                registerCourse(con, sc, studentId);
                                break;
                            case 6:
                                System.out.println("👋 Exiting student dashboard...");
                                return;
                            default:
                                System.out.println("❗ Invalid option. Try again.");
                        }
                    }

                } else {
                    System.out.println("❌ Invalid Student ID. Please try again.");
                }

                rs.close();
                ps.close();

            } else if (response.equals("no")) {
            	// Check this at later 
                // addStudent(con, sc);
            	System.out.println("Kindly ask Management to Register you as a Studnet");
            } else {
                System.out.println("Invalid input! Please type 'yes' or 'no'.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error occurred : Input valid number between 1 to 6");
            //e.printStackTrace();
            sc.nextLine(); 
        }
    }
	private void viewCourses(Connection con, int studentId) throws SQLException {
        String courseQuery = "SELECT c.course_id, c.course_name, c.credits, c.department, c.semester " +
                             "FROM Enrollment e JOIN Course c ON e.course_id = c.course_id " +
                             "WHERE e.student_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(courseQuery)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n📚 Enrolled Courses:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("course_name") +
                                   " | Dept: " + rs.getString("department") +
                                   " | Credits: " + rs.getInt("credits"));
            }
        }
    }

    private void viewFaculty(Connection con, int studentId) throws SQLException {
        String facultyQuery = "SELECT f.name, f.email, f.department " +
                              "FROM Faculty f JOIN Course c ON f.faculty_id = c.faculty_id " +
                              "JOIN Enrollment e ON c.course_id = e.course_id " +
                              "WHERE e.student_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(facultyQuery)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n👨‍🏫 Assigned Faculty:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("name") +
                                   " | " + rs.getString("email") +
                                   " | Dept: " + rs.getString("department"));
            }
        }
    }
    private void viewTimetable(Connection con, int studentId) throws SQLException {
        String timeQuery = "SELECT t.day_of_week, t.start_time, t.end_time, t.room_no, c.course_name " +
                           "FROM Timetable t JOIN Course c ON t.course_id = c.course_id " +
                           "JOIN Enrollment e ON c.course_id = e.course_id " +
                           "WHERE e.student_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(timeQuery)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\n🕒 Timetable:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("day_of_week") +
                                   " | " + rs.getString("course_name") +
                                   " | " + rs.getTime("start_time") +
                                   " - " + rs.getTime("end_time") +
                                   " | Room: " + rs.getString("room_no"));
            }
        }
    }

    private void updateDetails(Connection con, Scanner sc, int studentId) throws SQLException {
        System.out.print("Enter updated email: ");
        String newEmail = sc.nextLine();

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            System.out.println("❌ Invalid email format.");
            return;
        }

        System.out.print("Enter updated phone: ");
        String newPhone = sc.nextLine();

        if (!PHONE_PATTERN.matcher(newPhone).matches()) {
            System.out.println("❌ Invalid phone number. Must be exactly 10 digits.");
            return;
        }

        String updateQuery = "UPDATE Student SET email = ?, phone = ? WHERE student_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, newPhone);
            stmt.setInt(3, studentId);
            int rows = stmt.executeUpdate();
            System.out.println("✅ Student details updated! Rows affected: " + rows);
        }
    }

    private void registerCourse(Connection con, Scanner sc, int studentId) throws SQLException {
        System.out.print("Enter Course ID to register: ");
        int courseId = sc.nextInt();
        sc.nextLine();

        String enrollQuery = "INSERT INTO Enrollment (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(enrollQuery)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            int rows = stmt.executeUpdate();
            System.out.println("✅ Course registered! Rows affected: " + rows);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("❌ Already registered or invalid Course ID.");
        }
    }

	public void addStudent(Connection con, Scanner sc)
	{
        try 
        {
        	sc.nextLine();
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            System.out.print("Enter email: ");
            String email = sc.nextLine();
            System.out.print("Enter phone: ");
            String phone = sc.nextLine();
            System.out.print("Enter DOB (YYYY-MM-DD): ");
            String dob = sc.nextLine();
            System.out.print("Enter gender: ");
            String gender = sc.nextLine();
            System.out.print("Enter department: ");
            String dept = sc.nextLine();
            System.out.print("Enter year of study: ");
            int year = Integer.parseInt(sc.nextLine());

            String insert = "INSERT INTO student (name, email, phone, dob, gender, department, year_of_study) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insert)) {
                pst.setString(1, name);
                pst.setString(2, email);
                pst.setString(3, phone);
                pst.setDate(4, Date.valueOf(dob));
                pst.setString(5, gender);
                pst.setString(6, dept);
                pst.setInt(7, year);

                int rows = pst.executeUpdate();
                System.out.println("✅ Student added! Rows affected: " + rows);
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("❌ Error adding student:");
            e.printStackTrace();
        }
    }
	
	 public void viewEnrolledCourses(Connection con, int studentId) {
	        String courseQuery = "SELECT c.course_id, c.course_name, c.credits, c.department, c.semester " +
	                             "FROM Enrollment e " +
	                             "JOIN Course c ON e.course_id = c.course_id " +
	                             "WHERE e.student_id = ?";
	        try (PreparedStatement cstmt = con.prepareStatement(courseQuery)) {
	            cstmt.setInt(1, studentId);
	            ResultSet crs = cstmt.executeQuery();

	            System.out.println("\n📚 Enrolled Courses:");
	            boolean found = false;
	            while (crs.next()) {
	                found = true;
	                System.out.println("- " + crs.getString("course_name") +
	                                   " | Dept: " + crs.getString("department") +
	                                   " | Credits: " + crs.getInt("credits") +
	                                   " | Semester: " + crs.getInt("semester"));
	            }

	            if (!found) {
	                System.out.println("❌ No courses found for student ID: " + studentId);
	            }

	            crs.close();
	        } catch (SQLException e) {
	            System.out.println("❌ Error fetching enrolled courses:");
	            e.printStackTrace();
	        }
	    }
}