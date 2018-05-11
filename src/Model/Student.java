package Model;
import Controller.Configurations;
import Controller.MainController;
import Model.MapModel.Path;
import Model.MapModel.PointOfInterest;
import sun.applet.Main;

import java.awt.*;
import java.util.ArrayList;

public class Student {
    public ArrayList<Course> courses;

    public double locationNodeX,locationNodeY;
    public Path path;

    public int action = -1;
    public static final int ACTION_WALKING = 1;
    public static final int ACTION_WAITING = 2;
    public static final int ACTION_UNDECIDED = -1;
    public static final int ACTION_ATCOURSE = 3;

    public boolean hasCourseToday = false;
    public boolean isOnCampus = false;
    public boolean hasUpcomingCourse = false;
    public boolean isOnCourse;
    public Course currentCourse = null;
    public Course nextCourse = null;

    private static final int FALL_SEMESTER = 201710;
    private static final int SPRING_SEMESTER = 201720;

    ArrayList<Course> fall_courses;
    ArrayList<Course> spring_courses;

    int id;

    public Student(int id){
        this.id = id;
        fall_courses = new ArrayList<>();
        spring_courses = new ArrayList<>();
    }

    public void act(CampusTime time,double simSpeed){
        parseStudentStates(time);

    }

    public void paint(Graphics g){

    }

    public void initializeStudent(CampusTime time){
        courses = getCoursesAtDay(time.day);
        parseStudentStates(time);
        calculateCurrentLocation(time);
    }

    public void parseStudentStates(CampusTime time){
        isOnCourse = false;
        isOnCampus = false;
        hasUpcomingCourse = false;
        currentCourse = null;

        if(courses.size() == 0){
            hasCourseToday = false;
            return;
        }
        hasCourseToday = true;

        for(Course c : courses){

            double startTime = getCourseStartTime(c);
            double endTime = getCourseEndTime(c);
            double currentTime = getDoubleEncodedTime(time.hour,time.min);

            if(currentTime > startTime){
                isOnCampus = true;

                if(currentTime < endTime){
                    isOnCourse = true;
                    currentCourse = c;
                }
            }

            if(currentTime < endTime){
                hasUpcomingCourse = true;
            }
        }
    }

    public double getCourseStartTime(Course c){
        int startHour = Integer.parseInt(c.starting_time.split(":")[0]);
        int startMin = Integer.parseInt(c.starting_time.split(":")[1]);

        return getDoubleEncodedTime(startHour,startMin);
    }

    public double getCourseEndTime(Course c){
        int endHour = Integer.parseInt(c.finishing_time.split(":")[0]);
        int endMin = Integer.parseInt(c.finishing_time.split(":")[1]);

        return getDoubleEncodedTime(endHour,endMin);
    }

    public double getDoubleEncodedTime(int hour,int min){
        return hour+min/100.0;
    }



    public double getMinsTillNextCourse(CampusTime time){
        double currentTime = getDoubleEncodedTime(time.hour,time.min);
        this.nextCourse = null;

        double timeTillNextCourse = 10e9;
        if(isOnCourse) return 0;

        else{
            for(Course c : this.courses){
                double courseStart = getCourseStartTime(c);
                if(courseStart > currentTime){
                    double courseStartMin = convertToMins(courseStart);
                    double currentTimeMin = convertToMins(currentTime);


                    double timeDiff = courseStartMin - currentTimeMin;
                    if(timeDiff < timeTillNextCourse){
                        timeTillNextCourse=timeDiff;
                        this.nextCourse = c;
                    }
                }
            }
        }

        return timeTillNextCourse;

    }

    public double convertToMins(double encodedTime){
        int hour = (int)encodedTime;
        double mins = (encodedTime%1)*100;

        return hour*60+mins;
    }

    public void calculateCurrentLocation(CampusTime time){
        double minsTillNextCourse = getMinsTillNextCourse(time);
        MainController controller = MainController.getInstance();

        if(isOnCourse){
            try{
                PointOfInterest currentLoc = controller.poiController.GetPoiWithName(currentCourse.building);
            }
            catch(Exception e){
                System.out.println("Not found building = "+currentCourse.building);
                //Exception below should not be handled, it should lead into program crash if occurs for it cannot be fixed with code alone
                throw new RuntimeException("Building not found ("+currentCourse.getBuilding()+")");
            }

        }
        else if(isOnCampus){

        }
        else{

        }
    }

    public int getId() {
        return id;
    }

    public ArrayList<Course> getCoursesAtDay(int day){
        ArrayList<Course> courses = new ArrayList<Course>();
        ArrayList<Course> allCourses;

        if(Configurations.SIM_SEMESTER == Configurations.SEMESTER_FALL){
            allCourses = fall_courses;
        }
        else{
            allCourses = spring_courses;
        }

        for(Course c : allCourses){
            if(c.day == day){
                hasCourseToday = true;
                courses.add(c);
            }
        }

        return courses;

    }

    public ArrayList<Course> get_fall_courses() {
        return fall_courses;
    }

    public ArrayList<Course> get_spring_courses() {
        return spring_courses;
    }

    public void addCourse(Course course){
        int semester = course.getSemester();
        if(semester == FALL_SEMESTER){
            fall_courses.add(course);
        }
        else if(semester == SPRING_SEMESTER){
            spring_courses.add(course);
        }
        else{
            System.out.println("Error : Invalid Semester Code for Course Adding.");
        }
    }

}
