package Model;

import java.util.Date;

public class HumanData {
    public int[] coordinates;
    public Date date;

    public HumanData(int xCoordinate,int yCoordinate,Date date){
        this.coordinates = new int[2];
        this.coordinates[0] = xCoordinate;
        this.coordinates[1] = yCoordinate;
        this.date = date;
    }
}
