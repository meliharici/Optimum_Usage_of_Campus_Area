package GUI.AnalysisViewFiles;

import Controller.Configurations;
import Model.MapModel.PointOfInterest;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;

public class PoiPanel extends JPanel {
    PointOfInterest poi = null;
    JFrame poiFrame = null;

    JTextField poiName;

    JFormattedTextField sizeField;
    JFormattedTextField penaltyParemeterField;
    JFormattedTextField preferanceParameterField;
    JComboBox<String> typeDropdown;

    double lengthPenaltyParameter = 0;
    double preferanceParameter = 0;

    public PoiPanel(PointOfInterest poi){
        super();
        createFrame();
        createPanel();
        this.poiFrame.add(this);
        this.poiFrame.setVisible(true);
        this.poi = poi;
        loadData();
    }

    private void createPanel() {
        BoxLayout boxLayout = new BoxLayout(this,1);
        this.setLayout(boxLayout);

        initializeNameField();
        initializeSizeField();
        initializePenaltyField();
        initializePreferanceField();
        initializeTypeDropDown();
        initializeButtons();
    }

    public void loadData(){
        this.preferanceParameterField.setValue(poi.preferance*100);
        this.penaltyParemeterField.setValue(poi.distancePenalty*100);
        this.sizeField.setValue(poi.size);
        this.poiName.setText(poi.name);
        this.typeDropdown.setSelectedIndex(poi.type);
    }

    public void createFrame(){
        this.poiFrame = new JFrame("Point of Interest");
        this.poiFrame.setSize(Configurations.POI_FRAME_WIDTH,Configurations.POI_FRAME_HEIGHT);
    }

    public JPanel getDefaultContentPanel(){
        JPanel panel = new JPanel();
        panel.setSize(Configurations.POI_FRAME_WIDTH,Configurations.POI_FRAME_HEIGHT/5);

        BoxLayout boxLayout = new BoxLayout(panel,0);
        panel.setLayout(boxLayout);
        panel.setBackground(Color.lightGray);

        return panel;
    }

    public JFormattedTextField getNumberOnlyTextField(double maxValue){
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatterAdvanced formatter = new NumberFormatterAdvanced(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(new Integer(0));
        formatter.setMaximum(new Integer(100));
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField field = new JFormattedTextField(formatter);

        return field;
    }

    public void initializeNameField(){
        JPanel namePanel = getDefaultContentPanel();

        JLabel nameLabel = new JLabel("Enter POI Name : ");
        namePanel.add(nameLabel);

        poiName = new JTextField();
        namePanel.add(poiName);

        this.add(namePanel);


    }

    public void initializeSizeField(){
        JPanel sizePanel = getDefaultContentPanel();

        JLabel sizeLabel = new JLabel("Enter size of POI : ");
        sizePanel.add(sizeLabel);

        sizeField = getNumberOnlyTextField(15);
        sizePanel.add(sizeField);

        this.add(sizePanel);

    }

    public void initializePenaltyField(){
        JPanel penaltyPanel = getDefaultContentPanel();

        JLabel penaltyLabel = new JLabel("Enter distance-penalty parameter : ");
        penaltyPanel.add(penaltyLabel);

        penaltyParemeterField = getNumberOnlyTextField(100);
        penaltyPanel.add(penaltyParemeterField);

        this.add(penaltyPanel);

    }

    public void initializePreferanceField(){
        JPanel preferancePanel = getDefaultContentPanel();

        JLabel preferanceLabel = new JLabel("Enter desirability parameter : ");
        preferancePanel.add(preferanceLabel);

        preferanceParameterField = getNumberOnlyTextField(100);
        preferancePanel.add(preferanceParameterField);

        this.add(preferancePanel);

    }

    public void initializeTypeDropDown(){
        JPanel typePanel = getDefaultContentPanel();

        JLabel typeLabel = new JLabel("Select type of POI");
        typePanel.add(typeLabel);

        String[] names = {"Building","Freezone","Food-Main","Food-Snack","Enterance","Study-Area"};
        typeDropdown = new JComboBox<>(names);
        typePanel.add(typeDropdown);

        this.add(typePanel);

    }

    public void initializeButtons(){
        JPanel buttonPanel = getDefaultContentPanel();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                poiFrame.dispatchEvent(new WindowEvent(poiFrame,WindowEvent.WINDOW_CLOSING));
            }
        });
        buttonPanel.add(closeButton);

        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChanges();
                poiFrame.dispatchEvent(new WindowEvent(poiFrame,WindowEvent.WINDOW_CLOSING));
            }
        });
        buttonPanel.add(applyButton);

        this.add(buttonPanel);

    }

    public void saveChanges(){
        int preferanceParameterFieldValue = (int)(Double.parseDouble(preferanceParameterField.getValue().toString()));
        poi.preferance = preferanceParameterFieldValue/100.0;

        int distancePenaltyFieldValue = (int)Double.parseDouble(penaltyParemeterField.getValue().toString());
        poi.distancePenalty = distancePenaltyFieldValue/100.0;

        poi.size = (int) sizeField.getValue();
        poi.name = poiName.getText();
        poi.type = typeDropdown.getSelectedIndex();
    }


}

class PoiPanelMouseHandler extends MouseAdapter{

}

class NumberFormatterAdvanced extends NumberFormatter{
    public NumberFormatterAdvanced(NumberFormat format){
        super(format);
    }

    public Integer stringToValue(String string)
            throws ParseException {
        if (string == null || string.length() == 0) {
            return null;
        }
        return (Integer) super.stringToValue(string);
    }
}
