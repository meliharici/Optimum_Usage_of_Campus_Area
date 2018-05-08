package Model;
import Controller.Configurations;
import Model.MapModel.Path;

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

    public void initializeStudent(CampusTime time){
        courses = getCoursesAtDay(time.day);
        parseStudentStates(time);
        getMinsTillNextCourse(time);
    }

    public void parseStudentStates(CampusTime time){
        isOnCourse = false;
        isOnCampus = false;
        hasUpcomingCourse = false;

        if(courses.size() == 0){
            hasCourseToday = false;
            return;
        }
        hasCourseToday = true;

        for(Course c : courses){
            int startHour = Integer.parseInt(c.starting_time.split(":")[0]);
            int startMin = Integer.parseInt(c.starting_time.split(":")[1]);

            int endHour = Integer.parseInt(c.finishing_time.split(":")[0]);
            int endMin = Integer.parseInt(c.finishing_time.split(":")[1]);

            double startTime = getDoubleEncodedTime(startHour,startMin);
            double endTime = getDoubleEncodedTime(endHour,endMin);
            double currentTime = getDoubleEncodedTime(time.hour,time.min);

            if(currentTime > startTime){
                isOnCampus = true;

                if(currentTime < endTime){
                    isOnCourse = true;
                }
            }

            if(currentTime < endTime){
                hasUpcomingCourse = true;
            }
        }
    }

    public double getDoubleEncodedTime(int hour,int min){
        return hour+min/100.0;
    }

    public int getMinsTillNextCourse(CampusTime time){
        if(isOnCourse) return 0;

        return -1;

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
