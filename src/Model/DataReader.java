package Model;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.File;
import java.util.Date;


public class DataReader {

    BufferedReader br = null;

    public DataReader() {

    }

    public ArrayList<LocationData> locationsBetween(String path, Date starting_date, Date finishing_date){
        ArrayList<LocationData> locations = new ArrayList<>();
        if(starting_date.before(finishing_date)){
            ArrayList<LocationData> searchingLocations = null;
            if(Files.isDirectory(Paths.get(path))){
                searchingLocations = allLocationsInDirectory(path);
            }
            else if(Files.isRegularFile(Paths.get(path))){
                if(path.contains(".csv"))
                    searchingLocations = locationsAtFile(path);
                else
                    System.out.println("This is not a .csv file");
            }
            if(searchingLocations != null){
                for(int i = 0; i <  searchingLocations.size(); i++){
                    if(starting_date.before(searchingLocations.get(i).getDate()) && finishing_date.after(searchingLocations.get(i).getDate()))
                        locations.add(searchingLocations.get(i));
                }
            }
        }
        else{
            System.out.println("Finishing date must come after starting date!");
        }
        return locations;
    }

    public ArrayList<LocationData>  allLocationsInDirectory(String directory_path) {
        File folder = new File(directory_path);
        ArrayList<LocationData> allLocations = new ArrayList<>();
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".csv");
            }
        });
        if(files != null && files.length > 0){
            for(int i=0; i < folder.listFiles().length; i++){
                if(folder.listFiles()[i].isFile())
                    allLocations.addAll(locationsAtFile(folder.listFiles()[i].getAbsolutePath()));
                else if(folder.listFiles()[i].isDirectory())
                    allLocationsInDirectory(folder.listFiles()[i].getAbsolutePath());
            }
        }
        else{
            System.out.println("Folder doesn't contain a .csv file");
        }

        return allLocations;
    }

    public ArrayList<LocationData> locationsAtFile(String fileDir) {
        ArrayList<LocationData> locations = new ArrayList<>();
        String file_path = fileDir;
        String line = "";
        String splitCharacter = ";";
        try {
            br = new BufferedReader(new FileReader(file_path));
            while ((line = br.readLine()) != null) {
                // In each line of .csv file; first element is date, second element is X coordinate, third element is Y coordinate
                LocationData ld = new LocationData(getDate((line.split(splitCharacter)[0])), Double.parseDouble(line.split(splitCharacter)[1]), Double.parseDouble(line.split(splitCharacter)[2]));
                locations.add(ld);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return locations;
    }

    public Date getDate(String date_string) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date_string);
        }
        catch(ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }


}
