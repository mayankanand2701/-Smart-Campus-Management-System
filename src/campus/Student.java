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
                                    	updateDetailsMenu(con, sc, studentId);
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

private void updateDetailsMenu(Connection con, Scanner sc, int studentId) throws SQLException {
    boolean running = true;

    while (running) {
        System.out.println("\n----- Update Student Details -----");
        System.out.println("1. Update Name");
        System.out.println("2. Update Email");
        System.out.println("3. Update Phone");
        System.out.println("4. Update DOB");
        System.out.println("5. Update Gender");
        System.out.println("6. Update Department");
        System.out.println("7. Update Year of Study");
        System.out.println("8. Update All Fields");
        System.out.println("0. Return to Main Menu");
        System.out.print("Choose an option: ");

        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                updateField(con, sc, studentId, "name");
                break;
            case "2":
                updateField(con, sc, studentId, "email");
                break;
            case "3":
                updateField(con, sc, studentId, "phone");
                break;
            case "4":
                updateField(con, sc, studentId, "dob");
                break;
            case "5":
                updateField(con, sc, studentId, "gender");
                break;
            case "6":
                updateField(con, sc, studentId, "department");
                break;
            case "7":
                updateField(con, sc, studentId, "year_of_study");
                break;
            case "8":
                updateAllDetails(con, sc, studentId); 
                break;
            case "0":
                System.out.println("🔙 Returning to main menu...");
                running = false;
                break;
            default:
                System.out.println("❌ Invalid option. Please try again.");
        }
    }
}
private void updateField(Connection con, Scanner sc, int studentId, String field) throws SQLException {
    String query = "UPDATE Student SET " + field + " = ? WHERE student_id = ?";
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        switch (field) {
            case "name":
                System.out.print("Enter updated name: ");
                String name = sc.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("❌ Name cannot be empty.");
                    return;
                }
                if (!name.matches("^[A-Za-z ]+$")) {
                    System.out.println("❌ Name must contain only letters and spaces. No digits or special characters allowed.");
                    return;
                }
                stmt.setString(1, name);
                break;

            case "email":
                System.out.print("Enter updated email: ");
                String email = sc.nextLine().trim();
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    System.out.println("❌ Invalid email format.");
                    return;
                }
                stmt.setString(1, email);
                break;

            case "phone":
                System.out.print("Enter updated phone (10 digits): ");
                String phone = sc.nextLine().trim();
                if (!PHONE_PATTERN.matcher(phone).matches()) {
                    System.out.println("❌ Invalid phone number.");
                    return;
                }
                stmt.setString(1, phone);
                break;

            case "dob":
                System.out.print("Enter updated DOB (yyyy-MM-dd): ");
                String dobInput = sc.nextLine().trim();
                try {
                    java.sql.Date dob = java.sql.Date.valueOf(dobInput);
                    stmt.setDate(1, dob);
                } catch (IllegalArgumentException e) {
                    System.out.println("❌ Invalid date format.");
                    return;
                }
                break;

            case "gender":
                System.out.print("Enter updated gender (male/female/other): ");
                String gender = sc.nextLine().trim().toLowerCase();
                if (!gender.matches("male|female|other")) {
                    System.out.println("❌ Invalid gender.");
                    return;
                }
                stmt.setString(1, gender);
                break;

            case "department":
                System.out.print("Enter updated department: ");
                String dept = sc.nextLine().trim();
                if (dept.isEmpty()) {
                    System.out.println("❌ Department cannot be empty.");
                    return;
                }
                stmt.setString(1, dept);
                break;

            case "year_of_study":
                System.out.print("Enter updated year of study (1-5): ");
                String input = sc.nextLine().trim();
                try {
                    int year = Integer.parseInt(input);
                    if (year < 1 || year > 5) {
                        System.out.println("❌ Year must be between 1 and 5.");
                        return;
                    }
                    stmt.setInt(1, year);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid number.");
                    return;
                }
                break;

            default:
                System.out.println("❌ Unknown field.");
                return;
        }

        stmt.setInt(2, studentId);
        int rows = stmt.executeUpdate();
        System.out.println("✅ " + field.replace("_", " ") + " updated successfully. Rows affected: " + rows);
    }
}

