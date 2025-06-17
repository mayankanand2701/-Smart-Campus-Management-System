package validator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class InputValidator {

    private static final Scanner sc = new Scanner(System.in);

    public static String getName() {
        while (true) {
            System.out.print("Name: ");
            String name = sc.nextLine();
            if (name.matches("^[a-zA-Z ]+$")) return name;
            System.out.println("❌ Name must contain only letters and spaces. Try again.");
        }
    }

    public static String getEmail() {
        while (true) {
            System.out.print("Email: ");
            String email = sc.nextLine();
            if (email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) return email;
            System.out.println("❌ Invalid email format. Try again.");
        }
    }

    public static String getPhone() {
        while (true) {
            System.out.print("Phone: ");
            String phone = sc.nextLine();
            if (phone.matches("\\d{10}")) return phone;
            System.out.println("❌ Phone must be 10 digits.");
        }
    }

    public static String getDOB() {
        while (true) {
            System.out.print("DOB (yyyy-mm-dd): ");
            String dob = sc.nextLine();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                        .withResolverStyle(ResolverStyle.STRICT);
                LocalDate parsedDate = LocalDate.parse(dob, formatter);
                if (parsedDate.getYear() >= 2025) {
                    System.out.println("❌ Year must be before 2025.");
                    continue;
                }
                return dob;
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date. Use proper yyyy-mm-dd and valid date.");
            }
        }
    }

    public static String getGender() {
        while (true) {
            System.out.print("Gender (Male/Female): ");
            String gender = sc.nextLine();
            if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female")) return gender;
            System.out.println("❌ Enter 'Male' or 'Female' only.");
        }
    }

    public static String getDepartment() {
        while (true) {
            System.out.print("Department: ");
            String dept = sc.nextLine();
            if (dept.matches("^[a-zA-Z ]+$")) return dept;
            System.out.println("❌ Department must contain only letters and spaces.");
        }
    }

    public static int getYearOfStudy() {
        while (true) {
            System.out.print("Year of Study: ");
            String yearInput = sc.nextLine();
            try {
                int year = Integer.parseInt(yearInput);
                if (year >= 1 && year <= 6) return year;
                System.out.println("❌ Year must be between 1 and 6.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a numeric year.");
            }
        }
    }

    public static int getInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }
    public static String getDesignation() {
        while (true) {
            System.out.print("Designation: ");
            String designation = sc.nextLine();
            if (designation.matches("^[a-zA-Z ]+$")) return designation;
            System.out.println("❌ Designation must contain only letters and spaces.");
        }
    }
}
