package GUI.AnalysisViewFiles;

import Controller.Configurations;
import Model.CampusData;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class AnalysisView extends JPanel{
   public MapPanel mapPanel;
   public MenuPanel menuPanel;
   public JPanel infoPanel;

    private static int color_r = 43;
    private static int color_g = 63;
    private static int color_b = 83;

    public AnalysisView(){
        AnalysisMenuMouseHandler mouseHandler = new AnalysisMenuMouseHandler(this);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);
    }

    public void initialize(){
        this.setLayout(null);

        addMenuPanel();
        addMapPanel();
        addInfoPanel();
    }

    public void addMenuPanel(){
        menuPanel = new MenuPanel((int) (Configurations.WIDTH* Configurations.MENU_PANEL_WIDTH_RATIO), (int) (Configurations.HEIGHT*Configurations.MENU_PANEL_HEIGHT_RATIO));
        menuPanel.setLocation(0,0);
        this.add(menuPanel);
    }

    public void addMapPanel(){
        mapPanel = new MapPanel((int) (Configurations.WIDTH*Configurations.MAP_PANEL_WIDTH_RATIO), (int) (Configurations.HEIGHT*Configurations.MAP_PANEL_HEIGHT_RATIO));
        mapPanel.setLocation((int)(Configurations.WIDTH*Configurations.MENU_PANEL_WIDTH_RATIO),0);
        mapPanel.setBackground(Color.yellow);
        this.add(mapPanel);
    }

    public void addInfoPanel(){
        infoPanel = new JPanel();
        infoPanel.setSize((int) (Configurations.INFO_PANEL_WIDTH_RATIO * Configurations.WIDTH), (int) (Configurations.INFO_PANEL_HEIGHT_RATIO * Configurations.HEIGHT));
        infoPanel.setLocation((int)(Configurations.WIDTH*Configurations.MENU_PANEL_WIDTH_RATIO),(int) (Configurations.HEIGHT*Configurations.MAP_PANEL_HEIGHT_RATIO));
        infoPanel.setOpaque(true);
        infoPanel.setBackground(new Color(43, 63, 83));

        JSlider simulationSpeedSlider = new JSlider(2000,3900,3000);
        simulationSpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                Configurations.SIMULATION_SPEED = 4000-slider.getValue();
            }
        });
        simulationSpeedSlider.setBackground(new Color(color_r,color_g,color_b));
        infoPanel.add(simulationSpeedSlider);

        this.add(infoPanel);
    }

    public void setCampusData(CampusData campusData){
        this.repaint();
        this.mapPanel.setCampusData(campusData);
        this.repaint();
    }

    public void MouseDragged(double x,double y){
        this.mapPanel.mapDragged(x,y);
        this.repaint();

    }

    public void MouseZoomed(double zoomFactor){
        this.mapPanel.mapZoomed(zoomFactor);
        this.repaint();

    }

}

class AnalysisMenuMouseHandler extends MouseAdapter {
    public AnalysisView view;
    private double mouseX,mouseY;

    public AnalysisMenuMouseHandler(AnalysisView view){
        this.view = view;
    }

    public void mouseDragged(MouseEvent e){
        view.MouseDragged(mouseX-e.getX(),mouseY-e.getY());
        this.mouseY = e.getY();
        this.mouseX = e.getX();
    }

    public void mousePressed(MouseEvent e){
        this.mouseX = e.getX();
        this.mouseY = e.getY();
    }

    public void mouseWheelMoved(MouseWheelEvent e){
        view.MouseZoomed(e.getPreciseWheelRotation()/4);
    }
}

