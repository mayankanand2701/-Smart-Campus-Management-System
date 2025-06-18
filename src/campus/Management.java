package campus;
import validator.InputValidator;

import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Management {

    public void manageAll(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n🛠️ Management Portal:");
            System.out.println("1. Manage Courses");
            System.out.println("2. Manage Faculty");
            System.out.println("3. Manage Students");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    manageCourses(con, sc);
                    break;
                case 2:
                   manageFaculty(con, sc);
                   break;
                case 3:
                   manageStudents(con, sc);
                   break;
                case 4:
                    System.out.println("Exiting Management Portal.");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    // Course Done
    public void manageCourses(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n📚 Course Management:");
            System.out.println("1. Add Course");
            System.out.println("2. View Courses");
            System.out.println("3. Update Course");
            System.out.println("4. Delete Course");
            System.out.println("5. Back");
            int ch = InputValidator.getInt("Enter your choice: ");

            try {
                switch (ch) {
                    case 1:
                        String cname = InputValidator.getName(); // Course name
                        int credits = InputValidator.getCredits("Credits: ");
                        String dept = InputValidator.getDepartment();
                        int semester = InputValidator.getSemester("Semester: ");
                        int facultyId = InputValidator.getInt("Faculty ID: ");

                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO Course (course_name, credits, department, semester, faculty_id) VALUES (?, ?, ?, ?, ?)"
                        );
                        ps.setString(1, cname);
                        ps.setInt(2, credits);
                        ps.setString(3, dept);
                        ps.setInt(4, semester);
                        ps.setInt(5, facultyId);
                        ps.executeUpdate();

                        System.out.println("✅ Course added successfully!");
                        break;

                    case 2:
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Course");
                        while (rs.next()) {
                            System.out.println(
                                rs.getInt("course_id") + " | " +
                                rs.getString("course_name") + " | " +
                                rs.getString("department") + " | " +
                                rs.getInt("semester")
                            );
                        }
                        break;

                    case 3:
                        System.out.println("\n✏️ Update Course Details");

                        int cid = InputValidator.getInt("Course ID to update: ");

                        // Check if course exists
                        PreparedStatement check = con.prepareStatement("SELECT * FROM Course WHERE course_id = ?");
                        check.setInt(1, cid);
                        ResultSet rsCourse = check.executeQuery();

                        if (!rsCourse.next()) {
                            System.out.println("❌ No course found with that ID.");
                            break;
                        }

                        while (true) {
                            System.out.println("\n🔧 Choose field to update:");
                            System.out.println("1. Course Name");
                            System.out.println("2. Credits");
                            System.out.println("3. Department");
                            System.out.println("4. Semester");
                            System.out.println("5. Faculty ID");
                            System.out.println("6. Done");
                            int choice = InputValidator.getInt("Your choice: ");

                            String query = "";
                            PreparedStatement ps1 = null;

                            switch (choice) {
                                case 1:
                                    query = "UPDATE Course SET course_name=? WHERE course_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getName());
                                    break;
                                case 2:
                                    query = "UPDATE Course SET credits=? WHERE course_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setInt(1, InputValidator.getCredits("New Credits (1–10): "));
                                    break;
                                case 3:
                                    query = "UPDATE Course SET department=? WHERE course_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getDepartment());
                                    break;
                                case 4:
                                    query = "UPDATE Course SET semester=? WHERE course_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setInt(1, InputValidator.getSemester("New Semester (1–8): "));
                                    break;
                                case 5:
                                    query = "UPDATE Course SET faculty_id=? WHERE course_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setInt(1, InputValidator.getInt("New Faculty ID: "));
                                    break;
                                case 6:
                                    System.out.println("✅ Finished updating course.");
                                    return;
                                default:
                                    System.out.println("❌ Invalid choice. Try again.");
                                    continue;
                            }

                            // Common step: Set course_id in all prepared statements
                            ps1.setInt(2, cid);
                            int updated = ps1.executeUpdate();
                            if (updated > 0)
                                System.out.println("✅ Field updated successfully!");
                            else
                                System.out.println("❌ Update failed. Please try again.");
                        }


                    case 4:
                        int delId = InputValidator.getInt("Course ID to delete: ");
                        PreparedStatement delete = con.prepareStatement("DELETE FROM Course WHERE course_id=?");
                        delete.setInt(1, delId);
                        delete.executeUpdate();
                        System.out.println("🗑️ Course deleted successfully.");
                        break;

                    case 5:
                        System.out.println("🔙 Returning to main menu...");
                        return;

                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                }

            } catch (SQLException e) {
                System.out.println("❌ SQL Error: " + e.getMessage());
            }
        }
    }

    // 
    private void manageFaculty(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n👨‍🏫 Faculty Management:");
            System.out.println("1. Add Faculty");
            System.out.println("2. View Faculty");
            System.out.println("3. Update Faculty");
            System.out.println("4. Delete Faculty");
            System.out.println("5. Schedule Faculty");
            System.out.println("6. Back");
            int ch = InputValidator.getInt("Enter your choice: ");

            try {
                switch (ch) {
                    case 1:
                        System.out.println("\n➕ Add New Faculty");
                        String name = InputValidator.getName();
                        String email = InputValidator.getEmail();
                        String dept = InputValidator.getDepartment();
                        String designation = InputValidator.getDesignation(); // Custom method added below

                        PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO faculty (name, email, department, designation) VALUES (?, ?, ?, ?)"
                        );
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, dept);
                        ps.setString(4, designation);
                        ps.executeUpdate();
                        System.out.println("✅ Faculty Added!");
                        break;

                    case 2:
                        System.out.println("\n📋 Faculty List:");
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM faculty");
                        while (rs.next()) {
                            System.out.println(
                                rs.getInt("faculty_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("email") + " | " +
                                rs.getString("department") + " | " +
                                rs.getString("designation")
                            );
                        }
                        break;

                    case 3:
                        System.out.println("\n✏️ Update Faculty Email");
                        int fid = InputValidator.getInt("Faculty ID to Update: ");
                        String newEmail = InputValidator.getEmail();

                        PreparedStatement update = con.prepareStatement(
                            "UPDATE faculty SET email=? WHERE faculty_id=?"
                        );
                        update.setString(1, newEmail);
                        update.setInt(2, fid);
                        int updatedRows = update.executeUpdate();

                        if (updatedRows > 0)
                            System.out.println("✅ Faculty Updated!");
                        else
                            System.out.println("❌ No faculty found with the given ID.");
                        break;

                    case 4:
                        System.out.println("\n🗑️ Delete Faculty");
                        int delId = InputValidator.getInt("Faculty ID to Delete: ");
                        PreparedStatement del = con.prepareStatement("DELETE FROM faculty WHERE faculty_id=?");
                        del.setInt(1, delId);
                        int deleted = del.executeUpdate();

                        if (deleted > 0)
                            System.out.println("🗑️ Faculty Deleted!");
                        else
                            System.out.println("❌ No faculty found with the given ID.");
                        break;
                        
                        
                    // I HAVE ADDED A NEW CASE - THIS IS FOR TIMETABLE     
                    case 5:
                        System.out.println("\n📅 Faculty Timetable:");
                        displayWeeklyTimetableFormatted(con); // Show timetable

                        while (true) {
                            System.out.print("\nDo you want to add a new schedule? (yes/no): ");
                            String response = sc.nextLine().trim().toLowerCase();

                            if (response.equals("no")) break;
                            if (!response.equals("yes")) {
                                System.out.println("⚠️ Please enter 'yes' or 'no'.");
                                continue;
                            }

                            int facultyId;
                            String facultyDept = "";
                            String facultyName = "";

                            // 🔁 Ask for valid Faculty ID
                            while (true) {
                                System.out.print("Enter Faculty ID: ");
                                try {
                                    facultyId = Integer.parseInt(sc.nextLine());

                                    String getFaculty = "SELECT department, name FROM faculty WHERE faculty_id = ?";
                                    try (PreparedStatement checkFaculty = con.prepareStatement(getFaculty)) {
                                        checkFaculty.setInt(1, facultyId);
                                        ResultSet facultyResult = checkFaculty.executeQuery();

                                        if (facultyResult.next()) {
                                            facultyDept = facultyResult.getString("department");
                                            facultyName = facultyResult.getString("name");
                                            System.out.println("✅ Faculty Found: " + facultyName + " (" + facultyDept + ")");
                                            break;
                                        } else {
                                            System.out.println("❌ Invalid Faculty ID. Try again.");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("⚠️ Please enter a valid number.");
                                }
                            }

                            // 📚 Show and validate course selection from same department
                            int courseId;
                            Set<Integer> validCourses = new HashSet<>();

                            String getCourses = "SELECT course_id, course_name FROM course WHERE department = ?";
                            try (PreparedStatement courseStmt = con.prepareStatement(getCourses)) {
                                courseStmt.setString(1, facultyDept);
                                ResultSet courseResult = courseStmt.executeQuery();

                                System.out.println("\n📚 Available Courses in " + facultyDept + ":");
                                while (courseResult.next()) {
                                    int cid = courseResult.getInt("course_id");
                                    validCourses.add(cid);
                                    System.out.println("Course ID: " + cid + " - " + courseResult.getString("course_name"));
                                }
                            }

                            while (true) {
                                System.out.print("Enter Course ID to schedule: ");
                                try {
                                    courseId = Integer.parseInt(sc.nextLine());
                                    if (validCourses.contains(courseId)) {
                                        break;
                                    } else {
                                        System.out.println("❌ Invalid Course ID for this department. Try again.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("⚠️ Please enter a valid number.");
                                }
                            }

                            // 🕒 Time Slots
                            String[] timeSlots = {
                                "09:00:00 - 10:00:00",
                                "10:00:00 - 11:00:00",
                                "11:00:00 - 12:00:00",
                                "12:00:00 - 13:00:00",
                                "13:00:00 - 14:00:00",
                                "14:00:00 - 15:00:00",
                                "15:00:00 - 16:00:00",
                                "16:00:00 - 17:00:00"
                            };

                         // ✅ Validate Day Input
                            String day = "";
                            List<String> validDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

                            while (true) {
                                System.out.print("Enter Day (e.g., Monday): ");
                                day = sc.nextLine().trim();
                                String formattedDay = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
                                if (validDays.contains(formattedDay)) {
                                    day = formattedDay;
                                    break;
                                } else {
                                    System.out.println("❌ Invalid day. Please enter a valid weekday (Monday to Friday).");
                                }
                            }


                            System.out.println("\n🕒 Available Time Slots:");
                            for (int i = 0; i < timeSlots.length; i++) {
                                System.out.println((i + 1) + ". " + timeSlots[i]);
                            }

                            System.out.print("Select time slot number (1–8): ");
                            int slotIndex = Integer.parseInt(sc.nextLine()) - 1;
                            if (slotIndex < 0 || slotIndex >= timeSlots.length) {
                                System.out.println("❌ Invalid time slot selection.");
                                break;
                            }

                            String[] slotParts = timeSlots[slotIndex].split(" - ");
                            String startTime = slotParts[0];
                            String endTime = slotParts[1];

                            // Check for slot conflict
                            String checkSlot = "SELECT * FROM Timetable WHERE day_of_week = ? AND start_time = ? AND end_time = ?";
                            try (PreparedStatement slotStmt = con.prepareStatement(checkSlot)) {
                                slotStmt.setString(1, day);
                                slotStmt.setString(2, startTime);
                                slotStmt.setString(3, endTime);
                                ResultSet slotResult = slotStmt.executeQuery();

                                if (slotResult.next()) {
                                    System.out.println("❌ This slot is already occupied. Please try another.");
                                    continue;
                                }
                            }

                            // Room number
                  
                            System.out.print("Enter Room Number (e.g., CSE101): ");
                            String roomNo = sc.nextLine().trim();

                            // Check if the room is already booked on the same day and overlapping time
                            String roomCheckQuery = "SELECT f.name, t.start_time, t.end_time FROM Timetable t " +
                                                    "JOIN faculty f ON t.faculty_id = f.faculty_id " +
                                                    "WHERE t.room_no = ? AND t.day_of_week = ? " +
                                                    "AND NOT (t.end_time <= ? OR t.start_time >= ?)";
                            try (PreparedStatement roomStmt = con.prepareStatement(roomCheckQuery)) {
                                roomStmt.setString(1, roomNo);
                                roomStmt.setString(2, day);
                                roomStmt.setTime(3, Time.valueOf(startTime));
                                roomStmt.setTime(4, Time.valueOf(endTime));
                                ResultSet roomResult = roomStmt.executeQuery();
                                if (roomResult.next()) {
                                    String occupyingFaculty = roomResult.getString("name");
                                    String occupiedFrom = roomResult.getString("start_time").substring(0,5);
                                    String occupiedTo = roomResult.getString("end_time").substring(0,5);
                                    System.out.println("❌ This room is already occupied by " + occupyingFaculty + 
                                                       " from " + occupiedFrom + " to " + occupiedTo + " on " + day + ". Please choose another room.");
                                    continue;
                                }
                            }



                            // ✅ Insert Schedule
                            String insertTimetable = "INSERT INTO Timetable (course_id, faculty_id, day_of_week, start_time, end_time, room_no) VALUES (?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = con.prepareStatement(insertTimetable)) {
                                insertStmt.setInt(1, courseId);
                                insertStmt.setInt(2, facultyId);
                                insertStmt.setString(3, day);
                                insertStmt.setString(4, startTime);
                                insertStmt.setString(5, endTime);
                                insertStmt.setString(6, roomNo);

                                int rowsInserted = insertStmt.executeUpdate();
                                if (rowsInserted > 0) {
                                    System.out.println("✅ Schedule added successfully!");
                                    System.out.println("\n📋 Updated Schedule for " + facultyName + ":");
                                    printFacultySchedule(con, facultyId);
                                } else {
                                    System.out.println("❌ Failed to insert schedule.");
                                }
                            }
                        }
                        break;


                    case 6:
                        System.out.println("🔙 Returning to main menu...");
                        return;

                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                }

            } catch (SQLException e) {
                System.out.println("❌ SQL Error: " + e.getMessage());
            }
        }
    }


    //BELOW ARE THE 2 FUNCTIONS FOR PRINTING THE TIMETABLE FOR FACULTY 
    private void printFacultySchedule(Connection con, int facultyId) throws SQLException {
        String query = "SELECT day_of_week, start_time, end_time, course_id, room_no FROM Timetable WHERE faculty_id = ? ORDER BY FIELD(day_of_week, 'Monday','Tuesday','Wednesday','Thursday','Friday'), start_time";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, facultyId);
            ResultSet rs = stmt.executeQuery();
            System.out.printf("%-10s | %-10s - %-10s | %-10s | %-8s%n", "Day", "Start", "End", "Course ID", "Room");
            System.out.println("-------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s | %-10s - %-10s | %-10d | %-8s%n",
                    rs.getString("day_of_week"),
                    rs.getString("start_time"),
                    rs.getString("end_time"),
                    rs.getInt("course_id"),
                    rs.getString("room_no"));
            }
        }
    }

    private void displayWeeklyTimetableFormatted(Connection con) throws SQLException {
    	String[] timeSlots = {
    		    "09:00 – 10:00",
    		    "10:00 – 11:00",
    		    "11:00 – 12:00",
    		    "12:00 – 13:00",
    		    "13:00 – 14:00",
    		    "14:00 – 15:00",
    		    "15:00 – 16:00",
    		    "16:00 – 17:00"
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
                       "FROM Timetable t " +
                       "JOIN course c ON t.course_id = c.course_id " +
                       "JOIN faculty f ON t.faculty_id = f.faculty_id";

        try (Statement stmt = con.createStatement(); ResultSet result = stmt.executeQuery(query)) {
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
        System.out.println("\n📅 Smart Campus Weekly Timetable");
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
                    // Pad each line to fit 3-line cell
                    System.out.printf("| %-27s", lines[0]);
                } else {
                    System.out.printf("| %-27s", "-");
                }
            }
            System.out.println();

            // Print second line (faculty)
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

            // Print third line (room)
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

            // Print separator
            System.out.println("-".repeat(170));
        }
    }


   
    // Student Done
    public void manageStudents(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n🎓 Student Management:");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            int ch = InputValidator.getInt("");

            try {
                switch (ch) {
                    case 1:
                        try {
                            String name = InputValidator.getName();
                            String email = InputValidator.getEmail();
                            String phone = InputValidator.getPhone();
                            String dob = InputValidator.getDOB();
                            String gender = InputValidator.getGender();
                            String dept = InputValidator.getDepartment();
                            int year = InputValidator.getYearOfStudy();

                            PreparedStatement ps = con.prepareStatement(
                                    "INSERT INTO student (name, email, phone, dob, gender, department, year_of_study) VALUES (?, ?, ?, ?, ?, ?, ?)"
                            );
                            ps.setString(1, name);
                            ps.setString(2, email);
                            ps.setString(3, phone);
                            ps.setDate(4, Date.valueOf(dob));
                            ps.setString(5, gender);
                            ps.setString(6, dept);
                            ps.setInt(7, year);
                            ps.executeUpdate();

                            System.out.println("✅ Student Added!");
                        } catch (Exception e) {
                            System.out.println("❌ Error: " + e.getMessage());
                        }
                        break;

                    case 2:
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM student");
                        while (rs.next()) {
                            System.out.println(
                                rs.getInt("student_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("department")
                            );
                        }
                        break;

                    case 3:
                        int sid = InputValidator.getInt("Enter Student ID to update: ");
                        
                        PreparedStatement check = con.prepareStatement("SELECT * FROM student WHERE student_id = ?");
                        check.setInt(1, sid);
                        ResultSet rsCheck = check.executeQuery();
                        if (!rsCheck.next()) {
                            System.out.println("❌ Student not found!");
                            break;
                        }

                        while (true) {
                            System.out.println("\n🔧 Choose field to update:");
                            System.out.println("1. Name");
                            System.out.println("2. Email");
                            System.out.println("3. Phone");
                            System.out.println("4. DOB");
                            System.out.println("5. Gender");
                            System.out.println("6. Department");
                            System.out.println("7. Year of Study");
                            System.out.println("8. Done");
                            System.out.print("Enter your choice: ");
                            int choice = InputValidator.getInt("");

                            String query = "";
                            PreparedStatement ps1 = null;

                            switch (choice) {
                                case 1:
                                    query = "UPDATE student SET name=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getName());
                                    break;
                                case 2:
                                    query = "UPDATE student SET email=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getEmail());
                                    break;
                                case 3:
                                    query = "UPDATE student SET phone=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getPhone());
                                    break;
                                case 4:
                                    query = "UPDATE student SET dob=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setDate(1, Date.valueOf(InputValidator.getDOB()));
                                    break;
                                case 5:
                                    query = "UPDATE student SET gender=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getGender());
                                    break;
                                case 6:
                                    query = "UPDATE student SET department=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getDepartment());
                                    break;
                                case 7:
                                    query = "UPDATE student SET year_of_study=? WHERE student_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setInt(1, InputValidator.getYearOfStudy());
                                    break;
                                case 8:
                                    System.out.println("✅ Finished updating.");
                                    return;
                                default:
                                    System.out.println("❌ Invalid choice. Try again.");
                                    continue;
                            }

                            ps1.setInt(2, sid);
                            ps1.executeUpdate();
                            System.out.println("✅ Field updated!");
                        }

                    case 4:
                        int delId = InputValidator.getInt("Student ID to Delete: ");
                        PreparedStatement delete = con.prepareStatement("DELETE FROM student WHERE student_id=?");
                        delete.setInt(1, delId);
                        delete.executeUpdate();
                        System.out.println("🗑️ Student Deleted!");
                        break;

                    case 5:
                        System.out.println("🔙 Returning to main menu...");
                        return;

                    default:
                        System.out.println("❌ Invalid choice. Please try again.");
                }

            } catch (SQLException e) {
                System.out.println("❌ SQL Error: " + e.getMessage());
            }
        }
    }
}