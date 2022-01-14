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
        buttons = new ArrayList<>();
        this.vaccineSystem = vaccineSystem;
    }

    public Page() {}

    protected JPanel createLabelledComponentPanel(JComponent component, String inputText) {
//        JPanel panel = new JPanel();
//        panel.setLayout(new GridLayout(0, 2));
//
//        panel.add(new JLabel(inputText));
//        panel.add(component);
//        panel.setMaximumSize(panel.getPreferredSize());
//        return panel;
        JPanel panel = new JPanel();

        panel.add(new JLabel(inputText));
        panel.add(component);
        panel.setMaximumSize(panel.getPreferredSize());
        return panel;
    }

    protected void addButton(JButton button, JPanel panel) {
        button.addActionListener(this);
        panel.add(button);
    }

    protected String getSanatizedtext(JButton button) {
        return button.getText().replace(" ", "").toLowerCase();
    }

    protected void fitPanelToMainPanel(JPanel panel) {
        int width = vaccineSystem.getWidth();
        int height = panel.getMinimumSize().height;
        panel.setMaximumSize(new Dimension(width, height));
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
