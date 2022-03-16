package UserInterface;

import javax.swing.*;
import java.util.ArrayList;

public class Utils {

    public static void errorMessage(String message, boolean displayError) {
        if (displayError) {
            errorMessage(message);
        }
    }
    public static void errorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static JPanel addLabelledComponent(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    public static ListModel ArrayListToListModel(ArrayList<String> arrayList) {
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (Object listItem : arrayList) {
            listModel.addElement((String) listItem);
        }
        return (listModel);
    }

}
