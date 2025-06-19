# 📚 Smart Campus Management System 

## 🚀 Sprint 1 : Foundational Development
This is the foundational sprint for a **Smart Campus Management System (SCMS)** built using **Core Java**, **JDBC**, and **SQL**. The system supports basic operations like student registration, course management, timetable structuring, faculty assignment, and input/output handling.

## 🎯 Sprint Goal
To create the **core data model** and **backend logic** using:
- Normalized SQL schema
- Core Java POJOs
- JDBC CRUD operations
- Thread simulation for concurrency
- Input handling

## 🧱 Core Components

### ✅ SQL Schema
- Normalized tables for `Students`, `Courses`, `Faculty`, `Timetable` and `Enrollments`
- Keys, constraints, indexing, and views implemented
- Constraints : primary keys, foreign keys, auto-increment IDs
- Sample data inserted for testing

### ✅ Java POJOs
- Implemented `Person`, `Student` classes
- Used **inheritance**, **getters**, **setters**, **access modifiers**, **constructors**, and **collections**

### ✅ JDBC Integration
- MySQL used for persistent storage
- CRUD operations using JDBC
- Proper connection handling
- Prepared statements and exception handling

### ✅ Concurrency Simulation
- Threaded student enrollments
- `synchronized` blocks for consistency

### ✅ Input and Logging
- Scanner used for interactive CLI input
- Console-based menus for Student and Faculty modules
- Validation via InputValidator class

## 🧑‍💻 How to Clone and Run the Project

### 🔗 Step 1: Clone the Repository
 ```javascript
  https://github.com/mayankanand2701/Smart-Campus-Management-System.git
  cd Smart-Campus-Management-System  
  ```

### 💻 Step 2: Open in Your IDE
- ✅ Using Eclipse :
  - Open Eclipse → File → Import
  - Select Existing Projects into Workspace
  - Browse to the cloned folder and click Finish
  - Make sure your JDK and MySQL JDBC Driver (mysql-connector-java) are added to the build path

- ✅ Using IntelliJ IDEA:
  - Open IntelliJ → File → Open
  - Select the project folder you cloned
  - IntelliJ will auto-detect and set up as a Java project
  - Add your mysql-connector-java in Project Structure → Modules → Dependencies

### ▶️ Step 3: Run the Project
- Go to the campus package
- Locate and open the file: Main.java
- Run the main() method (Right-click → Run or use the IDE's Run button)
- Follow the menu prompts to interact with the system

## 👨‍💻 Contributors
- Jobin Shery Mathew
- KANALA VENKATA LAKSHMI PRASANNA
- Kaveesh Bhat
- Kishor Kumar Parida
- Kunal Kanti Saha
- Mayank Anand
- Mugesh B
- Neha Mohanta