private void updateAllDetails(Connection con, Scanner sc, int studentId) throws SQLException {
    StudentPOJO student = new StudentPOJO();
    System.out.println("***write 'NA' to cancel anytime***");
    // Name (non-empty)
    String name;
    while (true) {
        System.out.print("Enter updated name: ");
        name = sc.nextLine().trim();
        if (name.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        if (!name.matches("^[A-Za-z ]+$")) {
            System.out.println("❌ Name must contain only letters and spaces. No digits or special characters allowed.");
            return;
        }
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            
        } else {
            break;
        }
    }
    student.setName(name);

    // Email (pattern)
    String newEmail;
    while (true) {
        System.out.print("Enter updated email: ");
        newEmail = sc.nextLine();
        if (newEmail.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            System.out.println("❌ Invalid email format. Please try again.");
        } else {
            break;
        }
    }
    student.setEmail(newEmail);

    String newPhone;
    while (true) {
        System.out.print("Enter updated phone (10 digits): ");
        newPhone = sc.nextLine();
        if (newPhone.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        if (!PHONE_PATTERN.matcher(newPhone).matches()) {
            System.out.println("❌ Invalid phone number. Must be exactly 10 digits. Please try again.");
        } else {
            break;
        }
    }
    student.setPhone(newPhone);

    // Date of Birth (format check)
    String dob;
    while (true) {
        System.out.print("Enter updated DOB (yyyy-MM-dd): ");
        dob = sc.nextLine().trim();
        if (dob.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        try {
            java.sql.Date.valueOf(dob); // Validate format
            break;
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid date format. Use yyyy-MM-dd.");
        }
    }
    student.setDob(Date.valueOf(dob));

    // Gender (male/female/other)
    String gender;
    while (true) {
        System.out.print("Enter updated gender (male/female/other): ");
        gender = sc.nextLine().trim().toLowerCase();
        if (gender.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
            System.out.println("❌ Invalid gender. Please enter male, female, or other.");
        } else {
            break;
        }
    }
    student.setGender(gender);

    // Department (non-empty)
    String department;
    while (true) {
        System.out.print("Enter updated department: ");
        department = sc.nextLine().trim();
        if (department.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }
        if (department.isEmpty()) {
            System.out.println("❌ Department cannot be empty.");
        } else {
            break;
        }
    }
    student.setDepartment(department);

    // Year of study (1–5 range)
    int year;
    while (true) {
        System.out.print("Enter updated year of study (1-5): ");
        String input = sc.nextLine().trim();

        if (input.equalsIgnoreCase("NA")) {
            System.out.println("🔙 Returning to main menu...\n");
            return;
        }

        try {
            year = Integer.parseInt(input);
            if (year < 1 || year > 5) {
                System.out.println("❌ Year must be between 1 and 5.");
            } else {
                break;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number. Please enter a valid year.");
        }
    }
    student.setYearOfStudy(year);

    // Update in DB
    String query = "UPDATE Student SET name = ?, email = ?, phone = ?, dob = ?, gender = ?, department = ?, year_of_study = ? WHERE student_id = ?";
    try (PreparedStatement stmt = con.prepareStatement(query)) {
        stmt.setString(1, student.getName());
        stmt.setString(2, student.getEmail());
        stmt.setString(3, student.getPhone());
        stmt.setDate(4, student.getDob());
        stmt.setString(5, student.getGender());
        stmt.setString(6, student.getDepartment());
        stmt.setInt(7, student.getYearOfStudy());
        stmt.setInt(8, studentId);

        int rows = stmt.executeUpdate();
        System.out.println("✅ Student details updated successfully. Rows affected: " + rows);
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