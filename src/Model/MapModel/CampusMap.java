package Model.MapModel;

import Model.DijkstraModel.Algorithm;
import Model.DijkstraModel.Graph;
import Model.DijkstraModel.GraphInitializer;
import Model.DijkstraModel.Vertex;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class CampusMap {
    public Node[][] nodes;
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

        InitializeMap();

    }

    public void saveCampusMap(){
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
                nodes[x][y].nodeState = (int)s.getRow(y).getCell(x).getNumericCellValue();
            }
        }
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

    public void InitializeNodeConnections(){
        for(int i = 0;i<xDimension;i++){
            for(int j=0;j<yDimension;j++){
                InitializeNodeConnection(i,j);
            }
        }
    }

    public void InitializeNodeConnection(int x,int y){
        Node n = nodes[x][y];
        if(n.nodeState!=Node.ROAD&&n.nodeState!=Node.BUILDING){
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
            if(n2.nodeState!=Node.ROAD&&n2.nodeState!=Node.BUILDING){
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

    public void InitializeGraph(){
        GraphInitializer graphInitializer = new GraphInitializer(nodes);
        graph =  graphInitializer.initializeGraph();
    }

    public Path findPath(int x1, int y1, int x2, int y2){

        Path campusPath = new Path();

        Vertex source = get_vertex(x1,y1);
        Vertex target = get_vertex(x2,y2);

        // if source and destination are buildings
        if(nodes[x1][y1].nodeState == Node.BUILDING && nodes[x2][y2].nodeState == Node.BUILDING){
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

