
package GUI.AnalysisViewFiles;

import Controller.Configurations;
import Controller.MainController;
import Model.CampusData;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class MapPanel extends JPanel {

    private CampusData campusData;
    private int width,height;
    private int x = 0;
    private int y = 0;
    private double zoomFactor = 1;

    private static final double resolution_x = 7500.0;
    private static final double resolution_y = 5000.0;



    public MapPanel(int WIDTH, int HEIGHT) {
        width = WIDTH;
        height = HEIGHT;
        this.setSize(WIDTH,HEIGHT);

        System.out.println("map panel width : " + width + "  ,map panel height: " + height);
    }

    public void setCampusData(CampusData campusData){
        this.repaint();
        this.campusData = campusData;
        this.repaint();

        Graphics g = campusData.campusMapImage.getGraphics();


        if(campusData.data != null)
        for(int i=0; i < campusData.data.size(); i++){
            g.setColor(Color.RED);

            /*
            int x = (int) campusData.data.get(i).getX();
            int y = (int) campusData.data.get(i).getY();
            g.fillRect(x, y, 20,20);
            */


            int human_size = 13;

            double x = campusData.data.get(i).getX();
            double y = campusData.data.get(i).getY();


            if(x < 4000.0 && y > 3500.0){ // shuttle area
                if(x < 2700) {
                    g.fillRect((int) x, (int) y, human_size, human_size);
                }
                else{
                    Point calculated = rotate_and_translate(new Point(x,y), -5,  -150,  -100);
                    x = calculated.x;
                    y = calculated.y;
                    g.fillRect((int) x, (int) y, human_size,human_size);
                }
            }
            else if(x > 4200.0){ // engineering building and student center area
                g.setColor(Color.blue);
                Point calculated = rotate_and_translate(new Point(x,y), -24,  -600,  180);
                x = calculated.x;
                y = calculated.y;
                if(x > 2000 && x < 4700){
                    if(!(x > 4000 && y > 3500))
                        g.fillRect((int) x, (int) y, human_size,human_size);
                }
            }
            else{ // sports center area

                g.setColor(Color.magenta);
                Point calculated = rotate_and_translate(new Point(x,y), 119,  -1450.0,  820.0);
                Point reflected = reflect(calculated, 1258.0, 2689.9, 1986.1, 2352.8);
                Point calculated2 = rotate_and_translate(reflected, 0,  570.0,  1270.0);
                x = calculated2.x;
                y = calculated2.y;
                if(x > 2100 && x < 3500 ){
                    if(!(x > 3000 & y > 3000)){
                        if(!(x > 4000 && y > 3500))
                            g.fillRect((int) x, (int) y, human_size,human_size);
                    }

                }
            }
        }

        MainController controller = MainController.getInstance();
        controller.campusData.campusMapImage = campusData.campusMapImage;
    }


    //will be removed
    private Point rotate_and_translate(Point original, int angle, double translateX, double translateY){
        double centerx = resolution_x/2;
        double centery = resolution_y/2;
        double x = original.x;
        double y = original.y;
        // translating the coordinates back to origin
        x = x - centerx;
        y = y - centery;
        // rotate point
        double rotation_angle = Math.toRadians(angle);
        double cos = Math.cos(rotation_angle);
        double sin = Math.sin(rotation_angle);
        // rotation about the origin
        double x_new = x*cos - y*sin;
        double y_new = y*cos + x*sin;
        // translate the points back
        x = x_new + centerx;
        y = y_new + centery;
        // translating to left and above
        x  += translateX;
        y  += translateY;
        return new Point(x,y);
    }

    private Point reflect(Point p, double x0, double y0, double x1, double y1){ //  (x0,y0) and (x1,y1) are coordinates of endpoints of a line
        double dx,dy,a,b;
        long x2,y2;
        dx  = x1 - x0;
        dy  = y1 - y0;
        a   = (dx * dx - dy * dy) / (dx * dx + dy*dy);
        b   = 2 * dx * dy / (dx*dx + dy*dy);
        x2  = Math.round(a * (p.x - x0) + b*(p.y - y0) + x0);
        y2  = Math.round(b * (p.x - x0) - a*(p.y - y0) + y0);
        return new Point(x2,y2);
    }

    class Point {  // inner class for storing x,y coordinates
        double x,y;
        public Point(double x, double y){
            this.x = x;
            this.y = y;
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(campusData != null){

            g.drawImage(campusData.campusMapImage, (int) (x), (int) (y), (int)(width*zoomFactor), (int)(height*zoomFactor), this);
        }

        else
            g.drawImage(Configurations.DEFAULT_CAMPUS_MAP, 0, 0, width, height, this);
    }

    public void mapDragged(double x,double y){
        this.x -= x;
        this.y -= y;

        checkAndValidateCoordinates();
    }

    public void checkAndValidateCoordinates(){
        if(this.x > 0)this.x = 0;
        if(this.y > 0)this.y = 0;

        if(-1*this.x + this.width > this.width*this.zoomFactor)this.x = (int) (this.width- this.width*this.zoomFactor);
        if(-1*this.y + this.height > this.height*this.zoomFactor)this.y = (int) (this.height- this.height*this.zoomFactor);
    }

    public void mapZoomed(double zoomFactor){
        double newZoomFactor = this.zoomFactor - zoomFactor;

        double midPointX = this.width/2;
        double midPointY = this.height/2;

        double xDistance = midPointX-this.x;
        double yDistance = midPointY-this.y;

        double xRatio = xDistance/(width*this.zoomFactor);
        double yRatio = yDistance/(height*this.zoomFactor);

        double widthDifferance = width*newZoomFactor - width*this.zoomFactor;
        double heightDifferance = height*newZoomFactor - height*this.zoomFactor;

        this.x -= widthDifferance*xRatio;
        this.y -= heightDifferance*yRatio;

        this.zoomFactor -= zoomFactor;

        if(this.zoomFactor < 1) this.zoomFactor = 1;

        checkAndValidateCoordinates();
    }
}