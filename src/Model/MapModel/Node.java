package Model.MapModel;

public class Node {
    public static int BLOCK = 0;
    public static int ROAD = 1;
    public static int WALL = 2;
    public static int BUILDING = 4;

    public static int NOT_A_BUILDING = 0;
    public static int EF = 1;
    public static int FEAS = 2;

    public int buildingCode = 0;


    public static final int NODE_SIZE = 10; //bu değer değişicek

    public int nodeState = 1;

    public Node(){
    }

    public Node(int nodeState){
        this.nodeState = nodeState;
    }

    public static Node BlockNode(){
        return new Node(BLOCK);
    }

    public static Node RoadNode(){
        return new Node(ROAD);
    }

    public static Node WallNode(){
        return new Node(WALL);

    }

    public static Node Building(int buildingCode){
        Node n = new Node(4);
        n.buildingCode = buildingCode;
        return n;
    }
}
