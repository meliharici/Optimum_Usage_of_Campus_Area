package Controller;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Configurations {
    public static final String guiConfigPath = "config/guiConfig.txt";
    public static final String systemConfigPath = "config/systemConfig.txt";

    public static final String kroki_path = "resources/kroki.png";
    public static BufferedImage DEFAULT_CAMPUS_MAP;

    public static String selected_data_file_path = "";

    public static int WIDTH = 800;
    public static int HEIGHT = 600;

    public static double MENU_PANEL_WIDTH_RATIO = 0.25;
    public static double MENU_PANEL_HEIGHT_RATIO = 1.0;
    public static double MAP_PANEL_WIDTH_RATIO = 0.75;
    public static double MAP_PANEL_HEIGHT_RATIO = 0.85;
    public static double INFO_PANEL_WIDTH_RATIO = 0.75;
    public static double INFO_PANEL_HEIGHT_RATIO = 0.15;

    public static int SIM_SEMESTER = 201710;
    public static final int SEMESTER_FALL = 201720;
    public static final int SEMESTER_SPRING = 201710;

    public static String COURSE_FILEPATH = "data/Courses.csv";
    public static String STUDENT_FILEPATH = "data/Students.csv";

    public static int POI_FRAME_WIDTH = 400;
    public static int POI_FRAME_HEIGHT = 500;

    public static double SIMULATION_SPEED = 0.3;

    public static void initialize() {
        loadGuiParameters();
        loadSystemParameters();

    }

    private static void loadGuiParameters() {
        try {
            DEFAULT_CAMPUS_MAP = ImageIO.read(new java.io.File(kroki_path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(guiConfigPath));
            String line = br.readLine();

            while(line != null){
                if(line.contains("=")){
                    String[] args = line.split("=");
                    args[0] = args[0].trim();
                    args[1] = args[1].trim();

                    switch(args[0]){
                        case "WIDTH" : WIDTH = Integer.parseInt(args[1]); break;
                        case "HEIGHT" : HEIGHT = Integer.parseInt(args[1]); break;
                        case "MENU_PANEL_WIDTH_RATIO" : MENU_PANEL_WIDTH_RATIO = Double.parseDouble(args[1]); break;
                        case "MENU_PANEL_HEIGHT_RATIO" : MENU_PANEL_HEIGHT_RATIO = Double.parseDouble(args[1]); break;
                        case "MAP_PANEL_WIDTH_RATIO" : MAP_PANEL_WIDTH_RATIO = Double.parseDouble(args[1]); break;
                        case "MAP_PANEL_HEIGHT_RATIO" : MAP_PANEL_HEIGHT_RATIO = Double.parseDouble(args[1]); break;
                        case "INFO_PANEL_WIDTH_RATIO" : INFO_PANEL_WIDTH_RATIO = Double.parseDouble(args[1]); break;
                        case "INFO_PANEL_HEIGHT_RATIO" : INFO_PANEL_HEIGHT_RATIO = Double.parseDouble(args[1]); break;
                    }
                }

                line = br.readLine();
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

    }

    private static void loadSystemParameters(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(systemConfigPath));
            String line = br.readLine();

            while(line != null){
                if(line.contains("=")){
                    String[] args = line.split("=");
                    args[0] = args[0].trim();
                    args[1] = args[1].trim();

                    switch(args[0]){
                        case "SIMULATION_SPEED" : SIMULATION_SPEED = (int) Double.parseDouble(args[1]); break;
                    }
                }
                line = br.readLine();
            }
            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}

