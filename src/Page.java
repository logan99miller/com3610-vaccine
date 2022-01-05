import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Page implements ActionListener {

    protected JPanel mainPanel;
    protected VaccineSystem vaccineSystem;

    public Page(VaccineSystem vaccineSystem) {
        mainPanel = new JPanel();
        this.vaccineSystem = vaccineSystem;
    }

    JPanel createLabelledComponentPanel(JComponent component, String inputText) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        panel.add(new JLabel(inputText));
        panel.add(component);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
