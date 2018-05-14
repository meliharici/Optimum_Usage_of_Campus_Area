package Controller;
import Model.MapModel.CampusMap;
import Model.MapModel.PointOfInterest;
import Model.Student;

import java.util.ArrayList;
import java.util.Random;

public class POIController {
    private ArrayList<PointOfInterest> pois;

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

    public PointOfInterest getPOIWithType(double x,double y,int type){
        CampusMap map = CampusMap.getCampusMap();

        ArrayList<PointOfInterest> possiblePOIs = new ArrayList<PointOfInterest>();
        double currentScore = 0;
        double totalScore = 0;

        for(PointOfInterest poi : pois){
            if(poi.type == type){
                possiblePOIs.add(poi);
            }
        }
        Random rgen = new Random();
        int randomIndex = rgen.nextInt(possiblePOIs.size());

        return possiblePOIs.get(randomIndex);
    }


    public PointOfInterest getEnterance(){
        ArrayList<PointOfInterest> possiblePOIs = new ArrayList<PointOfInterest>();
        double currentScore = 0;
        double totalScore = 0;

        for(PointOfInterest poi : pois){
            if(poi.type == PointOfInterest.TYPE_ENTERANCE){
                possiblePOIs.add(poi);
            }
        }
        Random rgen = new Random();
        int randomIndex = rgen.nextInt(possiblePOIs.size());

        return possiblePOIs.get(randomIndex);
    }
}
