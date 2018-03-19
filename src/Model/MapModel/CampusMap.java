package Model.MapModel;

public class CampusMap {
    private static Node[][] nodes;
    public static int xDimension = 800;
    public static int yDimension = 500;

    private static CampusMap map;

    public void saveCampusMap(String filePath){

    }

    public void loadCampusMap(String filePath){

    }

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
        CoverMapWithBlocks();
    }

    public static void InitializeMap(){
        for(Node a[] :nodes){
            for(Node b :a){
                b = Node.RoadNode();
            }
        }
    }

    public static void CoverMapWithBlocks(){
        int xEnd = xDimension - 1;
        int yEnd = yDimension - 1;

        for(int i = 0;i<xDimension;i++){
            nodes[i][0] = Node.BlockNode();
            nodes[i][yEnd] = Node.BlockNode();
        }

        for(int j = 0;j<yDimension;j++){
            nodes[0][j] = Node.BlockNode();
            nodes[xEnd][j] = Node.BlockNode();
        }
    }
}

