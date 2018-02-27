package Controller;
import javax.imageio.ImageIO;

import GUI.*;
import Model.CampusData;
import Model.DataReader;
import Model.LocationData;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainController {
    public Date startingDate;
    public Date finishingDate;

    public Timer simulationThread = null;
    public Simulator simulator = null;

    public boolean isSimulationRunning = false;
    public boolean isSketchGenerated = false;

    public CampusData campusData;

    Date simulation_start;
    Date simulation_end;

    private static MainController controller;

    private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

    public static void createInstance(){
        controller = new MainController();
    }

    public static MainController getInstance(){
        return controller;
    }

    public MainView mainView;
    public MenuController menuController;

    private MainController(){
        mainView = new MainView();
        this.menuController = new MenuController();
    }

    public void initialize(){
        mainView.initialize();
    }

    public void start() {
        try {
            BufferedImage campusImage = (ImageIO.read(new File(Configurations.kroki_path)));
            campusData = new CampusData();
            campusData.campusMapImage = campusImage;

            mainView.setCampusData(campusData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public  void startSimulation(){
        simulator = new Simulator(startingDate,finishingDate);
        simulator.start();
        isSimulationRunning = true;

    }

    public void startSimulation2(){
        if(startingDate == null){
            System.out.println("");
        }
        else{
            simulation_start = startingDate;

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    long startingTime_inmilisec = simulation_start.getTime();
                    simulation_end = new Date(startingTime_inmilisec + (5 * ONE_MINUTE_IN_MILLIS));

                    drawData(simulation_start, simulation_end);
                    //sim start = sim start + 5 dakka

                    System.out.println("START : " + simulation_start +  "     ,    END : " + simulation_end);

                    simulation_start = simulation_end;

                    if(!simulation_start.before(finishingDate)){
                        cancel();
                    }
                    System.out.println(simulation_start.before(finishingDate));
                }
            };

            simulationThread = new Timer();
            simulationThread.scheduleAtFixedRate(task,0,Configurations.SIMULATION_SPEED);
            isSimulationRunning = true;


        }
    }

    public void cancelSimulation(){
        System.out.print("Simulation Cancelled");
        isSimulationRunning = false;
        if(simulator == null) return;
        simulator.cancel();
        simulator = null;
    }

    public void cancelSimulation2(){
        System.out.print("Simulation Cancelled");
        if(simulationThread == null) return;
        simulationThread.cancel();
        simulationThread.purge();
        drawData(startingDate, finishingDate);
        isSimulationRunning = false;
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public void drawData(Date startingDate, Date finishingDate) {

        CampusData campusData = new CampusData();
        try {
            campusData.campusMapImage = ImageIO.read(new File(Configurations.kroki_path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataReader reader = new DataReader();
        ArrayList<LocationData> locs = reader.locationsBetween(Configurations.selected_data_file_path,startingDate,finishingDate);

        campusData.data = locs;
        this.mainView.setCampusData(campusData);
    }

    public void drawData(){
        drawData(startingDate,finishingDate);
    }
}

