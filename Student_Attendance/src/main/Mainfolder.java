/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

/**
 *
 * @author HP
 */
import View.view;
import controller.Controller;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.StudentDAO;

public class Mainfolder {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default Swing look and feel if unavailable
            }

            view gui = new view();
            StudentDAO dao = new StudentDAO();
            new Controller(gui, dao);

            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}