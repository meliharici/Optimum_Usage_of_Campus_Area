import javax.swing.*;
import java.awt.*;

import Controller.*;
import GUI.*;

public class Main {

    public static void main(String[] args){
        Configurations.initialize();

        MainController.createInstance();
        MainController controller = MainController.getInstance();
        MainView view = new MainView();

        controller.mainView = view;
        controller.initialize();
        controller.start();


    }

}
