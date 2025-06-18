package campus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import pojos.StudentPOJO;

public class Student
{
	
	// Regular expression patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    
    public void manageStudents(Connection con, Scanner sc) {
        try {
            while (true) {
                System.out.print("Are you an existing student? (yes/no): ");
                String response = sc.next().toLowerCase();

                if (response.equals("yes")) {
                    while (true) {
                        System.out.print("\nEnter your Student ID (or type 'NA' to return to main menu): ");
                        String studentIdInput = sc.next();

                        if (studentIdInput.equalsIgnoreCase("NA")) {
                            System.out.println("🔙 Returning to main menu...\n");
                            return;
                        }

                        if (!studentIdInput.matches("\\d+")) {
                            System.out.println("❌ Invalid Student ID. It must be numeric.\n");
                            continue; // prompt again
                        }                  

                        int studentId = Integer.parseInt(studentIdInput);

                        String query = "SELECT * FROM Student WHERE student_id = ?";
                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, studentId);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                        	StudentPOJO student = new StudentPOJO();
                        	student.setStudentId(rs.getInt("student_id"));
                        	student.setName(rs.getString("name"));
                        	student.setEmail(rs.getString("email"));
                        	student.setPhone(rs.getString("phone"));
                        	student.setDob(rs.getDate("dob"));
                        	student.setGender(rs.getString("gender"));
                        	student.setDepartment(rs.getString("department"));
                        	student.setYearOfStudy(rs.getInt("year_of_study"));
                        	
                        	System.out.println("\n🎓 Student Details:");
                        	System.out.println("ID      : " + student.getStudentId());
                        	System.out.println("Name    : " + student.getName());
                        	System.out.println("Email   : " + student.getEmail());
                        	System.out.println("Phone Number   : " + student.getPhone());
                        	System.out.println("DOB   : " + student.getDob());
                        	System.out.println("Gender   : " + student.getGender());
                        	System.out.println("Department   : " + student.getDepartment());
                        	System.out.println("Year of Study   : " + student.getYearOfStudy());
                        	
                            while (true) {
                                System.out.println("\n📘 Choose an option:");
                                System.out.println("1. View enrolled courses");
                                System.out.println("2. View assigned faculty");
                                System.out.println("3. View timetable");
                                System.out.println("4. Edit student details");
                                System.out.println("5. Register to a new course");
                                System.out.println("6. Exit");

                                System.out.print("Enter your choice: ");
                                String choiceInput = sc.next();

                                if (!choiceInput.matches("\\d+")) {
                                    System.out.println("❗ Invalid input. Please enter a number between 1 and 6.");
                                    continue;
                                }

                                int subChoice = Integer.parseInt(choiceInput);
                                sc.nextLine(); 

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
                                        return; // Exit from the method
                                    default:
                                        System.out.println("❗ Invalid option. Try again.");
                                }
                            }
                        } else {
                            System.out.println("❌ Student ID not found. Please try again.");
                        }

                        rs.close();
                        ps.close();
                    }

                } else if (response.equals("no")) {
                    System.out.println("ℹ️ Kindly ask Management to register you as a student.");
                    return;
                } else {
                    System.out.println("Invalid input! Please type 'yes' or 'no'.");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error occurred: " + e.getMessage());
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
    	displayStudentTimetableFormatted(con, studentId);
    }

private void displayStudentTimetableFormatted(Connection con, int studentId) throws SQLException {
        String[] timeSlots = {
            "09:00 – 10:00", "10:00 – 11:00", "11:00 – 12:00", "12:00 – 13:00",
            "13:00 – 14:00", "14:00 – 15:00", "15:00 – 16:00", "16:00 – 17:00"
        };
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };

        // Create a nested map to store timetable data
        Map<String, Map<String, String>> timetable = new LinkedHashMap<>();
        for (String slot : timeSlots) {
            Map<String, String> dayMap = new LinkedHashMap<>();
            for (String day : days) {
                dayMap.put(day, "-");
            }
            timetable.put(slot, dayMap);
        }

