package GUI.AnalysisViewFiles;

import Controller.Configurations;
import Model.CampusData;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class AnalysisView extends JPanel{
   public MapPanel mapPanel;
   public MenuPanel menuPanel;
   public JPanel infoPanel;

    private static int color_r = 43;
    private static int color_g = 63;
    private static int color_b = 83;

    InfoElement ie1,ie2,ie3,ie4,ie5;
    JSlider simulationSpeedSlider;

    private static AnalysisView analysisView;

    public static AnalysisView getInstance(){
        if(analysisView == null){
            analysisView = new AnalysisView();
        }
        return analysisView;
    }

    private AnalysisView(){
        AnalysisMenuMouseHandler mouseHandler = new AnalysisMenuMouseHandler(this);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);

        AnalysisMenuKeyboardHandler keyboardHandler = new AnalysisMenuKeyboardHandler(this);
        this.addKeyListener(keyboardHandler);
        this.setFocusable(true);

        this.requestFocus();
    }

    public void initialize(){
        this.setLayout(null);

        addMenuPanel();
        addMapPanel();
        addInfoPanel();


        this.requestFocusInWindow();
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
        infoPanel.setLayout(null);
        int width =  infoPanel.getSize().width;
        int height = infoPanel.getSize().height;

        simulationSpeedSlider = new JSlider(1000,6000,3500);
        simulationSpeedSlider.setLocation(width / 4, height / 4);
        simulationSpeedSlider.setSize(width / 3 , height / 3);
        simulationSpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                Configurations.SIMULATION_SPEED = (slider.getValue())/6000.0;
            }
        });
        simulationSpeedSlider.setBackground(new Color(color_r,color_g,color_b));
        infoPanel.add(simulationSpeedSlider);

        this.add(infoPanel);
    }

    public void addInformationElements(){
        simulationSpeedSlider.setVisible(false);
        int width =  infoPanel.getSize().width;
        int height = infoPanel.getSize().height;
        int x = (width * 2) / 100;  // x-padding %2
        int y = (height * 15) / 100; // y-padding %15
        int w = (width * 16) / 100; // width %16
        int h = (height * 80) / 100; // height %80
        int s = 2 * x; // horizontal space between elements

         ie1 = new InfoElement(x,y , w, h, "0", Configurations.defaultIconPath);
         ie2 = new InfoElement(x + w + s, y , w, h, "1", Configurations.buildingIconPath);
         ie3 = new InfoElement(x + w + s + w + s , y , w, h, "2", Configurations.roadIconPath);
         ie4 = new InfoElement(x + w + s + 2*w + 2*s, y , w, h, "3", Configurations.wallIconPath);
         ie5 = new InfoElement(x + w + s + 3*w + 3*s, y , w, h, "4", Configurations.emptyIconPath);

        infoPanel.add(ie1);
        infoPanel.add(ie2);
        infoPanel.add(ie3);
        infoPanel.add(ie4);
        infoPanel.add(ie5);
    }

    public void hideInformationElements(){
        ie1.setVisible(false);
        ie2.setVisible(false);
        ie3.setVisible(false);
        ie4.setVisible(false);
        ie5.setVisible(false);
        simulationSpeedSlider.setVisible(true);
    }


    public void setCampusData(CampusData campusData){
        this.repaint();
        this.mapPanel.setCampusData(campusData);
        this.repaint();
    }

    public void MouseDragged(double x,double y){
        double mapPanelStartX = Configurations.WIDTH*Configurations.MENU_PANEL_WIDTH_RATIO;
        double mapPanelStartY = 0;

        double xOnMap = x-mapPanelStartX;
        double yOnMap = y-mapPanelStartY;

        this.mapPanel.mapDragged(xOnMap,yOnMap);
        this.repaint();

    }

    public void MouseMoved(double x,double y){
        double mapPanelStartX = Configurations.WIDTH*Configurations.MENU_PANEL_WIDTH_RATIO;
        double mapPanelStartY = 0;

        double xOnMap = x-mapPanelStartX;
        double yOnMap = y-mapPanelStartY;

        this.mapPanel.mouseMoved(xOnMap,yOnMap);
    }

    public void mousePressed(double x,double y){
        this.mapPanel.mapPressed(x,y);
        this.repaint();
    }

    public void mouseReleased(double x,double y){
        this.mapPanel.mapReleased(x,y);
        this.repaint();
    }

    public void mouseClicked(double x , double y ){
        this.mapPanel.mapClicked(x,y);
        this.repaint();
    }

    public void MouseZoomed(double zoomFactor){
        this.mapPanel.mapZoomed(zoomFactor);
        this.repaint();

    }

}

class AnalysisMenuMouseHandler extends MouseAdapter {
    public AnalysisView view;

    public AnalysisMenuMouseHandler(AnalysisView view){
        this.view = view;
    }

    public void mouseDragged(MouseEvent e){
        this.view.MouseDragged(e.getX(),e.getY());
    }

    public void mouseMoved(MouseEvent e){
        double x = e.getX();
        double y = e.getY();

        this.view.MouseMoved(x,y);
    }

    public void mousePressed(MouseEvent e){
        this.view.mousePressed(e.getX(),e.getY());
    }

    public void mouseReleased(MouseEvent e){
        this.view.mouseReleased(e.getX(),e.getY());
    }

    public void mouseClicked(MouseEvent e){
        this.view.mouseClicked(e.getX(),e.getY());
    }

    public void mouseWheelMoved(MouseWheelEvent e){
        view.MouseZoomed(e.getPreciseWheelRotation()/4);
    }
}

class AnalysisMenuKeyboardHandler extends KeyAdapter {
    public AnalysisView view;

    public AnalysisMenuKeyboardHandler(AnalysisView view){
        this.view = view;
    }

    public void keyPressed(KeyEvent e){
        this.view.mapPanel.keyPressed(e.getKeyChar());
    }
}

