import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class AddStocksPage extends AddPage {

    private JComboBox facilityComboBox;
    private JList<String> vaccinesList;

    public AddStocksPage(VaccineSystem vaccineSystem, MainPage mainPage) {
        super(vaccineSystem, mainPage, "Add Factory Stocks:");

        facilityComboBox = new JComboBox(new String[] {"Factory", "Distribution Centre", "Vaccination Centre"});

        inputGridPanel.add(facilityComboBox);

        setMaxWidthMinHeight(inputPanel);
    }

    private void createStatements() {
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createStatements();
        }
        super.actionPerformed(e);
    }
}
