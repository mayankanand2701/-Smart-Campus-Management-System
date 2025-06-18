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
                        System.out.println("\n✏️ Update Faculty Details");

                        int fid = InputValidator.getInt("Faculty ID to Update: ");

                        // Check if faculty exists
                        PreparedStatement check = con.prepareStatement("SELECT * FROM faculty WHERE faculty_id = ?");
                        check.setInt(1, fid);
                        ResultSet rsFac = check.executeQuery();

                        if (!rsFac.next()) {
                            System.out.println("❌ No faculty found with that ID.");
                            break;
                        }

                        while (true) {
                            System.out.println("\n🔧 Choose field to update:");
                            System.out.println("1. Name");
                            System.out.println("2. Email");
                            System.out.println("3. Department");
                            System.out.println("4. Designation");
                            System.out.println("5. Done");
                            int choice = InputValidator.getInt("Your choice: ");

                            String query = "";
                            PreparedStatement ps1 = null;

                            switch (choice) {
                                case 1:
                                    query = "UPDATE faculty SET name=? WHERE faculty_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getName());
                                    break;
                                case 2:
                                    query = "UPDATE faculty SET email=? WHERE faculty_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getEmail());
                                    break;
                                case 3:
                                    query = "UPDATE faculty SET department=? WHERE faculty_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getDepartment());
                                    break;
                                case 4:
                                    query = "UPDATE faculty SET designation=? WHERE faculty_id=?";
                                    ps1 = con.prepareStatement(query);
                                    ps1.setString(1, InputValidator.getDesignation());
                                    break;
                                case 5:
                                    System.out.println("✅ Finished updating faculty.");
                                    return;
                                default:
                                    System.out.println("❌ Invalid choice. Try again.");
                                    continue;
                            }

                            // Set faculty_id in second parameter
                            ps1.setInt(2, fid);
                            int updated = ps1.executeUpdate();
                            if (updated > 0)
                                System.out.println("✅ Field updated successfully!");
                            else
                                System.out.println("❌ Update failed. Please try again.");
                        }


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