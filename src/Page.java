import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Page implements ActionListener {

    protected JPanel mainPanel;
    protected VaccineSystem vaccineSystem;

    protected ArrayList<JButton> buttons;
    protected String buttonAction;

    public Page(VaccineSystem vaccineSystem) {
        mainPanel = new JPanel();
        buttons = new ArrayList<JButton>();
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

    public void addButton(JButton button, JPanel panel) {
        button.addActionListener(this);
        panel.add(button);
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
