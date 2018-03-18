package Model.MapModel;

public class Map {
    private Node[][] nodes;
    private int xDimension;
    private int yDimension;

    public Map(int xDimension,int yDimension){
        this.xDimension = xDimension;
        this.yDimension = yDimension;

        this.nodes = new Node[xDimension][yDimension];

        InitializeMap();
        CoverMapWithBlocks();
    }

    public void InitializeMap(){
        for(Node a[] :nodes){
            for(Node b :a){
                b = Node.RoadNode();
            }
        }
    }

    public void CoverMapWithBlocks(){
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

