package campus;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import exception.FacultyNotFoundException;
import exception.InvalidEmailFormatException;
import pojos.TeacherPOJO;

public class Teacher 
{
	 private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
	 private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
	 
	 private TeacherPOJO teacherPOJO = new TeacherPOJO();
    public void viewCoursesTeaching(Connection con, int facultyId) {
        String query = "SELECT course_id, course_name FROM Course WHERE faculty_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n📘 Courses You Are Teaching:");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("- " + rs.getString("course_name") + " (Course ID: " + rs.getInt("course_id") + ")");
            }
            if (!found) System.out.println("❌ No courses found.");
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewUniqueStudentsTaught(Connection con, int facultyId) {
        String query = "SELECT DISTINCT e.student_id FROM Enrollment e " +
                       "JOIN Course c ON e.course_id = c.course_id " +
                       "WHERE c.faculty_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            System.out.println("\n👥 Unique Students Taught:");
            while (rs.next()) {
                count++;
                System.out.println("Student ID: " + rs.getInt("student_id"));
            }
            System.out.println("Total Unique Students: " + count);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewTimetable(Connection con, int facultyId) throws SQLException {
        displayFacultyTimetableFormatted(con, facultyId);
    }

    private void displayFacultyTimetableFormatted(Connection con, int facultyId) throws SQLException {
        String[] timeSlots = {
            "09:00 – 10:00", "10:00 – 11:00", "11:00 – 12:00", "12:00 – 13:00",
            "13:00 – 14:00", "14:00 – 15:00", "15:00 – 16:00", "16:00 – 17:00"
        };
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };

        Map<String, Map<String, String>> timetable = new LinkedHashMap<>();
        for (String slot : timeSlots) {
            Map<String, String> dayMap = new LinkedHashMap<>();
            for (String day : days) {
                dayMap.put(day, "-");
            }
            timetable.put(slot, dayMap);
        }

        String query = "SELECT t.day_of_week, t.start_time, t.end_time, c.course_name, t.room_no " +
                       "FROM timetable t " +
                       "JOIN course c ON t.course_id = c.course_id " +
                       "WHERE t.faculty_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, facultyId);
            ResultSet result = stmt.executeQuery();
            while (result.next()) {
                String day = result.getString("day_of_week");
                String start = result.getString("start_time").substring(0, 5);
                String end = result.getString("end_time").substring(0, 5);
                String timeSlot = start + " – " + end;
                String content = result.getString("course_name") + "\n📍" + result.getString("room_no");

                if (timetable.containsKey(timeSlot)) {
                    timetable.get(timeSlot).put(day, content);
                }
            }
        }

        // 🖨️ Print Header
        System.out.println("\n📅 Faculty Weekly Timetable");
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

                if (lines.length == 2) {
                    System.out.printf("| %-27s", lines[0]);
                } else {
                    System.out.printf("| %-27s", "-");
                }
            }
            System.out.println();

            // Second line (room)
            System.out.printf("%-17s", "");
            for (String day : days) {
                String val = timetable.get(time).get(day);
                String[] lines = val.split("\n");

                if (lines.length == 2) {
                    System.out.printf("| %-27s", lines[1]);
                } else {
                    System.out.printf("| %-27s", "");
                }
            }
            System.out.println();

            // Separator
            System.out.println("-".repeat(170));
        }
    }



