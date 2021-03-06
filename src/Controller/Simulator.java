package Controller;

import GUI.AnalysisViewFiles.AnalysisView;
import Model.CampusTime;
import Model.MapModel.CampusMap;
import Model.Student;

import java.awt.*;
import java.util.Date;

public class Simulator extends Thread {
    public boolean isPaused = false;
    public boolean isFinished = false;

    private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

    public Date startingDate;
    public Date finishingDate;

    public Date simulationStart;
    public Date simulationEnd;

    public CampusTime currentTime;

    double semiTime = 0;

    private static final int SIMULATION_FREQUENCY = 25;

    public Simulator(){

    }

    public void initializeSimulation(CampusTime time){
        this.currentTime = time;

    }

    public void Simulator_OLD(Date startingDate,Date finishingDate){
        this.startingDate = startingDate;
        this.finishingDate = finishingDate;

        simulationStart = startingDate;

    }

    public void run(){
        while(!isFinished){
            if(!isPaused){
                try {
                    StudentController sController = MainController.getInstance().studentController;
                    sController.simulateStudents(currentTime,Configurations.SIMULATION_SPEED);

                    double currentTimeEncoded = Student.getDoubleEncodedTime(currentTime.getHour(),currentTime.getMin());
                    double simRatio = 100;

                    this.semiTime += Configurations.SIMULATION_SPEED*simRatio;
                    if(semiTime>1000){
                        System.out.println(currentTime.hour+":"+currentTime.min);
                        AnalysisView.getInstance().menuPanel.updateSimulationTime(currentTime);
                        int mins = (int)((semiTime+1e-5)/1000);
                        semiTime = semiTime%1000;

                        double currentTimeMins = Student.convertToMins(currentTimeEncoded);
                        currentTimeMins+= mins;

                        double currentTimeNew = Student.convertMinsToEncoded(currentTimeMins);
                        int newHour = (int)Student.getHourFromEncodedTime(currentTimeNew);
                        int newMins = (int)Student.getMinsFromEncodedTime(currentTimeNew);

                        this.currentTime.hour = newHour;
                        this.currentTime.min = newMins;

                    }

                    AnalysisView.getInstance().mapPanel.repaint();
                    sleep(SIMULATION_FREQUENCY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cancel();
    }

    public void run_old(){
        while(!isFinished){
            if(!isPaused){
                long startingTime_inmilisec = simulationStart.getTime();
                simulationEnd = new Date(startingTime_inmilisec + (5 * ONE_MINUTE_IN_MILLIS));

                MainController.getInstance().drawData(simulationStart, simulationEnd);
                //sim start = sim start + 5 dakka

                System.out.println("START : " + simulationStart +  "     ,    END : " + simulationEnd);

                simulationStart = simulationEnd;

                if(!simulationStart.before(finishingDate)){
                    isFinished = true;
                }
                System.out.println(simulationStart.before(finishingDate));
                AnalysisView.getInstance().menuPanel.updateSimulationTime(currentTime);
            }
            else{
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cancel();
    }

    public void cancel(){
        isFinished = true;
    }
}
