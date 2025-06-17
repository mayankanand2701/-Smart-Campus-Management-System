package campus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import exception.FacultyNotFoundException;
import exception.InvalidEmailFormatException;

public class Teacher 
{

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

    public void viewTimetable(Connection con, int facultyId) {
        String query = "SELECT course_id, day_of_week, start_time, end_time, room_no " +
                       "FROM Timetable WHERE faculty_id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, facultyId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n📅 Your Timetable:");
            while (rs.next()) {
                System.out.println("Course ID: " + rs.getInt("course_id") +
                        " | Day: " + rs.getString("day_of_week") +
                        " | Time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time") +
                        " | Room: " + rs.getString("room_no"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editFacultyDetails(Connection con, Scanner sc, int facultyId) {
        System.out.println("\n📝 Update Faculty Details:");
        sc.nextLine(); // consume leftover newline

        String email;
        while (true) {
            try {
                System.out.print("Enter new email: ");
                email = sc.nextLine();
                if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-z]+\\.com$")) {
                    throw new InvalidEmailFormatException("❌ Invalid email format! Email should be like example@domain.com");
                }
                break;
            } catch (InvalidEmailFormatException e) {
                System.out.println(e.getMessage());
            }
        }

        // ✅ Fetch and display available designations
        List<String> designations = new ArrayList<>();
        String designation = null;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT designation FROM Faculty")) {

            System.out.println("\nAvailable Designations:");
            int index = 1;
            while (rs.next()) {
                String desig = rs.getString("designation");
                designations.add(desig);
                System.out.println(index + ". " + desig);
                index++;
            }

            if (designations.isEmpty()) {
                System.out.println("❌ No designations found in the system.");
                return;
            }

            // Prompt user to select a designation
            int choice;
            while (true) {
                System.out.print("Select a designation by number: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input. Enter a number.");
                    sc.next();
                    continue;
                }
                choice = sc.nextInt();
                if (choice < 1 || choice > designations.size()) {
                    System.out.println("❌ Invalid choice. Please select a valid option.");
                    continue;
                }
                designation = designations.get(choice - 1);
                break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // ✅ Update faculty record (only email and designation now)
        String update = "UPDATE Faculty SET email = ?, designation = ? WHERE faculty_id = ?";
        try (PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, email);
            ps.setString(2, designation);
            ps.setInt(3, facultyId);

            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Faculty details updated successfully!");
            else
                System.out.println("❌ Faculty ID not found.");
        } catch (SQLException e) {
            e.printStackTrace();
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
                    sc.next(); // clear invalid input
                    continue;  // retry input
                }
                facultyId = sc.nextInt();
                break;  // valid input, exit loop
            }

            // ✅ Check if faculty exists
            String checkQuery = "SELECT * FROM Faculty WHERE faculty_id = ?";
            try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, facultyId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new FacultyNotFoundException("❌ Faculty ID not found. Please contact admin or try again.");
                } else {
                    // ✅ Faculty found → print details
                    System.out.println("\n👨‍🏫 Faculty Details:");
                    System.out.println("ID           : " + rs.getInt("faculty_id"));
                    System.out.println("Name         : " + rs.getString("name"));
                    System.out.println("Email        : " + rs.getString("email"));
                    System.out.println("Department   : " + rs.getString("department"));
                    System.out.println("Designation  : " + rs.getString("designation"));
                }
            }

            // ✅ Faculty exists → proceed to menu
            boolean exit = false;
            while (!exit) {
                System.out.println("\n👨‍🏫 Faculty Menu:");
                System.out.println("1. View all courses you are teaching");
                System.out.println("2. View all unique students you're teaching");
                System.out.println("3. View your timetable");
                System.out.println("4. Edit your details");
                System.out.println("5. View student count grouped by year");
                System.out.println("6. Exit");

                System.out.print("Choose an option: ");
                if (!sc.hasNextInt()) {
                    System.out.println("❌ Invalid input! Please enter a number for the option.");
                    sc.next(); // clear invalid input
                    continue;
                }

                int option = sc.nextInt();

                switch (option) {
                    case 1 -> viewCoursesTeaching(con, facultyId);
                    case 2 -> viewUniqueStudentsTaught(con, facultyId);
                    case 3 -> viewTimetable(con, facultyId);
                    case 4 -> editFacultyDetails(con, sc, facultyId);
                    case 5 -> viewStudentCountByYear(con, facultyId);
                    case 6 -> exit = true;
                    default -> System.out.println("❌ Invalid option! Try again.");
                }
            }

        } catch (FacultyNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}

