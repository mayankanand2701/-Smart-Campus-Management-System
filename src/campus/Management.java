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
                        int credits = InputValidator.getInt("Credits: ");
                        String dept = InputValidator.getDepartment();
                        int semester = InputValidator.getInt("Semester: ");
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
                        int cid = InputValidator.getInt("Course ID to update: ");
                        String newCname = InputValidator.getName();
                        int newCredits = InputValidator.getInt("New Credits: ");

                        PreparedStatement update = con.prepareStatement("UPDATE Course SET course_name=?, credits=? WHERE course_id=?");
                        update.setString(1, newCname);
                        update.setInt(2, newCredits);
                        update.setInt(3, cid);
                        update.executeUpdate();
                        System.out.println("✅ Course updated successfully.");
                        break;

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
            System.out.println("5. Back");
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