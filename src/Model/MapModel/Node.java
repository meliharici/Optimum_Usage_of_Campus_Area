package Model.MapModel;

public class Node {

    public static final double NODE_SIZE = 2; //bu değer değişicek

    public static int EMPTY = 0;
    public static int ROAD = 1;
    public static int WALL = 2;
    public static int BUILDING = 4;
    public int nodeState = 0;

    public static int NOT_A_BUILDING = 0;
    public static int EF = 1;
    public static int FEAS = 2;
    public int buildingCode = 0;

    public int xCoords,yCoords = -1; //-1 stands for unitialized

    public Node[] connections = null;

    public Node(int nodeState,int xCoords,int yCoords){
        this.nodeState = nodeState;
        connections = new Node[8];
    }

    public static Node Building(int buildingCode,int xCoords,int yCoords){
        Node n = new Node(4,xCoords,yCoords);
        n.buildingCode = buildingCode;
        return n;
    }
}
