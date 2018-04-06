package Model.MapModel;

import java.util.ArrayList;

public class Node {

    public static final double NODE_SIZE = 2; //bu değer değişicek

    public final static int EMPTY = 0;
    public final static int ROAD = 1;
    public final static int WALL = 2;
    public final static int BUILDING = 4;
    public int nodeState = 0;

    public final static int NOT_A_BUILDING = 0;
    public final static int EF = 1;
    public final static int FEAS = 2;
    public int buildingCode = 0;

    public int xCoords,yCoords = -1; //-1 stands for unitialized

    /*
    Her row için
    1. Eleman = Bağlantılı olduğu nodenin x kordinati (connections.get(herhangi)[0])
    2. Eleman = Bağlantılı olduğu nodenin y kordinati (connections.get(herhangi)[1])
    3. Eleman = Bağlantının weight i                  (connections.get(herhangi)[2])


     */
    public ArrayList<double[]> connections = null;

    public Node(int nodeState,int xCoords,int yCoords){
        this.nodeState = nodeState;
        connections = new ArrayList<double[]>();
    }

    public static Node Building(int buildingCode,int xCoords,int yCoords){
        Node n = new Node(4,xCoords,yCoords);
        n.buildingCode = buildingCode;
        return n;
    }
}
