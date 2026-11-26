/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */
public class Student {
    private int recordId;
    private String studentId;
    private String studentName;
    private String courseName;
    private String sectionName;
    private String date;
    private String status;

    public Student() {}

    public Student(String studentId, String studentName, String courseName, String sectionName, String date, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseName = courseName;
        this.sectionName = sectionName;
        this.date = date;
        this.status = status;
    }

    public Student(int recordId, String studentId, String studentName, String courseName, String sectionName, String date, String status) {
        this(studentId, studentName, courseName, sectionName, date, status);
        this.recordId = recordId;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}