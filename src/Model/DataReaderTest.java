package Model;

import java.util.ArrayList;
import java.util.Date;

public class DataReaderTest {

    // where excel files located
    private static final String DIRECTORY = "C://Users//meliharici//Desktop//BİTİRME//4 günlük veri";


    public static void main(String[] args)
    {

        DataReader dr = new DataReader();


        ArrayList<LocationData> locs1 = dr.allLocationsInDirectory(DIRECTORY);

        // locations that placed in a specific file
        ArrayList<LocationData> locs2 = dr.locationsAtFile(DIRECTORY + "/2.59-25_Nisan_2017-1230-1430_campus.csv");


        Date starting = dr.getDate("2017-04-25 08:00:00");
        Date finishing = dr.getDate("2017-04-25 08:45:00");

        // locations between two different time
        ArrayList<LocationData> locs3 = dr.locationsBetween(DIRECTORY + "/2.57-25_Nisan_2017-0800-1030_campus.csv", starting, finishing);   // search in a specific file
        ArrayList<LocationData> locs4 = dr.locationsBetween(DIRECTORY , starting, finishing); // search in a directory


        // simple print function to observe results.

        ArrayList<LocationData> currentTestData = locs4;

        for(int i=0; i < currentTestData.size(); i++){
            System.out.println(currentTestData.get(i).getDate().toString() + "    |  X : " + currentTestData.get(i).getX() + "   -   Y : " + currentTestData.get(i).getY());
        }

        System.out.println("\n\n");
        System.out.println(currentTestData.size() + " LOCATIONS FOUND.");

    }
}
