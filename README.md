<img width="482" height="263" alt="Screenshot 2026-11-24 184420" src="https://github.com/user-attachments/assets/8e919b17-6702-439c-a920-ffe1b49df474" />


CREATE DATABASE attendance_db;
USE attendance_db;

CREATE TABLE course (
    course_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL
);

CREATE TABLE section (
    section_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    section_name VARCHAR(50) NOT NULL,
    course_id INT(11) NOT NULL,

    FOREIGN KEY (course_id)
        REFERENCES course(course_id)
);

CREATE TABLE student_info (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    section_id INT(11) NOT NULL,
    date DATE DEFAULT NULL,

    FOREIGN KEY (section_id)
        REFERENCES section(section_id)
);

CREATE TABLE attendance_record (
    record_id INT(11) AUTO_INCREMENT PRIMARY KEY,
    id VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (id)
        REFERENCES student_info(id)
);

Project Title: Student Attendance Monitoring System Team/Members: Char May Armada, Lenny Galeno, Daniela Pastera, Raysian Kent Guilaran, Paul Andrie Velasco, Kim Laurence Maniero Section: BSIT 2-B Instructor: Anne Gee Almarza Date: November 23, 2026.

Project Overview: Problem -
Attendance is recorded on manual flat sheets for every class session, with each student's id, name, section, course, date, and status. Existing problems include student details repeating in every log, status typed as free text, totals tallied by hand, and records not being linked.

Intended User: Instructors / Faculty, Students, and Database Administrator.

Objectives: Design, normalize, and implement a Third Normal Form (3NF) relational database, attendance_db, for accurate daily attendance recording, querying, and reporting.

Features: Normalize the database through 1NF, 2NF, and 3NF across four linked tables (student_info, section, course, attendance_record). Enforce referential integrity using primary keys and foreign keys. Store standardized attendance status values (Present, Absent, or Late) in status (varchar(20)). Support SQL queries and reports using SELECT, WHERE, ORDER BY, GROUP BY, HAVING, INNER JOIN, LEFT JOIN, UPDATE, DELETE, and summary counts.

Scope: •Student records, sections, and courses •Daily attendance logs with status •Queries, joins, and summary counts

Limitations: •Local MySQL / XAMPP setup •Manual entry (no RFID or biometrics) •Attendance only: no grading, billing, or payroll

Technology and Setup:
MySQL / XAMPP – Local database server hosting attendance_db
phpMyAdmin / Designer – Create tables, inspect the 1 : M relationships, and run SQL
Apache NetBeans / Java (optional) – Application layer connecting through JDBC
Demonstration – Live walkthrough of the database, queries, and reports

Installation and Execution:
1. Prepare the local database server using MySQL / XAMPP and open phpMyAdmin or SQL Designer (or Apache NetBeans / Java with JDBC if using the optional application layer).
2. Open the SQL Editor or script window and load the attendance_db DDL script.
3. Execute the script to create the course, section, student_info, and attendance_record tables along with their primary and foreign key constraints.
4. Run sample SQL queries and summary reports to verify data insertion, joins, updates, deletions, and attendance totals.

1. Table Description, Important Fields, and Keys

A. Course Table (Primary Key): Stores academic course or program records in one place so sections can reference them without repeating course names.
Purpose: Stores information about academic courses or programs. Field | Data Type | Description course_id INT(11) | Primary Key; unique course ID. course_name VARCHAR(50) | Name of the academic course / program.

Important keys:
- Primary Key (PK): course_id
- No foreign key is shown.

B. Section Table (Primary Key + Foreign Key): Stores class section records and links each section to its corresponding academic course.
Purpose: Stores information about class sections and their parent courses. Field | Data Type | Description section_id INT(11) | Primary Key; unique section ID. section_name VARCHAR(50) | Name of the class section. course_id INT(11) | Foreign Key referencing course(course_id).

Important keys:
- Primary Key (PK): section_id
- Foreign Key (FK): course_id references course(course_id).

C. Student Info Table (Primary Key + Foreign Key): Stores student profile details once instead of repeating them in every session log, while linking each student to a class section.
Purpose: Stores student profile information and section assignment. Field | Data Type | Description id VARCHAR(50) | Primary Key; unique student identifier. name VARCHAR(100) | Student full name. section_id INT(11) | Foreign Key referencing section(section_id). date DATE | Date stored with the student entry.

Important keys:
- Primary Key (PK): id
- Foreign Key (FK): section_id references section(section_id).

D. Attendance Record Table (Primary Key + Foreign Key): This is the daily attendance log table of the system. It connects to student_info through id and records each session date and standardized attendance status.
Purpose: Stores daily attendance logs for students across class sessions. Field | Data Type | Description record_id INT(11) | Primary Key; attendance log ID. id VARCHAR(50) | Foreign Key referencing student_info(id). date DATE | Session calendar date. status VARCHAR(20) | Standardized status value: Present, Absent, or Late.

Important keys:
- Primary Key (PK): record_id
- Foreign Key (FK): id references student_info(id).
