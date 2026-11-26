/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author HP
 */

import java.time.LocalDate;
import View.view;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Student;
import model.StudentDAO;

public class Controller {
    private final view gui;
    private final StudentDAO dao;
    private final List<Integer> recordIds = new ArrayList<>();
    private Integer selectedRecordId = null;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public Controller(view gui, StudentDAO dao) {
        this.gui = gui;
        this.dao = dao;

        initListeners();
        loadTableData();setTodayDate();
    }

    private void setTodayDate() {
        gui.getTxtDate().setText(LocalDate.now().toString());
    }

    private void initListeners() {
        // CRUD & Form Buttons
        gui.getBtnRecord().addActionListener(e -> recordAttendance());
        gui.getBtnUpdate().addActionListener(e -> updateAttendance());
        gui.getBtnDelete().addActionListener(e -> deleteAttendance());
        gui.getBtnClear().addActionListener(e -> clearFields());

        gui.getBtnCancel().addActionListener(e -> {
            clearFields();
            gui.getTblAttendance().clearSelection();
        });

        // Refresh & Search
        gui.getBtnRefresh().addActionListener(e -> {
            gui.getTxtSearch().setText("");
            if (rowSorter != null) {
                rowSorter.setRowFilter(null);
            }
            loadTableData();
        });

        gui.getBtnSearch().addActionListener(e -> filterTable());

        gui.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        // Row Click -> Populate Form Fields
        gui.getTblAttendance().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = gui.getTblAttendance().getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = gui.getTblAttendance().convertRowIndexToModel(selectedRow);
                    DefaultTableModel model = (DefaultTableModel) gui.getTblAttendance().getModel();

                    selectedRecordId = recordIds.get(modelRow);
                    gui.getTxtStudentId().setText(model.getValueAt(modelRow, 0).toString());
                    gui.getTxtStudentName().setText(model.getValueAt(modelRow, 1).toString());
                    gui.getTxtCourse().setText(model.getValueAt(modelRow, 2).toString());
                    gui.getTxtSection().setText(model.getValueAt(modelRow, 3).toString());
                    gui.getTxtDate().setText(model.getValueAt(modelRow, 4).toString());

                    String status = model.getValueAt(modelRow, 5).toString();
                    if ("Present".equalsIgnoreCase(status)) {
                        gui.getRbPresent().setSelected(true);
                    } else if ("Absent".equalsIgnoreCase(status)) {
                        gui.getRbAbsent().setSelected(true);
                    } else if ("Late".equalsIgnoreCase(status)) {
                        gui.getRbLate().setSelected(true);
                    }
                }
            }
        });
    }

    private String getSelectedStatus() {
        if (gui.getRbPresent().isSelected()) return "Present";
        if (gui.getRbAbsent().isSelected()) return "Absent";
        if (gui.getRbLate().isSelected()) return "Late";
        return "";
    }

    private boolean validateFormInputs(String id, String name, String course, String section, String date, String status) {
        if (id.isEmpty() || name.isEmpty() || course.isEmpty() || section.isEmpty() || date.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(
                gui,
                "Please fill in all fields (Date as YYYY-MM-DD) and select a status.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    private void recordAttendance() {
        String id = gui.getTxtStudentId().getText().trim();
        String name = gui.getTxtStudentName().getText().trim();
        String course = gui.getTxtCourse().getText().trim();
        String section = gui.getTxtSection().getText().trim();
        String date = gui.getTxtDate().getText().trim();
        String status = getSelectedStatus();

        if (!validateFormInputs(id, name, course, section, date, status)) {
            return;
        }

        Student student = new Student(id, name, course, section, date, status);
        if (dao.addAttendance(student)) {
            JOptionPane.showMessageDialog(gui, "Attendance recorded successfully!");
            loadTableData();
            clearFields();
        }
    }

    private void updateAttendance() {
        if (selectedRecordId == null) {
            JOptionPane.showMessageDialog(
                gui,
                "Please select a record from the table first to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String id = gui.getTxtStudentId().getText().trim();
        String name = gui.getTxtStudentName().getText().trim();
        String course = gui.getTxtCourse().getText().trim();
        String section = gui.getTxtSection().getText().trim();
        String date = gui.getTxtDate().getText().trim();
        String status = getSelectedStatus();

        if (!validateFormInputs(id, name, course, section, date, status)) {
            return;
        }

        Student student = new Student(selectedRecordId, id, name, course, section, date, status);
        if (dao.updateAttendance(student)) {
            JOptionPane.showMessageDialog(gui, "Attendance record updated successfully!");
            loadTableData();
            clearFields();
        }
    }

    private void deleteAttendance() {
        if (selectedRecordId == null) {
            JOptionPane.showMessageDialog(
                gui,
                "Please select a record from the table to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            gui,
            "Are you sure you want to delete this attendance record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteAttendance(selectedRecordId)) {
                JOptionPane.showMessageDialog(gui, "Attendance record deleted!");
                loadTableData();
                clearFields();
            }
        }
    }

    public void loadTableData() {
        DefaultTableModel model = (DefaultTableModel) gui.getTblAttendance().getModel();
        model.setRowCount(0);
        recordIds.clear();

        List<Student> records = dao.getAllAttendanceRecords();
        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;

        for (Student s : records) {
            recordIds.add(s.getRecordId());
            model.addRow(new Object[]{
                s.getStudentId(),
                s.getStudentName(),
                s.getCourseName(),
                s.getSectionName(),
                s.getDate(),
                s.getStatus()
            });

            if ("Present".equalsIgnoreCase(s.getStatus())) {
                presentCount++;
            } else if ("Absent".equalsIgnoreCase(s.getStatus())) {
                absentCount++;
            } else if ("Late".equalsIgnoreCase(s.getStatus())) {
                lateCount++;
            }
        }

        gui.getTxtTotalStudent().setText(String.valueOf(records.size()));
        gui.getTxtPresentCount().setText(String.valueOf(presentCount));
        gui.getTxtAbsentCount().setText(String.valueOf(absentCount));
        gui.getTxtLateCount().setText(String.valueOf(lateCount));
        gui.getBtnCancel().addActionListener(e -> {
            gui.dispose();
        });
        rowSorter = new TableRowSorter<>(model);
        gui.getTblAttendance().setRowSorter(rowSorter);

        // Re-apply active search filter if text is present
        filterTable();
    }

    private void filterTable() {
        String keyword = gui.getTxtSearch().getText().trim();
        if (rowSorter != null) {
            if (keyword.isEmpty()) {
                rowSorter.setRowFilter(null);
            } else {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
            }
        }
    }

    private void clearFields() {
        selectedRecordId = null;
        gui.getTxtStudentId().setText("");
        gui.getTxtStudentName().setText("");
        gui.getTxtCourse().setText("");
        gui.getTxtSection().setText("");
    }
}