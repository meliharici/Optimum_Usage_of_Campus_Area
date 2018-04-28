package Model.MapModel;

import Model.DijkstraModel.Algorithm;
import Model.DijkstraModel.Graph;
import Model.DijkstraModel.GraphInitializer;
import Model.DijkstraModel.Vertex;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class CampusMap {
    public Node[][] nodes;
    public ArrayList<PointOfInterest> pois;
    public static int xDimension = 800;
    public static int yDimension = 500;
    public Graph graph;

    private static CampusMap map;

    public static CampusMap getCampusMap(){
        if(map == null){
            map = new CampusMap(xDimension,yDimension);
        }
        return map;
    }

    private CampusMap(int xDimension, int yDimension){
        this.xDimension = xDimension;
        this.yDimension = yDimension;

        this.nodes = new Node[xDimension][yDimension];
        this.pois = new ArrayList<PointOfInterest>();

        InitializeMap();
        loadPOIs();

    }
    public void InitializeMap(){
        for(int i = 0;i<xDimension;i++){
            for(int j = 0; j<yDimension;j++){
                nodes[i][j] = new Node(Node.EMPTY,i,j);
            }
        }
        loadCampusMap();
        InitializeNodeConnections();
    }

    public void loadCampusMap(){
        XSSFWorkbook wb = null;
        Sheet s = null;
        try {
            wb = new XSSFWorkbook(new File("CampusMap.xlsx"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        s = wb.getSheet("Map");


        for(int y = 0;y<yDimension;y++){

            for(int x = 0;x<xDimension;x++){
                int state = (int)s.getRow(y).getCell(x).getNumericCellValue();

                nodes[x][y].nodeState = state;
                nodes[x][y].xCoords = x;
                nodes[x][y].yCoords = y;
                if(state == Node.POI){
                    nodes[x][y] = new PointOfInterest(x,y);
                }

            }
        }
    }

    public void InitializeNodeConnections(){
        for(int i = 0;i<xDimension;i++){
            for(int j=0;j<yDimension;j++){
                InitializeNodeConnection(i,j);
            }
        }
    }

    public void InitializeNodeConnection(int x,int y){
        Node n = nodes[x][y];
        if(n.nodeState!=Node.ROAD&&n.nodeState!=Node.POI){
            return;
        }

        connectNodes(x,y,x-1,y);
        connectNodes(x,y,x,y-1);
        connectNodes(x,y,x+1,y);
        connectNodes(x,y,x,y+1);

        connectNodes(x,y,x-1,y-1);
        connectNodes(x,y,x+1,y-1);
        connectNodes(x,y,x+1,y+1);
        connectNodes(x,y,x-1,y+1);
    }

    public void connectNodes(int x1,int y1,int x2,int y2){
        if(checkValues(x2,y2)==false){
            return;
        }
        else{
            Node n2 = nodes[x2][y2];
            if(n2.nodeState!=Node.ROAD&&n2.nodeState!=Node.POI){
                return;
            }

            Node n = nodes[x1][y1];
            double[] connection = new double[3];
            connection[0] = x2;
            connection[1] = y2;
            connection[2] = Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
            n.connections.add(connection);
        }
    }

    public boolean checkValues(int x,int y){
        return x>0 && y>0 && x<xDimension && y<yDimension;
    }

    public void loadPOIs(){
        XSSFWorkbook wb = null;
        Sheet s = null;
        try {
            wb = new XSSFWorkbook(new File("Points of Interest.xlsx"));
            s = wb.getSheet("Point of Interest");
            int numberOfRows = s.getPhysicalNumberOfRows();

            for(int i = 1; i<numberOfRows; i++){
                XSSFRow currRow = (XSSFRow) s.getRow(i);
                PointOfInterest poi = parsePOI(currRow);
                this.pois.add(poi);
                this.nodes[poi.xCoords][poi.yCoords] = poi;
            }

        } catch (Exception e) {

        }



    }

    public PointOfInterest parsePOI(XSSFRow row){
        PointOfInterest poi = new PointOfInterest(-1,-1);

        for(int i = 0; i<7; i++){
            XSSFCell currCell = row.getCell(i);

            switch(i){
                case 0:
                    poi.name = currCell.getStringCellValue();
                    break;
                case 1:
                    poi.size = (int) currCell.getNumericCellValue();
                    break;
                case 2:
                    poi.type = (int) currCell.getNumericCellValue();
                    break;
                case 3:
                    poi.preferance = currCell.getNumericCellValue();
                    break;
                case 4:
                    poi.distancePenalty = currCell.getNumericCellValue();
                    break;
                case 5:
                    poi.xCoords = (int) currCell.getNumericCellValue();
                    break;
                case 6:
                    poi.yCoords = (int) currCell.getNumericCellValue();
                    break;
            }
        }

        return poi;

    }

    public void saveCampusMap(){
        saveNodes();
        savePOIs();

    }

    public void saveNodes(){
        XSSFWorkbook wb = null;
        Sheet s = null;
        wb = new XSSFWorkbook();
        wb.createSheet("Map");
        s = wb.getSheet("Map");


        for(int y = 0;y<yDimension;y++){
            s.createRow(y);

            for(int x = 0;x<xDimension;x++){
                s.getRow(y).createCell(x);
                s.getRow(y).getCell(x).setCellValue(nodes[x][y].nodeState);
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("CampusMap.xlsx");
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePOIs(){
        XSSFWorkbook wb = null;
        Sheet s = null;
        wb = new XSSFWorkbook();
        wb.createSheet("Point of Interest");
        s = wb.getSheet("Point of Interest");

        int numberOfPOIs = this.pois.size();

        s.createRow(0);
        setHeaderRow((XSSFRow) s.getRow(0));

        for(int y = 0; y<numberOfPOIs; y++){
            System.out.println("Writing POI at row "+y);

            s.createRow(y+1);
            PointOfInterest currPoi = pois.get(y);

            XSSFRow currRow = (XSSFRow) s.getRow(y+1);
            setRowValues(currRow,currPoi);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream("Points of Interest.xlsx");
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHeaderRow(XSSFRow row){

        for(int i = 0; i<7; i++){
            row.createCell(i);
            XSSFCell currCell = row.getCell(i);

            switch(i){
                case 0:
                    currCell.setCellValue("Name");
                    break;
                case 1:
                    currCell.setCellValue("Size");
                    break;
                case 2:
                    currCell.setCellValue("Type");
                    break;
                case 3:
                    currCell.setCellValue("Preferance");
                    break;
                case 4:
                    currCell.setCellValue("Penalty");
                    break;
                case 5:
                    currCell.setCellValue("X-Coord");
                    break;
                case 6:
                    currCell.setCellValue("Y-Coord");
                    break;
            }
        }
    }

    public void setRowValues(XSSFRow row, PointOfInterest poi){

        for(int i = 0; i<7; i++){
            row.createCell(i);
            XSSFCell currCell = row.getCell(i);

            switch(i){
                case 0:
                    currCell.setCellValue(poi.name);
                    break;
                case 1:
                    currCell.setCellValue(poi.size);
                    break;
                case 2:
                    currCell.setCellValue(poi.type);
                    break;
                case 3:
                    currCell.setCellValue(poi.preferance);
                    break;
                case 4:
                    currCell.setCellValue(poi.distancePenalty);
                    break;
                case 5:
                    currCell.setCellValue(poi.xCoords);
                    break;
                case 6:
                    currCell.setCellValue(poi.yCoords);
                    break;
            }
        }
    }

    public void removeNode(int x,int y){
        for(int i = 0;i<pois.size();i++){
            PointOfInterest poi = pois.get(i);

            if(poi.xCoords == x && poi.yCoords == y){
                pois.remove(i);
            }
        }
        nodes[x][y] = new Node(Node.EMPTY,x,y);
    }

    public void InitializeGraph(){
        GraphInitializer graphInitializer = new GraphInitializer(nodes);
        graph =  graphInitializer.initializeGraph();
    }

    public boolean checkInputs(int x1, int y1, int x2, int y2){
        int startNodeState = nodes[x1][y1].nodeState;
        int endNodeState = nodes[x2][y2].nodeState;

        return (startNodeState==Node.POI || startNodeState == Node.ROAD) && (endNodeState == Node.POI || endNodeState==Node.ROAD);
    }

    public Path findPath(int x1, int y1, int x2, int y2){

        Path campusPath = new Path();

        Vertex source = get_vertex(x1,y1);
        Vertex target = get_vertex(x2,y2);

        // if source and destination are buildings
        if(checkInputs(x1,y1,x2,y2)){
            Algorithm algo = new Algorithm(graph);
            algo.execute(source);
            LinkedList<Vertex> path = algo.getPath(target);
            for (Vertex vertex : path) {

                campusPath.appendNode(vertex.getX(),vertex.getY());
                // TODO: Bu alan Path class'ı implement edildikten sonra değişicek. Şimdilik sadece Print

                System.out.println("(" + vertex.getX() + " , " + vertex.getY() + ")");

            }
        }
        else{
            System.out.println("Invalid Inputs for path finding");
        }

        return campusPath;
    }

    private Vertex get_vertex(int x, int y){
        Vertex vertex = null;
        for(int i = 0; i < graph.getVertexes().size(); i++){
            Vertex current = graph.getVertexes().get(i);
            if(current.getX() == x && current.getY() == y){
                vertex = current;
            }
        }
        return vertex;
    }

}