        String query = "SELECT t.day_of_week, t.start_time, t.end_time, c.course_name, f.name, t.room_no " +
                       "FROM timetable t " +
                       "JOIN course c ON t.course_id = c.course_id " +
                       "JOIN faculty f ON t.faculty_id = f.faculty_id " +
                       "JOIN enrollment e ON c.course_id = e.course_id " +
                       "WHERE e.student_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                String day = result.getString("day_of_week");
                String start = result.getString("start_time").substring(0, 5);
                String end = result.getString("end_time").substring(0, 5);
                String timeSlot = start + " – " + end;
                String content = result.getString("course_name") + "\n🧑‍🏫 " + result.getString("name") + "\n📍" + result.getString("room_no");

                if (timetable.containsKey(timeSlot)) {
                    timetable.get(timeSlot).put(day, content);
                }
            }
        }

        // 🖨️ Print Header
        System.out.println("\n📅 Your Weekly Timetable");
        System.out.printf("%-17s", "Time Slot");
        for (String day : days) {
            System.out.printf("| %-27s", day);
        }
        System.out.println();
        System.out.println("=".repeat(170));

        // 🖨️ Print timetable row by row
        for (String time : timeSlots) {
            System.out.printf("%-17s", time);
            for (String day : days) {
                String val = timetable.get(time).get(day);
                String[] lines = val.split("\n");

                if (lines.length == 3) {
                    System.out.printf("| %-27s", lines[0]);
                } else {
                    System.out.printf("| %-27s", "-");
                }
            }
            System.out.println();

            // Second line (faculty)
            System.out.printf("%-17s", "");
            for (String day : days) {
                String val = timetable.get(time).get(day);
                String[] lines = val.split("\n");

                if (lines.length == 3) {
                    System.out.printf("| %-27s", lines[1]);
                } else {
                    System.out.printf("| %-27s", "");
                }
            }
            System.out.println();

            // Third line (room)
            System.out.printf("%-17s", "");
            for (String day : days) {
                String val = timetable.get(time).get(day);
                String[] lines = val.split("\n");

                if (lines.length == 3) {
                    System.out.printf("| %-27s", lines[2]);
                } else {
                    System.out.printf("| %-27s", "");
                }
            }
            System.out.println();

            // Separator
            System.out.println("-".repeat(170));
        }
    }

    private void updateDetails(Connection con, Scanner sc, int studentId) throws SQLException {
    	StudentPOJO student = new StudentPOJO();

    	System.out.print("Enter updated email: ");
    	String newEmail = sc.nextLine();
    	if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
    	    System.out.println("❌ Invalid email format.");
    	    return;
    	}
    	student.setEmail(newEmail);

    	System.out.print("Enter updated phone: ");
    	String newPhone = sc.nextLine();
    	if (!PHONE_PATTERN.matcher(newPhone).matches()) {
    	    System.out.println("❌ Invalid phone number. Must be exactly 10 digits.");
    	    return;
    	}
    	student.setPhone(newPhone);

    	String updateQuery = "UPDATE Student SET email = ?, phone = ? WHERE student_id = ?";
    	try (PreparedStatement stmt = con.prepareStatement(updateQuery)) {
    	    stmt.setString(1, student.getEmail());
    	    stmt.setString(2, student.getPhone());
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

    public void addStudent(Connection con, Scanner sc) {
        try {
            sc.nextLine(); // consume newline
            StudentPOJO student = new StudentPOJO();

            System.out.print("Enter name: ");
            student.setName(sc.nextLine());

            System.out.print("Enter email: ");
            student.setEmail(sc.nextLine());

            System.out.print("Enter phone: ");
            student.setPhone(sc.nextLine());

            System.out.print("Enter DOB (YYYY-MM-DD): ");
            student.setDob(Date.valueOf(sc.nextLine()));

            System.out.print("Enter gender: ");
            student.setGender(sc.nextLine());

            System.out.print("Enter department: ");
            student.setDepartment(sc.nextLine());

            System.out.print("Enter year of study: ");
            student.setYearOfStudy(Integer.parseInt(sc.nextLine()));

            String insert = "INSERT INTO student (name, email, phone, dob, gender, department, year_of_study) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pst = con.prepareStatement(insert)) {
                pst.setString(1, student.getName());
                pst.setString(2, student.getEmail());
                pst.setString(3, student.getPhone());
                pst.setDate(4, student.getDob());
                pst.setString(5, student.getGender());
                pst.setString(6, student.getDepartment());
                pst.setInt(7, student.getYearOfStudy());

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
	                System.out.println("Course ID: " + crs.getInt("course_id") +
	                		           " | Course Name: " + crs.getString("course_name") +
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