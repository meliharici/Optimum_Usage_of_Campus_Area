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
    public ArrayList<Course> courses;
    public ArrayList<Student> students;
    private static final String splitCharacter = ",";

    public DataReader() {

        courses = new ArrayList<>();
        students = new ArrayList<>();
    }


    // 201710,MATH,104,A,Çarşamba,10:40,12:30,CK.EF_AB1.238
    public void readCourses(String fileDir){
        String file_path = fileDir;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(file_path));
            boolean headpassed = false;
            while ((line = br.readLine()) != null) {
                if(headpassed){
                    if(line.split(splitCharacter).length == 8){
                        int semester = Integer.parseInt(line.split(splitCharacter)[0]);
                        String course_name = line.split(splitCharacter)[1];
                        String course_no = line.split(splitCharacter)[2];
                        String partition = line.split(splitCharacter)[3];
                        String day = line.split(splitCharacter)[4];
                        String starting_time = line.split(splitCharacter)[5];
                        String finishing_time = line.split(splitCharacter)[6];
                        String room_code = line.split(splitCharacter)[7];
                        Course c = new Course(semester, course_name, partition, course_no, day, room_code, starting_time, finishing_time);
                        courses.add(c);
                    }
                }
                headpassed = true;
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
    }




    /*
    *
    *   Student IDs goes like 1,2,3,4,5,......
    *   I added students to the students array list by looking IDs.
    *   For each iteration, checked if (current_id == size(students)).
    *   if not, add a new student object into the array list.
    *   Otherwise, just updated the student object at index (current_id - 1)
    *
    * */

    public void readStudents(String fileDir){
        String file_path = fileDir;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(file_path));
            boolean headpassed = false;
            while ((line = br.readLine()) != null) {
                if(headpassed){
                    if(line.split(splitCharacter).length == 6){
                        int semester = Integer.parseInt(line.split(splitCharacter)[0]);
                        int id = Integer.parseInt(line.split(splitCharacter)[1]);
                        String course_name = line.split(splitCharacter)[2];
                        String course_no = line.split(splitCharacter)[3];
                        String partition = line.split(splitCharacter)[4];

                        //System.out.println("Semester : " + semester + "  course_name : " + course_name + " course_no : " + course_no + "  partition : " + partition);

                        Course c = getCourse(semester, course_name, course_no, partition);

                        if(c != null){
                            // students already added
                            if(id == students.size()){
                                students.get(id - 1).addCourse(c);
                            }
                            else{
                                Student student = new Student(id);
                                student.addCourse(c);
                                students.add(student);
                            }
                        }
                        else{
                            System.out.println("Error : Course could not found!");
                        }
                    }
                }
                headpassed = true;
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

    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }



    public Course getCourse(int semester, String courseName, String course_no , String partition ){
        Course course = null;
        for(int i = 0; i < courses.size(); i++){
            Course c = courses.get(i);
            if(c.semester == semester && c.course_name.equals(courseName) && c.course_no.equals(course_no) && c.partition.equals(partition)){
                course = c;
                break;
            }
        }
        return course;
    }



    // BELOW LINES WILL BE DELETED!!!
    /*----------------------------------------------------------------*/


    public ArrayList<LocationData> locationsAtFile(String fileDir) {
        ArrayList<LocationData> locations = new ArrayList<>();
        String file_path = fileDir;
        String line = "";
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
