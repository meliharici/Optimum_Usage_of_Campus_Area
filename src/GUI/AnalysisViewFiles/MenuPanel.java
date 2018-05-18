
package GUI.AnalysisViewFiles;


import Controller.Configurations;
import Controller.MainController;
import Model.CampusTime;
import Model.MapModel.CampusMap;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MenuPanel extends JPanel {

    private int width,height;
    private GridLayout ly = null;
    Random rand = new Random();
    Color background_color;
    private static int color_r = 43;
    private static int color_g = 63;
    private static int color_b = 83;

    private int separator_thickness = 10;
    private int panel_height,panel_width,top_leading;

    private int generateButtonState = 0;
    private int mapEditorButtonState = 2;
    private int simulateButtonState = 2;

    private static final int BUTTON_GENERATE_STATE = 0;
    private static final int BUTTON_DRAWSKETCH_STATE = 1;

    private static final int BUTTON_SIMULATE_STATE = 2;
    private static final int BUTTON_SIMULATIONSTOP_STATE = 4;
    private static final int BUTTON_SIMULATIONPAUSE_STATE = 5;


    private static final int BUTTON_NORMAL_STAGE = 1;
    private static final int BUTTON_MAP_EDITOR_STAGE = 2;

    ArrayList<MenuButton> buttons;
    MenuButton simulationButton, generateButton, mapEditorButton, mb1;
    String[] hours, minutes;

    // TODO: Clear non-used values
    JDatePickerImpl datePicker, datePicker2;
    JSlider simulationSpeedSlider;



    public MenuPanel(int width, int height){
        super();
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setLayout(null);
        background_color = new Color(color_r, color_g, color_b);
        this.setBackground(background_color);

        buttons = new ArrayList<MenuButton>();
        panel_height = this.height/15;
        panel_width = this.width - this.width/10;
        top_leading = panel_height + percentage(5,this.height); // "Dashboard Label + %5 "
        separator_thickness = this.panel_height/2;
        generateMenu();
    }

    private void generateMenu(){
        // for label panel
        JPanel lp = labelPanel();
        lp.setLocation(0,0);
        lp.setSize(this.width, panel_height);
        lp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
        this.add(lp);
        addMenuButtons();
    }

    private void addMenuButtons(){
        // Time Button
        int numButtons = 0;
        mb1 = new MenuButton(0,top_leading + (numButtons * panel_height) + numButtons*separator_thickness,panel_width,2*panel_height,"");
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday", "Sunday"};
        fillTimeBoxes();
        mb1.addLabel(percentage(5,panel_width), percentage(15,panel_height), percentage(45, panel_width), percentage(30, panel_height), "Days");
        mb1.addCombo(percentage(5,panel_width), percentage(70,panel_height), percentage(45, panel_width), percentage(50, panel_height), days);
        mb1.addLabel(percentage(65,panel_width), percentage(15,panel_height), percentage(45, panel_width), percentage(30, panel_height), "Time");
        mb1.addCombo(percentage(58,panel_width), percentage(70,panel_height), percentage(20, panel_width), percentage(50, panel_height), hours);
        mb1.addCombo(percentage(80,panel_width), percentage(70,panel_height), percentage(20, panel_width), percentage(50, panel_height), minutes);
        mb1.setEnabled(false);

        // Generate Button
        numButtons = 1;
        top_leading += panel_height;
        generateButton = new MenuButton(0,top_leading + (numButtons * panel_height) + numButtons*separator_thickness,panel_width,panel_height,"Generate");
        generateButton.addActionListener(e -> {
            generate();
        });
        generateButton.setVisible(false);

        // Simulate Button
        numButtons = 2;
        simulationButton = new MenuButton(0,top_leading + (numButtons * panel_height) + numButtons*separator_thickness,panel_width,panel_height,"Simulate");
        simulationButton.addActionListener(e -> {
            simulateButtonEvent();
        });

        // CampusMap Editor Button
        numButtons = 3;
        mapEditorButton = new MenuButton(0,top_leading + (numButtons * panel_height) + numButtons*separator_thickness,panel_width,panel_height,"CampusMap Editor");
        mapEditorButton.addActionListener(e -> {
            mapEditor();
        });

        mb1.setFocusable(false);
        generateButton.setFocusable(false);
        simulationButton.setFocusable(false);
        mapEditorButton.setFocusable(false);

        this.add(mb1);
        this.add(generateButton);
        this.add(simulationButton);
        this.add(mapEditorButton);
    }

    private int percentage(int percent, int number){
        return (number*percent)/100;
    }

    private void fillTimeBoxes(){
        // Setting values for Java combo box
        hours = new String[24];
        minutes = new String[60];
        for(int i=0; i < 24; i++)
            hours[i] = String.format("%02d",  i);
        for(int i=0; i < 60; i++)
            minutes[i] = String.format("%02d",  i);
    }

    private void mapEditor(){
        // TODO: Change method name (if better) and implement correspondingly
        MainController controller = MainController.getInstance();


        if(mapEditorButtonState == BUTTON_MAP_EDITOR_STAGE){
            controller.mainView.analysisMenu.mapPanel.setDrawMode(MapPanel.PAINT_MAPEDITOR);
            changeMapEditorButton(BUTTON_NORMAL_STAGE);
            AnalysisView.getInstance().addInformationElements();
            AnalysisView.getInstance().repaint();
        }
        else{
            CampusMap.getCampusMap().saveCampusMap();
            CampusMap.getCampusMap().InitializeNodeConnections();
            controller.mainView.analysisMenu.mapPanel.setDrawMode(MapPanel.PAINT_NORMAL);
            changeMapEditorButton(BUTTON_MAP_EDITOR_STAGE);
            AnalysisView.getInstance().hideInformationElements();
        }

    }

    private void changeMapEditorButton(int state){
        switch (state){
            case BUTTON_MAP_EDITOR_STAGE:
                mapEditorButton.setText("CampusMap Editor");
                mapEditorButtonState = state;
                AnalysisView.getInstance().mapPanel.mapEditorMode = MapPanel.DEFAULT_MODE;
                return;
            case BUTTON_NORMAL_STAGE:
                mapEditorButton.setText("Simulator");
                mapEditorButtonState = state;
                AnalysisView.getInstance().requestFocus();

                return;
        }
    }

    private void changeGenerateButton(int state){
        switch(state){
            case BUTTON_GENERATE_STATE :
                generateButton.setText("Generate");
                generateButtonState = state;
                return;
            case BUTTON_DRAWSKETCH_STATE :
                generateButton.setText("Download Sketch");
                generateButtonState = state;
                return;

        }
    }

    private void changeSimulationButton(int state){
        switch(state){
            case BUTTON_SIMULATE_STATE :
                simulationButton.setText("Simulate");
                simulateButtonState = state;
                return;
            case BUTTON_SIMULATIONSTOP_STATE :
                simulationButton.setText("Cancel Simulation");
                simulateButtonState = state;
                return;
        }
    }

    private void simulateButtonEvent(){
        MainController controller = MainController.getInstance();
        if(simulateButtonState == BUTTON_SIMULATE_STATE){
            //if(checkDateInputs() == false) return;
            changeSimulationButton(BUTTON_SIMULATIONSTOP_STATE);
            simulate();
        }
        else if (simulateButtonState == BUTTON_SIMULATIONSTOP_STATE){
            changeSimulationButton(BUTTON_SIMULATE_STATE);
            cancelSimulation();
        }
    }

    private void simulate(){
        MainController controller = MainController.getInstance();
        controller.studentController.initializeStudents(getTimeOnController());
        MainController.getInstance().startSimulation(getTimeOnController());
        System.out.print("Sim start from simulateButtonEvent()");
    }

    public void updateSimulationTime(CampusTime simulationTime){
        ArrayList<JComboBox> combos = mb1.getCombos();
        int day = simulationTime.getDay() - 1;
        int hour = simulationTime.getHour();
        int min = simulationTime.getMin();
        if(hour >= 0 && hour < 24 && min >= 0 && min < 60 && day >= 0 && day < 7){
            combos.get(0).setSelectedIndex(day); // day
            combos.get(1).setSelectedIndex(hour); //hour
            combos.get(2).setSelectedIndex(min); // min

            combos.get(0).repaint();
            combos.get(1).repaint();
            combos.get(2).repaint();
        }
    }

    private void cancelSimulation(){
        MainController controller = MainController.getInstance();
        controller.cancelSimulation();
        System.out.print("Sim cancel from simulateButtonEvent()");
    }

    private void generate(){
        /*
        if(checkDateInputs() == false){
            return;
        }
        */

        MainController controller = MainController.getInstance();

        if(controller.isSimulationRunning){
            controller.cancelSimulation();
            changeSimulationButton(BUTTON_SIMULATE_STATE);
        }

        if(generateButtonState == BUTTON_DRAWSKETCH_STATE){
            changeGenerateButton(BUTTON_GENERATE_STATE);
            generateSketch();
        }
        else if(generateButtonState == BUTTON_GENERATE_STATE){
            changeGenerateButton(BUTTON_DRAWSKETCH_STATE);

            getTimeOnController();
            controller.drawData();
        }

    }

    private void generateSketch(){
        String filename = "SKETCH.png";
        File outputfile = new File(filename);
        try {
            ImageIO.write(MainController.getInstance().campusData.campusMapImage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private CampusTime getTimeOnController (){
        String day = (String) mb1.combos.get(0).getSelectedItem();
        int hour = Integer.valueOf(((String) mb1.combos.get(1).getSelectedItem()));
        int min = Integer.valueOf(((String) mb1.combos.get(2).getSelectedItem()));
        return new CampusTime(hour, min, day);
    }


    // TODO: Delete below lines if not required
    // Bu alttakinin artık geçerliliği yok.
    /*
    private void setTimeOnController(){
        MainController controller = MainController.getInstance();

        Date startingDate = (Date) datePicker.getModel().getValue();
        Date finishingDate = (Date) datePicker2.getModel().getValue();

        int startingHour  = Integer.valueOf(((String) starting_hour_combo.getSelectedItem()));
        int startingMin   = Integer.valueOf(((String) starting_min_combo.getSelectedItem()));
        int finishingHour = Integer.valueOf(((String) finishing_hour_combo.getSelectedItem()));
        int finishingMin = Integer.valueOf(((String) finishing_min_combo.getSelectedItem()));

        // Adjusting hour and minute values
        Calendar cal = Calendar.getInstance();
        cal.setTime(startingDate);
        cal.set(Calendar.HOUR_OF_DAY, startingHour);
        cal.set(Calendar.MINUTE, startingMin);
        cal.set(Calendar.SECOND, 0);
        startingDate = cal.getTime();
        cal.setTime(finishingDate);
        cal.set(Calendar.HOUR_OF_DAY, finishingHour);
        cal.set(Calendar.MINUTE, finishingMin);
        cal.set(Calendar.SECOND, 0);
        finishingDate = cal.getTime();

        System.out.println("Starting: " + startingDate.toString() + "   Finishing: " + finishingDate.toString());
        controller.startingDate = startingDate;
        controller.finishingDate = finishingDate;
    }
    */


    //TODO: Delete below function if not required
    private boolean checkDateInputs(){
        Date startingDate = (Date) datePicker.getModel().getValue();
        Date finishingDate = (Date) datePicker2.getModel().getValue();

        if(startingDate == null || finishingDate == null){
            System.out.println("Enter a valid date!");
            return false;
        }
        return true;
    }

    private JPanel labelPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(background_color);
        JLabel label = new JLabel("Dashboard");
        label.setFont(new Font("Santa Fe LET", Font.BOLD, this.width/10));
        label.setForeground(Color.WHITE);
        label.setLocation((this.width/5),(panel_height/50));
        label.setSize((this.width- this.width/10),panel_height);

        panel.add(label);
        return panel;
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String path = (f.getAbsolutePath()).replace('\\','/');
            System.out.println("selected path: " + path);
            Configurations.selected_data_file_path = path;
        } else {
            // do nothing
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

