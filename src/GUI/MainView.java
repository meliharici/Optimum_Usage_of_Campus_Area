package GUI;

import Controller.Configurations;
import GUI.AnalysisViewFiles.AnalysisView;
import Model.CampusData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView{
    JFrame frame;
    JPanel mainPanel;

    JPanel currentView;

    AnalysisView analysisMenu;

    public MainView(){

    }

    public void initialize(){
        initializeFrame();
        initializeMainPanel();
        initializeAnalysisPanel();

        showPanel(analysisMenu);
        frame.setVisible(true);
    }

    public void initializeFrame(){
        frame = new JFrame("Campus Area Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Configurations.WIDTH,Configurations.HEIGHT);
        frame.setBackground(Color.cyan);
    }

    public void initializeMainPanel(){
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());
        frame.add(mainPanel);
    }

    public void initializeAnalysisPanel(){
        analysisMenu = new AnalysisView();
        analysisMenu.setName("AnalysisMenu");
        analysisMenu.initialize();
        analysisMenu.setVisible(true);

        mainPanel.add(analysisMenu,analysisMenu.getName());

    }

    public void showPanel(JPanel panel){
        CardLayout mainPanelLayout = (CardLayout) mainPanel.getLayout();
        mainPanelLayout.show(mainPanel,panel.getName());
        this.currentView = panel;
    }

    public void setCampusData(CampusData campusData){
        mainPanel.repaint();
        analysisMenu.setCampusData(campusData);
        mainPanel.repaint();
    }



}