public void editFacultyDetailsMenu(Connection con, Scanner sc, int facultyId) throws SQLException {
        boolean running = true;

        while (running) {
            System.out.println("\n----- Update Faculty Details -----");
            System.out.println("1. Update Name");
            System.out.println("2. Update Email");
            System.out.println("3. Update Phone");
            System.out.println("4. Update Department");
            System.out.println("5. Update Designation");
            System.out.println("6. Update All Fields");
            System.out.println("0. Return to Main Menu");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    updateFacultyField(con, sc, facultyId, "name");
                    break;
                case "2":
                    updateFacultyField(con, sc, facultyId, "email");
                    break;
                case "3":
                    updateFacultyField(con, sc, facultyId, "phone");
                    break;
                case "4":
                    updateFacultyField(con, sc, facultyId, "department");
                    break;
                case "5":
                    updateFacultyField(con, sc, facultyId, "designation");
                    break;
                case "6":
                    updateAllFacultyDetails(con, sc, facultyId);
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
    private void updateFacultyField(Connection con, Scanner sc, int facultyId, String field) throws SQLException {
        String query = "UPDATE Faculty SET " + field + " = ? WHERE faculty_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            String input;

            switch (field) {
                case "name":
                    System.out.print("Enter updated name: ");
                    input = sc.nextLine().trim();
                    if (!input.matches("^[A-Za-z ]+$")) {
                        System.out.println("❌ Name must contain only letters and spaces.");
                        return;
                    }
                    break;

                case "email":
                    System.out.print("Enter updated email: ");
                    input = sc.nextLine().trim();
                    if (!EMAIL_PATTERN.matcher(input).matches()) {
                        System.out.println("❌ Invalid email format.");
                        return;
                    }
                    break;

                case "phone":
                    System.out.print("Enter updated phone (10 digits): ");
                    input = sc.nextLine().trim();
                    if (!PHONE_PATTERN.matcher(input).matches()) {
                        System.out.println("❌ Invalid phone number.");
                        return;
                    }
                    break;

                case "department":
                    System.out.print("Enter updated department: ");
                    input = sc.nextLine().trim();
                    if (input.isEmpty()) {
                        System.out.println("❌ Department cannot be empty.");
                        return;
                    }
                    break;

                case "designation":
                    System.out.print("Enter updated designation: ");
                    input = sc.nextLine().trim();
                    if (input.isEmpty()) {
                        System.out.println("❌ Designation cannot be empty.");
                        return;
                    }
                    break;

                default:
                    System.out.println("❌ Unknown field.");
                    return;
            }

            stmt.setString(1, input);
            stmt.setInt(2, facultyId);

            int rows = stmt.executeUpdate();
            System.out.println("✅ " + field + " updated successfully. Rows affected: " + rows);
        }
    }
    private void updateAllFacultyDetails(Connection con, Scanner sc, int facultyId) throws SQLException {
        System.out.println("*** Write 'NA' to cancel anytime ***");

        // Name
        String name;
        while (true) {
            System.out.print("Enter updated name: ");
            name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("NA")) return;
            if (!name.matches("^[A-Za-z ]+$")) {
                System.out.println("❌ Name must contain only letters and spaces.");
            } else break;
        }

        // Email
        String email;
        while (true) {
            System.out.print("Enter updated email: ");
            email = sc.nextLine().trim();
            if (email.equalsIgnoreCase("NA")) return;
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("❌ Invalid email format.");
            } else break;
        }

        // Phone
        String phone;
        while (true) {
            System.out.print("Enter updated phone (10 digits): ");
            phone = sc.nextLine().trim();
            if (phone.equalsIgnoreCase("NA")) return;
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("❌ Invalid phone number.");
            } else break;
        }

        // Department
        String department;
        while (true) {
            System.out.print("Enter updated department: ");
            department = sc.nextLine().trim();
            if (department.equalsIgnoreCase("NA")) return;
            if (department.isEmpty()) {
                System.out.println("❌ Department cannot be empty.");
            } else break;
        }

        // Designation
        String designation;
        while (true) {
            System.out.print("Enter updated designation: ");
            designation = sc.nextLine().trim();
            if (designation.equalsIgnoreCase("NA")) return;
            if (designation.isEmpty()) {
                System.out.println("❌ Designation cannot be empty.");
            } else break;
        }

        String query = "UPDATE Faculty SET name = ?, email = ?, phone = ?, department = ?, designation = ? WHERE faculty_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, department);
            stmt.setString(5, designation);
            stmt.setInt(6, facultyId);

            int rows = stmt.executeUpdate();
            System.out.println("✅ Faculty details updated successfully. Rows affected: " + rows);
        }
    }





    public void viewStudentCountByYear(Connection con, int facultyId) {
        String query = "SELECT s.year_of_study, COUNT(DISTINCT s.student_id) AS student_count " +
                       "FROM Student s " +
                       "JOIN Enrollment e ON s.student_id = e.student_id " +
                       "JOIN Course c ON e.course_id = c.course_id " +
                       "WHERE c.faculty_id = ? " +
                       "GROUP BY s.year_of_study";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n📊 Student Count Grouped by Year of Study:");
            while (rs.next()) {
                System.out.println("Year: " + rs.getInt("year_of_study") +
                        " | Students: " + rs.getInt("student_count"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void manageFaculty(Connection con, Scanner sc) {
        try {
            int facultyId;
            while (true) {
                System.out.print("Enter your Faculty ID: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number for the Faculty ID.");
                    sc.next();
                    continue;
                }
                facultyId = sc.nextInt();
                break;
            }

            String checkQuery = "SELECT * FROM Faculty WHERE faculty_id = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, facultyId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new FacultyNotFoundException("❌ Faculty ID not found.");
                } else {
                    // Set teacher data to POJO
                    teacherPOJO.setFacultyId(rs.getInt("faculty_id"));
                    teacherPOJO.setName(rs.getString("name"));
                    teacherPOJO.setEmail(rs.getString("email"));
                    teacherPOJO.setDepartment(rs.getString("department"));
                    teacherPOJO.setDesignation(rs.getString("designation"));

                    System.out.println("\n👨‍🏫 Faculty Details:");
                    System.out.println("ID           : " + teacherPOJO.getFacultyId());
                    System.out.println("Name         : " + teacherPOJO.getName());
                    System.out.println("Email        : " + teacherPOJO.getEmail());
                    System.out.println("Department   : " + teacherPOJO.getDepartment());
                    System.out.println("Designation  : " + teacherPOJO.getDesignation());
                }
            }

            boolean exit = false;
            while (!exit) {
                System.out.println("\n Faculty Menu:");
                System.out.println("1. View all courses you are teaching");
                System.out.println("2. View all unique students you're teaching");
                System.out.println("3. View your timetable");
                System.out.println("4. Edit your details");
                System.out.println("5. View student count grouped by year");
                System.out.println("6. Exit");

                System.out.print("Choose an option: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number.");
                    sc.next();
                    continue;
                }

                int option = sc.nextInt();
                int facultyIdFromPOJO = teacherPOJO.getFacultyId();

                switch (option) {
                    case 1 -> viewCoursesTeaching(con, facultyIdFromPOJO);
                    case 2 -> viewUniqueStudentsTaught(con, facultyIdFromPOJO);
                    case 3 -> viewTimetable(con, facultyIdFromPOJO);
                    case 4 -> editFacultyDetailsMenu(con, sc, facultyId);
                    case 5 -> viewStudentCountByYear(con, facultyIdFromPOJO);
                    case 6 -> exit = true;
                    default -> System.out.println("❌ Invalid option! Try again.");
                }
            }

        } catch (FacultyNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        }
    }
}

