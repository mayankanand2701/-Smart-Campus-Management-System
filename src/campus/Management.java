package campus;
import validator.InputValidator;

import java.sql.*;
import java.util.Scanner;

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

    // course
    private void manageCourses(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n📘 Course Management:");
            System.out.println("1. Add Course");
            System.out.println("2. View Courses");
            System.out.println("3. Update Course");
            System.out.println("4. Delete Course");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            try {
                switch (ch) {
                    case 1:
                        System.out.print("Course Name: ");
                        String cname = sc.nextLine();
                        System.out.print("Credits: ");
                        int credits = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Department: ");
                        String dept = sc.nextLine();
                        System.out.print("Semester: ");
                        int semester = sc.nextInt();
                        System.out.print("Faculty ID: ");
                        int fid = sc.nextInt();

                        PreparedStatement ps = con.prepareStatement("INSERT INTO course (course_name, credits, department, semester, faculty_id) VALUES (?, ?, ?, ?, ?)");
                        ps.setString(1, cname);
                        ps.setInt(2, credits);
                        ps.setString(3, dept);
                        ps.setInt(4, semester);
                        ps.setInt(5, fid);
                        ps.executeUpdate();
                        System.out.println("✅ Course Added!");
                        break;

                    case 2:
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM course");
                        while (rs.next()) {
                            System.out.println(rs.getInt("course_id") + " | " + rs.getString("course_name") + " | Credits: " + rs.getInt("credits"));
                        }
                        break;

                    case 3:
                        System.out.print("Course ID to Update: ");
                        int cid = sc.nextInt();
                        sc.nextLine();
                        System.out.print("New Course Name: ");
                        String newName = sc.nextLine();
                        PreparedStatement update = con.prepareStatement("UPDATE course SET course_name=? WHERE course_id=?");
                        update.setString(1, newName);
                        update.setInt(2, cid);
                        update.executeUpdate();
                        System.out.println("✅ Course Updated!");
                        break;

                    case 4:
                        System.out.print("Course ID to Delete: ");
                        int delId = sc.nextInt();
                        PreparedStatement del = con.prepareStatement("DELETE FROM course WHERE course_id=?");
                        del.setInt(1, delId);
                        del.executeUpdate();
                        System.out.println("🗑️ Course Deleted!");
                        break;

                    case 5:
                        return;

                    default:
                        System.out.println("❌ Invalid Choice");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void manageFaculty(Connection con, Scanner sc) {
        while (true) {
            System.out.println("\n👨‍🏫 Faculty Management:");
            System.out.println("1. Add Faculty");
            System.out.println("2. View Faculty");
            System.out.println("3. Update Faculty");
            System.out.println("4. Delete Faculty");
            System.out.println("5. Back");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            try {
                switch (ch) {
                    case 1:
                        System.out.print("Name: ");
                        String name = sc.nextLine();
                        System.out.print("Email: ");
                        String email = sc.nextLine();
                        System.out.print("Department: ");
                        String dept = sc.nextLine();
                        System.out.print("Designation: ");
                        String designation = sc.nextLine();

                        PreparedStatement ps = con.prepareStatement("INSERT INTO faculty (name, email, department, designation) VALUES (?, ?, ?, ?)");
                        ps.setString(1, name);
                        ps.setString(2, email);
                        ps.setString(3, dept);
                        ps.setString(4, designation);
                        ps.executeUpdate();
                        System.out.println("✅ Faculty Added!");
                        break;

                    case 2:
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM faculty");
                        while (rs.next()) {
                            System.out.println(rs.getInt("faculty_id") + " | " + rs.getString("name") + " | " + rs.getString("department"));
                        }
                        break;

                    case 3:
                        System.out.print("Faculty ID to Update: ");
                        int fid = sc.nextInt();
                        sc.nextLine();
                        System.out.print("New Email: ");
                        String newEmail = sc.nextLine();
                        PreparedStatement update = con.prepareStatement("UPDATE faculty SET email=? WHERE faculty_id=?");
                        update.setString(1, newEmail);
                        update.setInt(2, fid);
                        update.executeUpdate();
                        System.out.println("✅ Faculty Updated!");
                        break;

                    case 4:
                        System.out.print("Faculty ID to Delete: ");
                        int delId = sc.nextInt();
                        PreparedStatement del = con.prepareStatement("DELETE FROM faculty WHERE faculty_id=?");
                        del.setInt(1, delId);
                        del.executeUpdate();
                        System.out.println("🗑️ Faculty Deleted!");
                        break;

                    case 5:
                        return;

                    default:
                        System.out.println("❌ Invalid Choice");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Student
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
                        int sid = InputValidator.getInt("Student ID to Update: ");
                        String newEmail = InputValidator.getEmail();

                        PreparedStatement update = con.prepareStatement("UPDATE student SET email=? WHERE student_id=?");
                        update.setString(1, newEmail);
                        update.setInt(2, sid);
                        update.executeUpdate();
                        System.out.println("✅ Student Updated!");
                        break;

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