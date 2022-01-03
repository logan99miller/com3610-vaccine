import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VaccineSystem extends JFrame implements ActionListener {

    public static void main(String[] args) {
        VaccineSystem vaccineSystem = new VaccineSystem("Vaccine System");

        final int WINDOW_WIDTH = 700;
        final int WINDOW_HEIGHT = 700;

        vaccineSystem.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        vaccineSystem.setLocationRelativeTo(null); // Sets window to centre of screen
        vaccineSystem.setVisible(true);
    }

    public VaccineSystem(String titleBarText) {
        super(titleBarText);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
