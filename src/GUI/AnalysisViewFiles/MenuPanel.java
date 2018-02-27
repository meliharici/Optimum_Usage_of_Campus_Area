
package GUI.AnalysisViewFiles;


import Controller.Configurations;
import Controller.MainController;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import sun.applet.Main;

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
    private int simulateButtonState = 2;

    private static final int BUTTON_GENERATE_STATE = 0;
    private static final int BUTTON_DRAWSKETCH_STATE = 1;

    private static final int BUTTON_SIMULATE_STATE = 2;
    private static final int BUTTON_SIMULATIONSTOP_STATE = 4;
    private static final int BUTTON_SIMULATIONPAUSE_STATE = 5;


    private JComboBox<String> starting_hour_combo, starting_min_combo, finishing_hour_combo, finishing_min_combo;
    JDatePickerImpl datePicker, datePicker2;

    ArrayList<JPanel> panels;

    JButton simulationButton, generateButton;
    JSlider simulationSpeedSlider;



    public MenuPanel(int width, int height){
        super();
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setLayout(null);
        background_color = new Color(color_r, color_g, color_b);
        this.setBackground(background_color);

        panels = new ArrayList<>();// all panels except the label panel
        panel_height = this.height/15;
        panel_width = this.width - this.width/10;
        top_leading = this.height/15;
        separator_thickness = this.panel_height/2;

        addPanels();
    }

    private void addPanels(){
        // for label panel
        JPanel lp = labelPanel();
        lp.setLocation(0,0);
        lp.setSize(this.width, panel_height);
        lp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.white));
        this.add(lp);

        // creating inner panels
        addloadDataPanel(); // i=0
        addTimePanel();     // i=1
        addGeneratePanel(); // i=2
        addSimulationPanel(); // i=3

        // putting inner panels into the menu panel
        for(int i = 0; i < panels.size(); i++){
                if(i > 1)   // panels after TimePanel
                    panels.get(i).setLocation(0,  (top_leading + ((i+4) * panel_height) + i*separator_thickness));
                else
                    panels.get(i).setLocation(0,  (top_leading + ((i+1) * panel_height) + i*separator_thickness)); // (+1 panel_height because of TimePanel)

                if(i == 1)  // TimePanel
                    panels.get(i).setSize(panel_width, 4*panel_height);
                else
                    panels.get(i).setSize(panel_width, panel_height);

            panels.get(i).setBackground(Color.lightGray);
            panels.get(i).setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            this.add(panels.get(i));
        }
    }

    private void addTimePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(background_color);

        // Time Panel GUI parameters
        int time_panel_height = 4*panel_height;
        int height_middle = time_panel_height/2;
        int leading_x_dates = panel_width/100; // %1 of panel width
        int leading_x_hours = (panel_width*56)/100; // %56 of panel width

        // Setting values for Java combo box
        String[] hours = new String[24];
        String[] minutes = new String[60];

        for(int i=0; i < 24; i++)
            hours[i] = String.format("%02d",  i);
        for(int i=0; i < 60; i++)
            minutes[i] = String.format("%02d",  i);


        // creating date models for calendar panel
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model);

        // Starting Date Components
        JLabel startingDateLabel = new JLabel("Starting Date");
        startingDateLabel.setLocation(leading_x_dates,time_panel_height/10);
        startingDateLabel.setSize((panel_width * 50)/100,time_panel_height/10);
        panel.add(startingDateLabel);

        JLabel startingHourLabel = new JLabel("Hour");
        startingHourLabel.setLocation(leading_x_hours,time_panel_height/10);
        startingHourLabel.setSize((panel_width * 20)/100,time_panel_height/10);
        panel.add(startingHourLabel);

        starting_hour_combo = new JComboBox<String>(hours);
        starting_hour_combo.setLocation(leading_x_hours,time_panel_height/4);
        starting_hour_combo.setSize((panel_width*20)/100,(time_panel_height*15)/100);
        panel.add(starting_hour_combo);

        JLabel startingMinLabel = new JLabel("Min.");
        startingMinLabel.setLocation((panel_width*80)/100,time_panel_height/10);
        startingMinLabel.setSize((panel_width * 20)/100,time_panel_height/10);
        panel.add(startingMinLabel);

        starting_min_combo = new JComboBox<String>(minutes);
        starting_min_combo.setLocation((panel_width*80)/100,time_panel_height/4);
        starting_min_combo.setSize((panel_width*20)/100,(time_panel_height*15)/100);
        panel.add(starting_min_combo);

        datePicker = new JDatePickerImpl(datePanel);
        datePicker.setBounds(leading_x_dates, time_panel_height/4, (panel_width*50)/100, (time_panel_height*15)/100);
        datePicker.setBackground(Color.lightGray);
        panel.add(datePicker);


        // Finishing Date Components
        JLabel finishingDateLabel = new JLabel("Finishing Date");
        finishingDateLabel.setLocation(leading_x_dates,height_middle + time_panel_height/10);
        finishingDateLabel.setSize((panel_width * 50)/100,time_panel_height/10);
        panel.add(finishingDateLabel);

        JLabel finishingHourLabel = new JLabel("Hour");
        finishingHourLabel.setLocation(leading_x_hours,height_middle + time_panel_height/10);
        finishingHourLabel.setSize((panel_width * 20)/100,time_panel_height/10);
        panel.add(finishingHourLabel);

        finishing_hour_combo = new JComboBox<String>(hours);
        finishing_hour_combo.setLocation(leading_x_hours,height_middle + time_panel_height/4);
        finishing_hour_combo.setSize((panel_width*20)/100,(time_panel_height*15)/100);
        panel.add(finishing_hour_combo);

        JLabel finishingMinLabel = new JLabel("Min.");
        finishingMinLabel.setLocation((panel_width*80)/100,height_middle + time_panel_height/10);
        finishingMinLabel.setSize((panel_width * 20)/100,time_panel_height/10);
        panel.add(finishingMinLabel);

        finishing_min_combo = new JComboBox<String>(minutes);
        finishing_min_combo.setLocation((panel_width*80)/100,height_middle + time_panel_height/4);
        finishing_min_combo.setSize((panel_width*20)/100,(time_panel_height*15)/100);
        panel.add(finishing_min_combo);

        UtilDateModel model2 = new UtilDateModel();
        JDatePanelImpl datePanel2 = new JDatePanelImpl(model2);
        datePicker2 = new JDatePickerImpl(datePanel2);
        datePicker2.setBounds(leading_x_dates, height_middle + time_panel_height/4, (panel_width*50)/100, (time_panel_height*15)/100);
        datePicker2.setBackground(Color.lightGray);
        panel.add(datePicker2);

        panels.add(panel);
    }

    private void addGeneratePanel(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(background_color);
        generateButton = new JButton("Generate");
        generateButton.setFont(new Font("Santa Fe LET", Font.PLAIN, this.width/20));
        generateButton.setLocation(0,0);
        generateButton.setSize(panel_width,panel_height);
        generateButton.setBackground(Color.lightGray);
        generateButton.addActionListener(e -> {
            generate();
        });
        panel.add(generateButton);
        panels.add(panel);
    }

    private void addSimulationPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(background_color);
        simulationButton = new JButton("Simulate");
        simulationButton.setFont(new Font("Santa Fe LET", Font.PLAIN, this.width/20));
        simulationButton.setLocation(0,0);
        simulationButton.setSize(panel_width,panel_height);
        simulationButton.setBackground(Color.lightGray);
        simulationButton.addActionListener(e -> {
            simulateButtonEvent();
        });
        panel.add(simulationButton);
        panels.add(panel);
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
            if(checkDateInputs() == false) return;
            changeSimulationButton(BUTTON_SIMULATIONSTOP_STATE);
            simulate();
        }
        else if (simulateButtonState == BUTTON_SIMULATIONSTOP_STATE){
            changeSimulationButton(BUTTON_SIMULATE_STATE);
            cancelSimulation();
        }
    }

    private void simulate(){
        setTimeOnController();
        MainController.getInstance().startSimulation();
        System.out.print("Sim start from simulateButtonEvent()");
    }

    private void cancelSimulation(){
        MainController controller = MainController.getInstance();
        controller.cancelSimulation();
        controller.drawData();
        System.out.print("Sim cancel from simulateButtonEvent()");
    }

    private void generate(){
        if(checkDateInputs() == false){
            return;
        }

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

            setTimeOnController();
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

    private void addloadDataPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(background_color);
        JButton button = new JButton("Load the data");
        button.setFont(new Font("Santa Fe LET", Font.PLAIN, this.width/20));
        button.setLocation(0,0);
        button.setSize(panel_width,panel_height);
        button.setBackground(Color.lightGray);
        button.addActionListener(e -> {
            chooseFile();
        });
        panel.add(button);
        panels.add(panel);
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

