package Model.MapModel;

public class CampusMap {
    private static Node[][] nodes;
    public static int xDimension = 800;
    public static int yDimension = 500;

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

    public void saveCampusMap(String filePath){

    }

    public void loadCampusMap(String filePath){

    }



    public static void InitializeMap(){
        for(int i = 0;i<xDimension;i++){
            for(int j = 0; j<yDimension;j++){
                nodes[i][j] = new Node(Node.EMPTY,i,j);
            }
        }
    }


}

