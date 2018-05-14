package Model;
import Controller.Configurations;
import Controller.MainController;
import Controller.POIController;
import Model.MapModel.CampusMap;
import Model.MapModel.Node;
import Model.MapModel.Path;
import Model.MapModel.PointOfInterest;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Student {
    public ArrayList<Course> courses;

    public double locationNodeX,locationNodeY;
    public double paintOffsetX,paintOffsetY;

    public Path path;

    public int action = -1;
    public int nextAction = -1;
    public static final int ACTION_WALKING = 1;
    public static final int ACTION_WAITING = 2;
    public static final int ACTION_UNDECIDED = -1;
    public static final int ACTION_ATCOURSE = 3;
    public static final int ACTION_DISAPPEAR = 4;

    public boolean hasCourseToday = false;
    public boolean isOnCampus = false;
    public boolean hasUpcomingCourse = false;
    public boolean isOnCourse;

    public int foodState = -1;
    public final int FOOD_EATEN = 1;
    public final int FOOD_HUNGRY = 2;
    public final int FOOD_EATING = 3;

    public final int PERCENTAGE_STUDY = 20;
    public final int PERCENTAGE_SCACK = 30;
    public final int PERCENTAGE_FREEZONE = 50;

    public Course currentCourse = null;
    public Course nextCourse = null;
    public Course pastCourse = null;
    public Course earliestCourse =  null;
    public Course latestCourse = null;

    public PointOfInterest currentPOI = null;
    public double poiLeaveTime = -1;

    private static final int FALL_SEMESTER = 201710;
    private static final int SPRING_SEMESTER = 201720;

    ArrayList<Course> fall_courses;
    ArrayList<Course> spring_courses;

    Random rand;

    int id;

    public Student(int id){
        this.id = id;
        fall_courses = new ArrayList<>();
        spring_courses = new ArrayList<>();
        rand = new Random();
    }

    public void act(CampusTime time,double simSpeed){
        parseStudentStates(time);

        if(hasCourseToday){
            if(isOnCampus){
                if(action==ACTION_WAITING){

                }
                else if(action == ACTION_WALKING){
                    if(this.path.isDestinationReached()){

                    }
                    else{
                        walk(simSpeed);
                    }
                }
            }
        }

    }

    public void walk(double simSpeed){
        this.path.move(simSpeed);
        this.locationNodeX = this.path.getCurrentNodeOnMap()[0];
        this.locationNodeY =this.path.getCurrentNodeOnMap()[1];
    }

    public void paint(Graphics g, int x, int y, double zoomFactor){
        if(!hasCourseToday){
            return;
        }

        if(isOnCampus){
            if(!isOnCourse){
                g.setColor(Color.RED);
            }
            else{
                //On course
                g.setColor(Color.BLUE);
            }

            double finalSize = Node.NODE_SIZE*zoomFactor*0.50;
            double paintX = (this.locationNodeX+0.5+paintOffsetX)* Node.NODE_SIZE*zoomFactor+x;
            double paintY = (this.locationNodeY+0.5+paintOffsetY)* Node.NODE_SIZE*zoomFactor+y;
            g.fillRect((int)paintX,(int)paintY,(int)finalSize,(int)finalSize);
        }


    }

    public void calculateOffsetOnCurrentPOI(){
        this.paintOffsetX = (rand.nextDouble()*2-1)*currentPOI.size;
        this.paintOffsetY = (rand.nextDouble()*2-1)*currentPOI.size;
    }

    public void calculateOffsetOnWalking(){
        this.paintOffsetX = rand.nextDouble()-0.5;
        this.paintOffsetY = rand.nextDouble()-0.5;
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

            if(currentTime < startTime){
                hasUpcomingCourse = true;
            }

        }
    }

    public void calculateFoodState(CampusTime time){


        for(Course c:courses){

        }

        this.foodState = FOOD_EATING;
        this.foodState = rand.nextInt(3)+1;

    }

    public static double getCourseStartTime(Course c){
        int startHour = Integer.parseInt(c.starting_time.split(":")[0]);
        int startMin = Integer.parseInt(c.starting_time.split(":")[1]);

        return getDoubleEncodedTime(startHour,startMin);
    }

    public static double getCourseEndTime(Course c){
        int endHour = Integer.parseInt(c.finishing_time.split(":")[0]);
        int endMin = Integer.parseInt(c.finishing_time.split(":")[1]);

        return getDoubleEncodedTime(endHour,endMin);
    }

    public static double getDoubleEncodedTime(int hour,int min){
        return hour+min/100.0;
    }



    public double calculateNextCourse(CampusTime time){
        double currentTime = getDoubleEncodedTime(time.hour,time.min);
        this.nextCourse = null;
        hasUpcomingCourse = false;

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
                        hasUpcomingCourse = true;
                    }
                }
            }
        }

        return timeTillNextCourse;

    }

    public static double convertToMins(double encodedTime){
        int hour = (int)encodedTime;
        double mins = (encodedTime%1)*100;

        return hour*60+mins;
    }

    public static double convertMinsToEncoded(double mins){
        double hour = (int) (mins/60);
        double minute = (int) (mins%60);

        return hour+(minute/100.0);
    }

    public static double addTwoEncodedTimes(double time1,double time2){
        double minTime1 = convertToMins(time1);
        double minTime2 = convertToMins(time2);

        return convertMinsToEncoded(minTime1+minTime2);
    }

    public static double getHourFromEncodedTime(double encodedTime){
        return (int)encodedTime;
    }

    public static double getMinsFromEncodedTime(double encodedTime){
        return (encodedTime%1)*100;
    }

    public void calculateCurrentLocation(CampusTime time){
        CampusMap map = CampusMap.getCampusMap();
        calculateFoodState(time);

        double minsTillNextCourse = calculateNextCourse(time);
        MainController controller = MainController.getInstance();

        double currentTimeEncoded = getDoubleEncodedTime(time.hour,time.min);

        if(isOnCourse){
            try{
                PointOfInterest currentLoc = controller.poiController.GetPoiWithName(currentCourse.building);
                this.locationNodeX = currentLoc.xCoords;
                this.locationNodeY = currentLoc.yCoords;
                this.action = ACTION_ATCOURSE;
                this.nextAction = ACTION_UNDECIDED;
                this.currentPOI = currentLoc;
                calculateOffsetOnCurrentPOI();

                System.out.println("--------");
                System.out.println("At Course = "+currentCourse.course_name);
            }
            catch(Exception e){
                //Exception below should not be handled, it should lead into program crash if occurs for it cannot be fixed with code alone
                throw new RuntimeException("Building not found ("+currentCourse.getBuilding()+")");
            }

        }
        else if(isOnCampus){

            double minsAfterPastCourse = calculatePastCourse(time);

            if(hasUpcomingCourse){
                double minsBetweenCourses = minsAfterPastCourse+minsTillNextCourse;

                //TODO
                //Calculate the previous location
                PointOfInterest previousLocation = null;
                PointOfInterest pastCourseLocation = controller.poiController.GetPoiWithName(pastCourse.building);
                PointOfInterest nextLocation = controller.poiController.GetPoiWithName(nextCourse.building);

                if(minsBetweenCourses < 15){
                    previousLocation = controller.poiController.GetPoiWithName(pastCourse.building);
                    nextLocation = controller.poiController.GetPoiWithName(this.nextCourse.building);
                }
                else if(minsAfterPastCourse < 10){
                    previousLocation = pastCourseLocation;

                    if(foodState == FOOD_HUNGRY){
                        System.out.println("Food not eaten");
                        nextLocation = controller.poiController.getPOIWithType(previousLocation.xCoords,previousLocation.yCoords,PointOfInterest.TYPE_MAINFOOD);
                    }
                    else if(foodState == FOOD_EATEN){
                        nextLocation = getFreePOI(previousLocation);
                    }
                    else{
                        nextLocation = getFreePOI(previousLocation);
                    }
                }
                else{
                    if(foodState == FOOD_EATING){
                        previousLocation = controller.poiController.getPOIWithType(pastCourseLocation.xCoords,pastCourseLocation.yCoords,PointOfInterest.TYPE_MAINFOOD);
                    }
                    else{
                        previousLocation = controller.poiController.getPOIWithType(pastCourseLocation.xCoords,pastCourseLocation.yCoords,PointOfInterest.TYPE_FREEZONE);
                    }

                }

                if(minsTillNextCourse<15){
                    //TODO
                    //Walk towards next course
                    //Next action = on course
                    if(previousLocation != nextLocation){
                        action = ACTION_WALKING;
                        nextAction = ACTION_WAITING;
                        this.path = map.findPath(previousLocation.xCoords,previousLocation.yCoords,nextLocation.xCoords,nextLocation.yCoords);
                        double randProgres = rand.nextDouble()/2+0.25;
                        double pathLength = path.getPathLength();
                        path.move(pathLength*randProgres);

                        this.locationNodeX = path.getCurrentNodeOnMap()[0];
                        this.locationNodeY = path.getCurrentNodeOnMap()[1];

                        calculateOffsetOnWalking();


                        System.out.println("--------");
                        System.out.println("Walking from ("+previousLocation.name+") to ("+nextLocation.name+")");
                        System.out.println("Walking from ("+previousLocation.xCoords+","+previousLocation.yCoords+") to " +
                                "("+nextLocation.xCoords+","+nextLocation.yCoords+")");
                        System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                        System.out.println("Progress = "+path.getPathProgressRatio());
                    }
                    else{

                        action = ACTION_WAITING;
                        nextAction = ACTION_ATCOURSE;
                        this.locationNodeX = previousLocation.xCoords;
                        this.locationNodeY = previousLocation.yCoords;
                        poiLeaveTime = getCourseStartTime(nextCourse);
                        this.currentPOI = previousLocation;
                        calculateOffsetOnCurrentPOI();


                        System.out.println("--------");
                        System.out.println("Waiting on ("+previousLocation.name+")");
                        System.out.println("Waiting till = "+poiLeaveTime);
                    }

                }
                else{
                    if(minsAfterPastCourse<10){
                        System.out.println("--------");
                        System.out.println("Walking from ("+previousLocation.name+") to ("+nextLocation.name+")");
                        System.out.println("Walking from ("+previousLocation.xCoords+","+previousLocation.yCoords+") to " +
                                "("+nextLocation.xCoords+","+nextLocation.yCoords+")");
                        System.out.println("Mins between courses = "+minsBetweenCourses);
                        System.out.println("Mins till next Course = "+minsTillNextCourse);

                        action = ACTION_WALKING;
                        nextAction = ACTION_WAITING;
                        this.path = map.findPath(previousLocation.xCoords,previousLocation.yCoords,nextLocation.xCoords,nextLocation.yCoords);
                        double randProgres = rand.nextDouble()/2+0.25;
                        double pathLength = path.getPathLength();
                        path.move(pathLength*randProgres);
                        this.locationNodeX = path.getCurrentNodeOnMap()[0];
                        this.locationNodeY = path.getCurrentNodeOnMap()[1];
                        calculateOffsetOnWalking();


                        System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                        System.out.println("Progress = "+path.getPathProgressRatio());

                    }
                    else if(minsAfterPastCourse>=10){
                        action = ACTION_WAITING;
                        nextAction = ACTION_UNDECIDED;
                        this.locationNodeX = previousLocation.xCoords;
                        this.locationNodeY = previousLocation.yCoords;
                        this.currentPOI = previousLocation;
                        this.poiLeaveTime = addTwoEncodedTimes(currentTimeEncoded,rand.nextDouble()/5);
                        calculateOffsetOnCurrentPOI();

                        System.out.println("--------");
                        System.out.println("Waiting at a POI");
                        System.out.println("POI name = "+this.currentPOI.name);
                        System.out.println("Mins till next Course = "+minsTillNextCourse);
                        System.out.println("Current time = "+currentTimeEncoded);
                        System.out.println("POI leave time  ="+poiLeaveTime);
                    }


                }
            }
            else{
                if(minsAfterPastCourse>10){
                    //TODO
                    //Set student to dead
                }
                else{
                    //TODO
                    //Walk to exit
                }
            }

        }
        else{
            if(hasUpcomingCourse){
                //TODO
                //Spawn and walk towards next course if less than 20 mins
            }

        }
    }

    public PointOfInterest getFreePOI(PointOfInterest previousLocation){
        int randInt = rand.nextInt(100);
        POIController poiController = MainController.getInstance().poiController;

        if(randInt<PERCENTAGE_FREEZONE){
            return poiController.getPOIWithType(previousLocation.xCoords,previousLocation.yCoords,PointOfInterest.TYPE_FREEZONE);
        }
        else if(randInt<PERCENTAGE_FREEZONE+PERCENTAGE_SCACK){
            return poiController.getPOIWithType(previousLocation.xCoords,previousLocation.yCoords,PointOfInterest.TYPE_SNACKFOOD);
        }
        else{
            return poiController.getPOIWithType(previousLocation.xCoords,previousLocation.yCoords,PointOfInterest.TYPE_STUDYAREA);
        }
    }

    public double calculatePastCourse(CampusTime time){
        double currentTime = getDoubleEncodedTime(time.hour,time.min);
        this.pastCourse = null;

        double timeBetweenCourses = 10e9;
        if(isOnCourse) return 0;

        else{
            for(Course c : this.courses){
                double courseStart = getCourseStartTime(c);
                double courseEnd = getCourseEndTime(c);
                if(courseEnd < currentTime){
                    double courseEndMin = convertToMins(courseEnd);
                    double currentTimeMin = convertToMins(currentTime);


                    double timeDiff = currentTimeMin - courseEndMin;
                    if(timeDiff < timeBetweenCourses){
                        timeBetweenCourses=timeDiff;
                        this.pastCourse = c;
                    }
                }
            }
        }

        return timeBetweenCourses;
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
