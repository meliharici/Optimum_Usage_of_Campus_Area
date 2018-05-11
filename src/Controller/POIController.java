package Controller;
import Model.MapModel.PointOfInterest;
import java.util.ArrayList;

public class POIController {
    private final ArrayList<PointOfInterest> pois;

    public POIController(ArrayList<PointOfInterest> pois){
        this.pois = pois;
    }

    public PointOfInterest GetPoiWithName(String name){
        PointOfInterest poi = null;

        for(PointOfInterest p : pois){
            if(p.name.equals(name)){
                poi = p;
            }
        }

        return poi;
    }
}
