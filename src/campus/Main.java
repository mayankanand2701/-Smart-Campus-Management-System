package campus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
	static final String URL = "jdbc:mysql://localhost:3306/campusDB";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {
        Connection con = null;
        Scanner sc = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);

            while (true) {
                System.out.println("---- Smart Campus Management System ----");
                System.out.println("----------------------------------------");
                System.out.println("Options : ");
                System.out.println("1. For Student");
                System.out.println("2. For Teacher");
                System.out.println("3. For Management");
                System.out.println("4. Exit");
                System.out.println("----------------------------------------");

                int choice = -1;

                try
                {
                    System.out.print("Choose an option: ");
                    choice = sc.nextInt();
                    sc.nextLine(); 
                } 
                catch (InputMismatchException e)
                {
                    System.out.println("Invalid input! Please enter a number between 1 and 4.");
                    sc.nextLine(); 
                    continue;
                }

                Student student = new Student();
                Teacher faculty = new Teacher();
                Management management = new Management();

                switch (choice) 
                {
                    case 1:
                        try 
                        {
                            student.manageStudents(con, sc);
                        } 
                        catch (Exception e) 
                        {
                            System.out.println("Error in Student module: " + e.getMessage());
                        }
                        break;
                    case 2:
                        try 
                        {
                            faculty.manageFaculty(con, sc);
                        } 
                        catch (Exception e) 
                        {
                            System.out.println("Error in Teacher module: " + e.getMessage());
                        }
                        break;
                    case 3:
                        try 
                        {
                            management.manageAll(con, sc);
                        } 
                        catch (Exception e)
                        {
                            System.out.println("Error in Management module: " + e.getMessage());
                        }
                        break;
                    case 4:
                        System.out.println("Exiting... Thank you!");
                        con.close();
                        sc.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option! Please enter a number between 1 and 4.");
                }
            }

        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } 
        catch (SQLException e) 
        {
            System.out.println("Database connection error: " + e.getMessage());
        } 
        catch (Exception e) 
        {
            System.out.println("Unexpected error: " + e.getMessage());
        } 
        finally 
        {
            
        	try 
        	{
                if (con != null && !con.isClosed())
                {
                    con.close();
                }
                sc.close();
            } 
        	catch (SQLException e) 
        	{
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
