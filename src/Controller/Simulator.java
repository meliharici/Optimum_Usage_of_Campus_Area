package Controller;

import sun.applet.Main;

import java.util.Date;

public class Simulator extends Thread {
    public boolean isPaused = false;
    public boolean isFinished = false;

    private static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

    public Date startingDate;
    public Date finishingDate;

    public Date simulationStart;
    public Date simulationEnd;

    private static final int SIMULATION_FREQUENCY = 10;

    public Simulator(){

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
                    System.out.print("Sleeping for = "+Configurations.SIMULATION_SPEED);
                    sleep(Configurations.SIMULATION_SPEED);
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

                try {
                    System.out.print("Sleeping for = "+Configurations.SIMULATION_SPEED);
                    sleep(Configurations.SIMULATION_SPEED);
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

    public void cancel(){
        isFinished = true;
    }
}
