import javax.swing.*;
import java.awt.*;

import Controller.*;
import GUI.*;
import Model.DijkstraModel.Graph;
import Model.MapModel.CampusMap;
import Model.MapModel.Path;

public class Main {

    public static void main(String[] args){
        Configurations.initialize();

        MainController.createInstance();
        MainController controller = MainController.getInstance();
        MainView view = new MainView();

        controller.mainView = view;
        controller.initialize();
        controller.start();

        testStuff();



    }

    public static void testStuff(){
        CampusMap cm = CampusMap.getCampusMap();
        cm.InitializeGraph();
        Path p = cm.findPath(305,165,270,160);

        System.out.println("Simulating movement with speed 0.5");
        for(int i = 0;i<20;i++){

            System.out.println(p);
            p.move(0.5);
        }

        p.reset();

        System.out.println("");
        System.out.println("Simulating movement with speed 3");

        for(int i = 0;i<20;i++){
            System.out.println(p);
            p.move(3);
        }
    }

}
