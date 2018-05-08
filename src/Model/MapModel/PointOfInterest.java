package Model.MapModel;

public class PointOfInterest extends Node{
    public int size = 2;
    public String name = "Empty";

    public double distancePenalty = 0.65;
    public double preferance = 0.45;

    public int type = 0;
    public static final int TYPE_BUILDING = 0;
    public static final int TYPE_FREEZONE = 1;
    public static final int TYPE_MAINFOOD = 2;
    public static final int TYPE_SNACKFOOD = 3;
    public static final int TYPE_ENTERANCE = 4;
    public static final int TYPE_STUDYAREA = 5;

    public PointOfInterest(int xCoords,int yCoords){
        super(Node.POI,xCoords,yCoords);

    }
}
