package Model;

import java.util.Date;

public class LocationData {

    private Date date;
    private double x;
    private double y;

    public LocationData(Date date, double x, double y) {
        this.date = date;
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public Date getDate(){ return this.date; }

    public void printLocation() {
        System.out.println("Location (" + this.x + " , " + this.y + ")  recorded at: " + this.date.getTime());
    }
}
