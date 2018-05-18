package Controller;
import Model.MapModel.CampusMap;
import Model.MapModel.Path;
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
                Path path = CampusMap.getCampusMap().findPath((int)x,(int)y,poi.xCoords,poi.yCoords);
                totalScore += poi.preferance*100+100-poi.distancePenalty*path.getPathLength()/2;
            }
        }
        Random rgen = new Random();
        currentScore = rgen.nextDouble()*totalScore;

        for(PointOfInterest poi : possiblePOIs){
            Path path = CampusMap.getCampusMap().findPath((int)x,(int)y,poi.xCoords,poi.yCoords);
            double poiScore = poi.preferance*100+100-poi.distancePenalty*path.getPathLength()/2;
            if(currentScore<poiScore){
                return poi;
            }
            else{
                currentScore -= poiScore;
            }
        }

        System.out.println("Should not happen");
        return possiblePOIs.get(0);
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
