package pojos;

import java.sql.Date;

public class StudentPOJO {
    private int studentId;
    private String name;
    private String email;
    private String phone;
    private Date dob;
    private String gender;
    private String department;
    private int yearOfStudy;
    
    // Constructors
    public StudentPOJO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public StudentPOJO(int studentId, String name, String email, String phone, Date dob, String gender,
			String department, int yearOfStudy) {
		super();
		this.studentId = studentId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.dob = dob;
		this.gender = gender;
		this.department = department;
		this.yearOfStudy = yearOfStudy;
	}

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }
	public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Date getDob() {
        return dob;
    }
    public void setDob(Date dob) {
        this.dob = dob;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public int getYearOfStudy() {
        return yearOfStudy;
    }
    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }
}

