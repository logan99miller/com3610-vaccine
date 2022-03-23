/**
 * The parent class of all add pop-up pages, which are used when the table being added to need's an unknown amount of records
 * to be added to other tables linked to the table (e.g. when adding lifespan values to a vaccine, we do not initially know
 * how many temperature ranges the lifespan will be different for until the user tells us). The pop-up page is generated
 * after the user tells us the amount of records that will need to be added.
 */
package UserInterface.AddPopupPages;

import UserInterface.AddPages.AddPage;

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
            frame.setVisible(false);
        }
    }

}
