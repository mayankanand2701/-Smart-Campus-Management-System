package pojos;

public class TeacherPOJO {
    private int facultyId;
    private String name;
    private String email;
    private String department;
    private String designation;

    // Constructor
    public TeacherPOJO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TeacherPOJO(int facultyId, String name, String email, String department, String designation) {
		super();
		this.facultyId = facultyId;
		this.name = name;
		this.email = email;
		this.department = department;
		this.designation = designation;
	}
   

    // Getters and Setters
    public int getFacultyId() {
        return facultyId;
    }


	public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
