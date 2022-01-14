import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SelectTablePage extends Page {

    protected MainPage mainPage;

    protected JButton vaccineButton, personButton, medicalConditionButton, manufacturerButton, factoryButton;
    protected JButton transporterButton, transportLocationButton, distributionCentreButton, vaccinationCentreButton, bookingButton;


    public SelectTablePage(VaccineSystem vaccineSystem, MainPage mainPage, String buttonAction) {
        super(vaccineSystem);
        this.mainPage = mainPage;
        this.buttonAction = buttonAction;

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel(buttonAction + ":");
        mainPanel.add(pageTitle);

        vaccineButton = new JButton("Vaccine");
        personButton = new JButton("Person");
        medicalConditionButton = new JButton("Medical Condition");
        manufacturerButton = new JButton("Manufacturer");
        factoryButton = new JButton("Factory");
        transporterButton = new JButton("Transport Provider");
        transportLocationButton = new JButton("Transport Location");
        distributionCentreButton = new JButton("Distribution Centre");
        vaccinationCentreButton = new JButton("Vaccination Centre");
        bookingButton = new JButton("Booking");

        buttons.add(vaccineButton);
        buttons.add(personButton);
        buttons.add(medicalConditionButton);
        buttons.add(manufacturerButton);
        buttons.add(factoryButton);
        buttons.add(transporterButton);
        buttons.add(transportLocationButton);
        buttons.add(distributionCentreButton);
        buttons.add(vaccinationCentreButton);
        buttons.add(bookingButton);

        for (JButton button : buttons) {
            addButton(button, mainPanel);
        }
    }

    public void actionPerformed(ActionEvent e) {
        for (JButton button : buttons) {
            if (e.getSource() == button) {
                mainPage.setPageName(buttonAction + getSanatizedtext(button));
                mainPage.updatePage();
            }
        }
    }
}
