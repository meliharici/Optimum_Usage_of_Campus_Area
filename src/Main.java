import javax.swing.*;
import java.awt.*;

import Controller.*;
import GUI.*;
import Model.DijkstraModel.Graph;
import Model.MapModel.CampusMap;

public class Main {

    public static void main(String[] args){
        Configurations.initialize();

        MainController.createInstance();
        MainController controller = MainController.getInstance();
        MainView view = new MainView();

        controller.mainView = view;
        controller.initialize();
        controller.start();




        /*---------------------------------------------*/
        // DIJKSTRA TEST

        CampusMap cm = CampusMap.getCampusMap();
        cm.InitializeGraph();
        cm.findPath(305,165,270,160);


    }

}
