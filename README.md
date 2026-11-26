<img width="482" height="263" alt="Screenshot 2026-11-24 184420" src="https://github.com/user-attachments/assets/8e919b17-6702-439c-a920-ffe1b49df474" />

CREATE DATABASE IF NOT EXISTS attendance_db;
USE attendance_db;
CREATE TABLE IF NOT EXISTS course (
  course_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  course_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS section (
  section_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  section_name VARCHAR(50) NOT NULL,
  course_id INT(11) NOT NULL,
  CONSTRAINT fk_section_course
    FOREIGN KEY (course_id) REFERENCES course(course_id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS student_info (
  id VARCHAR(50) NOT NULL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  section_id INT(11) NOT NULL,
  date DATE DEFAULT NULL,
  CONSTRAINT fk_student_section
    FOREIGN KEY (section_id) REFERENCES section(section_id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS attendance_record (
  record_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id VARCHAR(50) NOT NULL,
  date DATE NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_attendance_student
    FOREIGN KEY (id) REFERENCES student_info(id)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

