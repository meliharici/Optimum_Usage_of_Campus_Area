
package GUI.AnalysisViewFiles;

import Controller.Configurations;
import Controller.MainController;
import Model.CampusData;
import Model.MapModel.CampusMap;
import Model.MapModel.Node;
import Model.MapModel.PointOfInterest;
import jdk.nashorn.internal.runtime.arrays.ArrayIndex;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class MapPanel extends JPanel {

    private PoiPanel poiPanel;
    private CampusData campusData;
    private int width,height;
    private int x = 0;
    private int y = 0;
    private double zoomFactor = 1;

    CampusMap map;
    private double oldMouseX,oldMouseY;

    private int mouseNodeX,mouseNodeY;

    private static final double resolution_x = 7500.0;
    private static final double resolution_y = 5000.0;

    private int paintMode = 0;
    public static final int PAINT_NORMAL = 0;
    public static final int PAINT_MAPEDITOR = 1;

    public int mapEditorMode = 0;
    public static final int DEFAULT_MODE = 0;
    public static final int POI_MODE = 1;
    public static final int ROAD_MODE = 2;
    public static final int WALL_MODE = 3;
    public static final int EMPTY_MODE = 4;

    public static final Color POI_COLOR = new Color(200,244,124);
    public static final Color POI_AREA_COLOR = new Color(250,100,100,100);
    public static final Color ROAD_COLOR = new Color(152,200,240);
    public static final Color WALL_COLOR = new Color(52,23,124);




    public MapPanel(int WIDTH, int HEIGHT) {
        width = WIDTH;
        height = HEIGHT;
        this.setSize(WIDTH,HEIGHT);

        this.map = CampusMap.getCampusMap();

        System.out.println("map panel width : " + width + "  ,map panel height: " + height);
    }

    public void setDrawMode(int mode){
        this.paintMode = mode;
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
            g.drawImage(Configurations.DEFAULT_CAMPUS_MAP, x, y, (int)(width*zoomFactor), (int)(height*zoomFactor), this);
            switch(paintMode){
                case PAINT_NORMAL : paintNormal(g);break;
                case PAINT_MAPEDITOR : paintMapEditor(g);break;
            }
        }

        else
            g.drawImage(Configurations.DEFAULT_CAMPUS_MAP, 0, 0, width, height, this);
    }

    private void paintNormal(Graphics g){
        MainController.getInstance().studentController.paintStudents(g,x,y,zoomFactor);
    }




    private void paintMapEditor(Graphics g){

        for(int i = 0; i < CampusMap.xDimension;i++){
            for(int j = 0; j<CampusMap.yDimension;j++){

                double finalLength = (Node.NODE_SIZE*zoomFactor);

                if(((i+1)*finalLength+x)<0 || (i*finalLength+x)>width){

                }
                else if(((j+1)*finalLength+y)<0 || (j*finalLength+y) > height){

                }
                else{
                    Node[][] nodes = CampusMap.getCampusMap().nodes;
                    boolean isMouseOnNode = i==this.mouseNodeX && j==this.mouseNodeY;
                    drawNode(g,nodes[i][j],isMouseOnNode);
                }


            }
        }
        drawPOIs(g);

        if(isMouseHeld&&heldPOI != null){
            drawPOIArea(g,heldPOI);
        }

        repaint();
    }


    public void drawNode(Graphics g,Node n,boolean isMouseOnNode){
        Polygon p = getNodePolygon(n,0);
        Node[][] nodes = CampusMap.getCampusMap().nodes;

        if(isMouseOnNode && mapEditorMode!=DEFAULT_MODE){
            if(nodes[mouseNodeX][mouseNodeY].nodeState == Node.EMPTY){
                switch(mapEditorMode){
                    case POI_MODE:
                        g.setColor(POI_COLOR);
                        break;
                    case ROAD_MODE:g.setColor(ROAD_COLOR);break;
                    case WALL_MODE:g.setColor(WALL_COLOR);break;
                }
            }
            else{
                g.setColor(Color.RED);
            }
            g.fillPolygon(p);
            return;
        }

        switch(n.nodeState){
            case Node.EMPTY:
                g.setColor(Color.BLACK);
                g.drawPolygon(p);
                break;
            case Node.ROAD:
                g.setColor(ROAD_COLOR);
                g.fillPolygon(p);
                break;
            case Node.WALL:
                g.setColor(WALL_COLOR);
                g.fillPolygon(p);
                break;
            case Node.POI:

                g.setColor(POI_COLOR);
                g.fillPolygon(p);
                break;
        }
    }

    public Polygon getNodePolygon(Node n,double size){
        int x = n.xCoords;
        int y = n.yCoords;

        double finalLength = (Node.NODE_SIZE*zoomFactor);

        int[] xCoords = new int[4];
        int[] yCoords = new int[4];

        xCoords[0] = xCoords[3] = (int)((x-size)*finalLength+this.x);
        xCoords[1] = xCoords[2] = (int)((x+1+size)*finalLength+this.x);

        yCoords[0] = yCoords[1] = (int)((y-size)*finalLength+this.y);
        yCoords[2] = yCoords[3] = (int)((y+1+size)*finalLength+this.y);

        return new Polygon(xCoords,yCoords,4);
    }

    public void drawPOIs(Graphics g){
        ArrayList<PointOfInterest> pois = CampusMap.getCampusMap().pois;

        for(int i = 0;i<pois.size();i++){
            PointOfInterest poi = pois.get(i);
            drawPOIArea(g,poi);
            boolean isMouseOnNode = poi.xCoords==this.mouseNodeX && poi.yCoords==this.mouseNodeY;
            drawNode(g,poi,isMouseOnNode);
        }
    }

    public void drawPOIArea(Graphics g,PointOfInterest poi){
        g.setColor(POI_AREA_COLOR);
        Polygon poiPoly = getNodePolygon(poi,poi.size);
        g.fillPolygon(poiPoly);
    }


    public void mapDragged(double x,double y){

        if(mapEditorMode == DEFAULT_MODE){
            double xDiff = oldMouseX-x;
            double yDiff = oldMouseY-y;
            this.oldMouseX = x;
            this.oldMouseY = y;

            this.x -= xDiff;
            this.y -= yDiff;

            checkAndValidateCoordinates();
        }
        else{
            findMouseNode(x,y);

            Node[][] nodes = CampusMap.getCampusMap().nodes;
            if(mapEditorMode != POI_MODE){
                placeNode(x,y);
            }
            if(heldPOI!=null&&isMouseHeld){
                heldPOI.xCoords = mouseNodeX;
                heldPOI.yCoords = mouseNodeY;
            }

        }

    }

    public void mouseMoved(double x,double y){
        this.oldMouseX = x;
        this.oldMouseY = y;
        findMouseNode(x,y);
    }

    public Node getNodeAt(int x,int y){
        Node[][] nodes = CampusMap.getCampusMap().nodes;
        try{
            return nodes[x][y];
        }
        catch(ArrayIndexOutOfBoundsException e){
            return nodes[0][0];
        }
    }

    public void mapClicked(double x,double y){
        Node node = getNodeAt(mouseNodeX,mouseNodeY);

        if(pressX == x && pressY == y){
            if(mapEditorMode==POI_MODE&&node.nodeState == Node.POI){
                Node[][] nodes = CampusMap.getCampusMap().nodes;

                System.out.println("Pressed POI");
                System.out.println(mouseNodeX+" - "+mouseNodeY);

                Node n = nodes[mouseNodeX][mouseNodeY];
                if(n instanceof PointOfInterest){
                    if(poiPanel != null){
                        if(poiPanel.isVisible() == true){
                            poiPanel.poiFrame.dispose();
                        }
                    }

                    PointOfInterest poi = (PointOfInterest) nodes[mouseNodeX][mouseNodeY];
                    poiPanel = new PoiPanel(poi);
                }
                else{
                    System.out.println("Should be point of interest but is not");
                }

            }
        }
    }

    boolean isMouseHeld = false;
    PointOfInterest heldPOI = null;
    Node previousNode = null;
    double pressX,pressY;

    public void mapPressed(double x,double y){
        isMouseHeld = true;
        pressX = x;
        pressY = y;
        Node node = getNodeAt(mouseNodeX,mouseNodeY);

        if(mapEditorMode==POI_MODE&&node.nodeState == Node.POI){
            heldPOI = (PointOfInterest) node;
            Node[][] nodes = CampusMap.getCampusMap().nodes;

            nodes[mouseNodeX][mouseNodeY] = new Node(Node.EMPTY,mouseNodeX,mouseNodeY);
        }
        else{
            placeNode(x,y);
        }
    }

    public void mapReleased(double x,double y){
        Node[][] nodes = CampusMap.getCampusMap().nodes;

        if(heldPOI != null && isMouseHeld){
            heldPOI.xCoords = mouseNodeX;
            heldPOI.yCoords = mouseNodeY;
            nodes[mouseNodeX][mouseNodeY] = heldPOI;
        }

        isMouseHeld = false;
        heldPOI = null;

    }

    public void placeNode(double x,double y){
        Node[][] nodes = CampusMap.getCampusMap().nodes;

        if(paintMode == PAINT_MAPEDITOR){
            switch(mapEditorMode){
                case DEFAULT_MODE:
                    System.out.println("Pressed : "+this.mouseNodeX+","+this.mouseNodeY);
                    System.out.println("Node is = "+nodes[this.mouseNodeX][this.mouseNodeY].nodeState);
                    break;
                case POI_MODE:
                    CampusMap map = CampusMap.getCampusMap();
                    PointOfInterest newPoi = new PointOfInterest(mouseNodeX,mouseNodeY);
                    nodes[mouseNodeX][mouseNodeY] = newPoi;
                    map.pois.add(newPoi);

                    System.out.println("Placed Building");
                    break;
                case ROAD_MODE:
                    nodes[mouseNodeX][mouseNodeY].nodeState = Node.ROAD;
                    System.out.println("Placed Road");
                    break;
                case WALL_MODE:
                    nodes[mouseNodeX][mouseNodeY].nodeState = Node.WALL;
                    System.out.println("Placed Wall");
                    break;
                case EMPTY_MODE:
                    CampusMap.getCampusMap().removeNode(mouseNodeX,mouseNodeY);
                    break;
            }
        }
    }

    public void findMouseNode(double x,double y){
        double finalLength = (Node.NODE_SIZE*zoomFactor);
        double reverseTranslationX = x-this.x;
        double reverseTranslationY = y-this.y;

        this.mouseNodeX = (int)(reverseTranslationX/finalLength);
        this.mouseNodeY = (int)(reverseTranslationY/finalLength);
    }

    public void keyPressed(char c){
        int num = 0;
        try{
            num = Integer.parseInt(c+"");
        }
        catch(Exception e){
            return;
        }

        System.out.println(num);

        if(paintMode==PAINT_MAPEDITOR){
            switch(num){
                case 0:mapEditorMode = DEFAULT_MODE;break;
                case 1:mapEditorMode = POI_MODE;break;
                case 2:mapEditorMode = ROAD_MODE;break;
                case 3:mapEditorMode = WALL_MODE;break;
                case 4:mapEditorMode = EMPTY_MODE;break;
            }
        }

    }

    public void checkAndValidateCoordinates(){
        if(this.x > 0)this.x = 0;
        if(this.y > 0)this.y = 0;

        if(-1*this.x + this.width > this.width*this.zoomFactor)this.x = (int) (this.width- this.width*this.zoomFactor);
        if(-1*this.y + this.height > this.height*this.zoomFactor)this.y = (int) (this.height- this.height*this.zoomFactor);

        repaint();
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