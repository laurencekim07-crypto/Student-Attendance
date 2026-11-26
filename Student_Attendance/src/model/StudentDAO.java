/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author HP
 */

import config.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class StudentDAO {

    private int getOrCreateCourseId(Connection conn, String courseName) throws SQLException {
        String selectSql = "SELECT course_id FROM course WHERE course_name = ?";
        try (PreparedStatement pst = conn.prepareStatement(selectSql)) {
            pst.setString(1, courseName);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("course_id");
                }
            }
        }

        String insertSql = "INSERT INTO course (course_name) VALUES (?)";
        try (PreparedStatement pst = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, courseName);
            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Unable to resolve course_id for: " + courseName);
    }

    private int getOrCreateSectionId(Connection conn, String sectionName, int courseId) throws SQLException {
        String selectSql = "SELECT section_id FROM section WHERE section_name = ? AND course_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(selectSql)) {
            pst.setString(1, sectionName);
            pst.setInt(2, courseId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("section_id");
                }
            }
        }

        String insertSql = "INSERT INTO section (section_name, course_id) VALUES (?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, sectionName);
            pst.setInt(2, courseId);
            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Unable to resolve section_id for: " + sectionName);
    }

    private void upsertStudentInfo(Connection conn, String studentId, String studentName, int sectionId, String date) throws SQLException {
        String sql = "INSERT INTO student_info (id, name, section_id, date) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = VALUES(name), section_id = VALUES(section_id), date = VALUES(date)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, studentId);
            pst.setString(2, studentName);
            pst.setInt(3, sectionId);
            pst.setDate(4, Date.valueOf(date)); // Expected format: YYYY-MM-DD
            pst.executeUpdate();
        }
    }

    public boolean addAttendance(Student s) {
        String insertAttendanceSql = "INSERT INTO attendance_record (id, date, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                int courseId = getOrCreateCourseId(conn, s.getCourseName());
                int sectionId = getOrCreateSectionId(conn, s.getSectionName(), courseId);
                upsertStudentInfo(conn, s.getStudentId(), s.getStudentName(), sectionId, s.getDate());

                try (PreparedStatement pst = conn.prepareStatement(insertAttendanceSql)) {
                    pst.setString(1, s.getStudentId());
                    pst.setDate(2, Date.valueOf(s.getDate()));
                    pst.setString(3, s.getStatus());
                    pst.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException | IllegalArgumentException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Record Error (use YYYY-MM-DD for Date): " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Transaction Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAttendance(Student s) {
        String updateRecordSql = "UPDATE attendance_record SET date = ?, status = ? WHERE record_id = ?";
        try (Connection conn = DBConnector.getConnection()) {
            if (conn == null) return false;
            conn.setAutoCommit(false);

            try {
                int courseId = getOrCreateCourseId(conn, s.getCourseName());
                int sectionId = getOrCreateSectionId(conn, s.getSectionName(), courseId);
                upsertStudentInfo(conn, s.getStudentId(), s.getStudentName(), sectionId, s.getDate());

                try (PreparedStatement pst = conn.prepareStatement(updateRecordSql)) {
                    pst.setDate(1, Date.valueOf(s.getDate()));
                    pst.setString(2, s.getStatus());
                    pst.setInt(3, s.getRecordId());
                    int updated = pst.executeUpdate();
                    conn.commit();
                    return updated > 0;
                }
            } catch (SQLException | IllegalArgumentException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Update Error (use YYYY-MM-DD for Date): " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Update Transaction Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAttendance(int recordId) {
        String sql = "DELETE FROM attendance_record WHERE record_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, recordId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Delete Error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getAllAttendanceRecords() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT ar.record_id, si.id, si.name, c.course_name, sec.section_name, ar.date, ar.status " +
                     "FROM attendance_record ar " +
                     "JOIN student_info si ON ar.id = si.id " +
                     "JOIN section sec ON si.section_id = sec.section_id " +
                     "JOIN course c ON sec.course_id = c.course_id " +
                     "ORDER BY ar.record_id DESC";

        try (Connection conn = DBConnector.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(
                    rs.getInt("record_id"),
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("course_name"),
                    rs.getString("section_name"),
                    rs.getDate("date").toString(),
                    rs.getString("status")
                );
                list.add(s);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Load Error: " + e.getMessage());
        }
        return list;
    }
}