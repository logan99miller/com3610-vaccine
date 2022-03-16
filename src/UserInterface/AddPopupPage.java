package UserInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddPopupPage extends AddPage {

    protected JFrame frame;
    protected int frameWidth;

    public AddPopupPage(JFrame frame, int frameWidth) {
        this.frame = frame;
        this.frameWidth = frameWidth;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        initializePage("Lifespans:");
    }

    public AddPopupPage(JFrame frame) {
        this(frame, 0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            System.out.println("Hello world");
            frame.setVisible(false);
        }
    }

}
