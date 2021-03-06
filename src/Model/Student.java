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
    double movementSpeedRatio = 1;
    public static final boolean shouldPrint = false;

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

    public PointOfInterest currentLoc = null;
    public PointOfInterest previousLoc = null;
    public PointOfInterest nextLoc = null;
    public PointOfInterest enterancePOI = null;

    public double poiLeaveTime = -1;

    private static final int FALL_SEMESTER = 201710;
    private static final int SPRING_SEMESTER = 201720;

    ArrayList<Course> fall_courses;
    ArrayList<Course> spring_courses;

    Random rand;

    int id;

    public void initializeParameters(){
        action = -1;
        nextAction = -1;
        poiLeaveTime = -1;
        currentLoc = null;
        previousLoc = null;
        nextLoc = null;
        enterancePOI = null;
        foodState = -1;
    }

    public Student(int id){
        this.id = id;
        fall_courses = new ArrayList<>();
        spring_courses = new ArrayList<>();
        rand = new Random();
        this.movementSpeedRatio = 1+rand.nextDouble()/5.0;
    }

    public void act(CampusTime time,double simSpeed){
        parseStudentStates(time);

        double currentTimeEncoded = getDoubleEncodedTime(time.getHour(),time.getMin());

        if(hasCourseToday){
            if(isOnCampus){
                if(action==ACTION_WAITING){
                    if(currentTimeEncoded>poiLeaveTime){
                        changeActions(time);
                    }
                    else{
                        //Keep waiting
                    }

                }
                else if(action == ACTION_WALKING){
                    if(this.path.isDestinationReached()){
                        currentLoc = nextLoc;
                        changeActions(time);
                    }
                    else{
                        walk(simSpeed*movementSpeedRatio);
                    }
                }
                else if(action == ACTION_ATCOURSE){
                    if(currentTimeEncoded>poiLeaveTime){
                        changeActions(time);
                    }
                }
                else if(action == ACTION_UNDECIDED){
                    decideNextAction(time);
                }
                else if(action == ACTION_DISAPPEAR){

                }
            }
            else{
                //Not on campus , but will come
            }
        }

    }

    public void changeActions(CampusTime time){
        double minsTillNextCourse = calculateNextCourse(time);
        action = nextAction;

        switch(nextAction){
            case ACTION_WAITING:
                //Next action == waiting at POI

                currentLoc = nextLoc;
                calculateOffsetOnCurrentPOI();

                if(minsTillNextCourse>20){
                    poiLeaveTime = getEncodedRandomTimeWithBound(time,20,10);
                }
                else{
                    if(nextCourse == null){
                        if(currentLoc.name.equals(currentCourse.building)){
                            action = ACTION_ATCOURSE;
                            poiLeaveTime = getCourseEndTime(currentCourse);
                        }
                        else{
                            //Walk to course building
                            System.out.println("Waiting not on course");
                        }

                    }
                    else{
                        poiLeaveTime = getCourseStartTime(nextCourse)-0.05;
                        nextAction = ACTION_UNDECIDED;
                    }

                }

                nextAction = ACTION_UNDECIDED;
                break;
            case ACTION_WALKING:
                throw new RuntimeException("This shouldnt happen");

            case ACTION_ATCOURSE:
                currentCourse = nextCourse;
                currentLoc = nextLoc;
                nextAction = ACTION_UNDECIDED;
                isOnCourse = true;
                calculateOffsetOnCurrentPOI();

                break;
            case ACTION_DISAPPEAR:
                break;
            case ACTION_UNDECIDED:
                break;
        }
    }

    public double getEncodedRandomTimeWithBound(CampusTime currentTime,double bound,double threshold){
        double randAddition = rand.nextDouble()*(bound)+(threshold);
        double encodedTimeMins = convertToMins(getDoubleEncodedTime(currentTime.getHour(),currentTime.getMin()));

        return convertMinsToEncoded(randAddition+encodedTimeMins);
    }

    public void decideNextAction(CampusTime time){
        CampusMap map = CampusMap.getCampusMap();
        if(currentLoc == null){
            currentLoc = getEnterancePOI();
        }

        if(isOnCourse){
            if(currentLoc.name.equals(currentCourse.building)){
                action = ACTION_ATCOURSE;
                nextAction = ACTION_UNDECIDED;
                poiLeaveTime = getCourseEndTime(currentCourse);
                if (shouldPrint) {
                    System.out.println("- - - - - ");
                    System.out.println("At course at ("+ currentLoc.name+")");
                    System.out.println("Will leave course at ("+poiLeaveTime+")");
                }

            }
            else{
                action = ACTION_WALKING;
                nextAction = ACTION_WAITING;
                nextLoc = MainController.getInstance().poiController.GetPoiWithName(currentCourse.building);
                this.path = map.findPath(currentLoc.xCoords, currentLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                calculateOffsetOnWalking();

                if (shouldPrint) {
                    System.out.println("- - - - - ");
                    System.out.println("Walking from ("+ currentLoc.name+") to ("+nextLoc.name+")");
                }

            }
        }
        else if(hasUpcomingCourse){
            //not on course
            double minsTillNextCourse = calculateNextCourse(time);
            if(minsTillNextCourse < 21){
                if(currentLoc.name.equals(nextCourse.building)){
                    action = ACTION_WAITING;
                    nextAction = ACTION_UNDECIDED;
                    poiLeaveTime = getCourseStartTime(nextCourse);

                    if (shouldPrint) {
                        System.out.println("- - - - - ");
                        System.out.println("Waiting for course at ("+ currentLoc.name+")");
                        System.out.println("Will change state at ("+poiLeaveTime+")");
                    }

                }
                else{
                    action = ACTION_WALKING;
                    nextAction = ACTION_WAITING;
                    nextLoc = MainController.getInstance().poiController.GetPoiWithName(nextCourse.building);
                    this.path = map.findPath(currentLoc.xCoords, currentLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                    calculateOffsetOnWalking();

                    if (shouldPrint) {
                        System.out.println("- - - - - ");
                        System.out.println("Walking from ("+ currentLoc.name+") to ("+nextLoc.name+")");
                    }

                }
            }
            else{
                if(foodState == FOOD_HUNGRY){
                    action = ACTION_WALKING;
                    nextAction = ACTION_WAITING;
                    nextLoc = MainController.getInstance().poiController.getPOIWithType(currentLoc.xCoords, currentLoc.yCoords,PointOfInterest.TYPE_MAINFOOD);
                    this.foodState = FOOD_EATEN;
                    poiLeaveTime = getEncodedRandomTimeWithBound(time,20,25);
                    this.path = map.findPath(currentLoc.xCoords, currentLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                    calculateOffsetOnWalking();

                    if (shouldPrint) {
                        System.out.println("- - - - - ");
                        System.out.println("Walking from ("+ currentLoc.name+") to ("+nextLoc.name+")");
                        System.out.println("Will change state at ("+poiLeaveTime+")");
                    }

                }
                else{
                    action = ACTION_WALKING;
                    nextAction = ACTION_WAITING;
                    nextLoc = getFreePOI(currentLoc);
                    poiLeaveTime = getEncodedRandomTimeWithBound(time,15,5);
                    this.path = map.findPath(currentLoc.xCoords, currentLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                    calculateOffsetOnWalking();

                    if (shouldPrint) {
                        System.out.println("- - - - - ");
                        System.out.println("Walking from ("+ currentLoc.name+") to ("+nextLoc.name+")");
                        System.out.println("Will change state at ("+poiLeaveTime+")");
                    }

                }
            }

        }
        else{
            action = ACTION_WALKING;
            nextAction = ACTION_DISAPPEAR;
            nextLoc = getEnterancePOI();
            this.path = map.findPath(currentLoc.xCoords, currentLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
            calculateOffsetOnWalking();

            if (shouldPrint) {
                System.out.println("- - - - - ");
                System.out.println("Walking from ("+ currentLoc.name+") to ("+nextLoc.name+")");
            }

        }
    }

    public void walk(double simSpeed){
        this.path.move(simSpeed);
        this.locationNodeX = this.path.getCurrentNodeOnMap()[0];
        this.locationNodeY =this.path.getCurrentNodeOnMap()[1];
    }

    public void paint(Graphics g, int x, int y, double zoomFactor){

        if(action == ACTION_DISAPPEAR){
            return;
        }

        if(!hasCourseToday){
            return;
        }

        if(isOnCampus){
            if(action==ACTION_WALKING){
                g.setColor(Color.RED);
            }
            if(action == ACTION_WAITING){
                g.setColor(Color.GREEN);
            }
            if(action == ACTION_ATCOURSE){
                g.setColor(Color.BLUE);
            }
            if(action == ACTION_UNDECIDED){
                g.setColor(Color.BLACK);
            }

            double finalSize = Node.NODE_SIZE*zoomFactor*0.50;
            double paintX = (this.locationNodeX+0.5+paintOffsetX)* Node.NODE_SIZE*zoomFactor+x;
            double paintY = (this.locationNodeY+0.5+paintOffsetY)* Node.NODE_SIZE*zoomFactor+y;
            g.fillRect((int)paintX,(int)paintY,(int)finalSize,(int)finalSize);
        }


    }

    public void calculateOffsetOnCurrentPOI(){
        this.paintOffsetX = (rand.nextDouble()*2-1)* currentLoc.size;
        this.paintOffsetY = (rand.nextDouble()*2-1)* currentLoc.size;
    }

    public void calculateOffsetOnWalking(){
        this.paintOffsetX = rand.nextDouble()-0.5;
        this.paintOffsetY = rand.nextDouble()-0.5;
    }

    public void initializeStudent(CampusTime time){
        initializeParameters();
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

            if(currentTime > startTime-0.2){
                isOnCampus = true;
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
        int hour = (int)(encodedTime+1e-5);
        int mins = (int)(((encodedTime+1e-5)%1)*100);

        return hour*60+mins;
    }

    public static double convertMinsToEncoded(double mins){
        double hour = (int) (mins/60.0);
        double minute = (int) ((mins+1e-5)%60);

        return hour+(minute/100.0);
    }

    public static double addTwoEncodedTimes(double time1,double time2){
        double minTime1 = convertToMins(time1);
        double minTime2 = convertToMins(time2);

        return convertMinsToEncoded(minTime1+minTime2);
    }

    public static double getHourFromEncodedTime(double encodedTime){
        return (int)(encodedTime+1e-5);
    }

    public static double getMinsFromEncodedTime(double encodedTime){
        return (int)(((encodedTime+1e-5)%1.0)*100);
    }

    public void calculateCurrentLocation(CampusTime time){
        CampusMap map = CampusMap.getCampusMap();
        calculateFoodState(time);

        double minsTillNextCourse = calculateNextCourse(time);
        MainController controller = MainController.getInstance();

        double currentTimeEncoded = getDoubleEncodedTime(time.hour,time.min);

        //PointOfInterest currentLoc = null;

        if(isOnCourse){
            try{
                currentLoc = controller.poiController.GetPoiWithName(currentCourse.building);
                this.locationNodeX = currentLoc.xCoords;
                this.locationNodeY = currentLoc.yCoords;
                this.action = ACTION_ATCOURSE;
                this.nextAction = ACTION_UNDECIDED;
                this.currentLoc = currentLoc;
                poiLeaveTime = getCourseEndTime(currentCourse);
                calculateOffsetOnCurrentPOI();

                if(shouldPrint){
                    System.out.println("--------");
                    System.out.println("At Course = "+currentCourse.course_name);
                    System.out.println("Leave time = "+poiLeaveTime);
                }

            }
            catch(Exception e){
                //Exception below should not be handled, it should lead into program crash if occurs for it cannot be fixed with code alone
                throw new RuntimeException("Building not found ("+currentCourse.getBuilding()+")");
            }

        }
        else if(isOnCampus){

            double minsAfterPastCourse = calculatePastCourse(time);

            if(hasUpcomingCourse){

                if(pastCourse == null){
                    if(hasUpcomingCourse){
                        //TODO
                        //Spawn and walk towards next course if less than 20 mins
                        if(minsTillNextCourse<20){
                            isOnCampus = true;
                            this.enterancePOI = getEnterancePOI();
                            previousLoc = this.enterancePOI;
                            action = ACTION_WALKING;
                            nextAction = ACTION_WAITING;
                            poiLeaveTime = getCourseStartTime(nextCourse);
                            nextLoc = MainController.getInstance().poiController.GetPoiWithName(nextCourse.building);

                            this.path = map.findPath(previousLoc.xCoords,previousLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                            double randProgres = rand.nextDouble()/4+(20-minsTillNextCourse)/40;
                            double pathLength = path.getPathLength();
                            path.move(pathLength*randProgres);
                            this.locationNodeX = path.getCurrentNodeOnMap()[0];
                            this.locationNodeY = path.getCurrentNodeOnMap()[1];
                            calculateOffsetOnWalking();

                            if(shouldPrint){
                                System.out.println("--------");
                                System.out.println("Walking from ("+previousLoc.name+") to ("+nextLoc.name+")");
                                System.out.println("Walking from ("+previousLoc.xCoords+","+previousLoc.yCoords+") to " +
                                        "("+nextLoc.xCoords+","+nextLoc.yCoords+")");
                                System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                                System.out.println("Progress = "+path.getPathProgressRatio());
                            }

                        }
                        else{
                        }


                    }
                }
                else{
                    double minsBetweenCourses = minsAfterPastCourse+minsTillNextCourse;

                    //TODO
                    //Calculate the previous location
                    previousLoc = null;
                    PointOfInterest pastCourseLocation = controller.poiController.GetPoiWithName(pastCourse.building);
                    nextLoc = controller.poiController.GetPoiWithName(nextCourse.building);

                    if(minsBetweenCourses < 15){
                        previousLoc = controller.poiController.GetPoiWithName(pastCourse.building);
                        nextLoc = controller.poiController.GetPoiWithName(this.nextCourse.building);
                    }
                    else if(minsAfterPastCourse < 10){
                        previousLoc = pastCourseLocation;

                        if(foodState == FOOD_HUNGRY){
                            nextLoc = controller.poiController.getPOIWithType(previousLoc.xCoords,previousLoc.yCoords,PointOfInterest.TYPE_MAINFOOD);
                            this.foodState = FOOD_EATEN;
                        }
                        else if(foodState == FOOD_EATEN){
                            nextLoc = getFreePOI(previousLoc);
                        }
                        else{
                            nextLoc = getFreePOI(previousLoc);
                        }
                    }
                    else{
                        if(foodState == FOOD_EATING){
                            previousLoc = controller.poiController.getPOIWithType(pastCourseLocation.xCoords,pastCourseLocation.yCoords,PointOfInterest.TYPE_MAINFOOD);
                            this.foodState = FOOD_EATEN;
                        }
                        else{
                            previousLoc = getFreePOI(pastCourseLocation);
                        }

                    }

                    if(minsTillNextCourse<15){
                        //TODO
                        //Walk towards next course
                        //Next action = on course
                        if(previousLoc != nextLoc){
                            action = ACTION_WALKING;
                            nextAction = ACTION_WAITING;
                            this.path = map.findPath(previousLoc.xCoords,previousLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                            double randProgres = rand.nextDouble()/4+((15-minsTillNextCourse)/30);
                            double pathLength = path.getPathLength();
                            path.move(pathLength*randProgres);

                            this.locationNodeX = path.getCurrentNodeOnMap()[0];
                            this.locationNodeY = path.getCurrentNodeOnMap()[1];

                            calculateOffsetOnWalking();


                            if(shouldPrint){
                                System.out.println("--------");
                                System.out.println("Walking from ("+previousLoc.name+") to ("+nextLoc.name+")");
                                System.out.println("Walking from ("+previousLoc.xCoords+","+previousLoc.yCoords+") to " +
                                        "("+nextLoc.xCoords+","+nextLoc.yCoords+")");
                                System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                                System.out.println("Progress = "+path.getPathProgressRatio());
                            }

                        }
                        else{

                            action = ACTION_WAITING;
                            nextAction = ACTION_ATCOURSE;
                            this.locationNodeX = previousLoc.xCoords;
                            this.locationNodeY = previousLoc.yCoords;
                            poiLeaveTime = getCourseStartTime(nextCourse);
                            this.currentLoc = previousLoc;
                            calculateOffsetOnCurrentPOI();


                            if(shouldPrint){
                                System.out.println("--------");
                                System.out.println("Waiting on ("+previousLoc.name+")");
                                System.out.println("Waiting till = "+poiLeaveTime);
                            }

                        }

                    }
                    else{
                        if(minsAfterPastCourse<10){


                            action = ACTION_WALKING;
                            nextAction = ACTION_WAITING;
                            this.path = map.findPath(previousLoc.xCoords,previousLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                            double randProgres = rand.nextDouble()/2+0.25;
                            double pathLength = path.getPathLength();
                            path.move(pathLength*randProgres);
                            this.locationNodeX = path.getCurrentNodeOnMap()[0];
                            this.locationNodeY = path.getCurrentNodeOnMap()[1];
                            calculateOffsetOnWalking();


                            if(shouldPrint){
                                System.out.println("--------");
                                System.out.println("Walking from ("+previousLoc.name+") to ("+nextLoc.name+")");
                                System.out.println("Walking from ("+previousLoc.xCoords+","+previousLoc.yCoords+") to " +
                                        "("+nextLoc.xCoords+","+nextLoc.yCoords+")");
                                System.out.println("Mins between courses = "+minsBetweenCourses);
                                System.out.println("Mins till next Course = "+minsTillNextCourse);
                                System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                                System.out.println("Progress = "+path.getPathProgressRatio());
                            }


                        }
                        else if(minsAfterPastCourse>=10){
                            action = ACTION_WAITING;
                            nextAction = ACTION_UNDECIDED;
                            this.locationNodeX = previousLoc.xCoords;
                            this.locationNodeY = previousLoc.yCoords;
                            this.currentLoc = previousLoc;
                            this.poiLeaveTime = addTwoEncodedTimes(currentTimeEncoded,rand.nextDouble()/5);
                            calculateOffsetOnCurrentPOI();

                            if(shouldPrint){
                                System.out.println("--------");
                                System.out.println("Waiting at a POI");
                                System.out.println("POI name = "+this.currentLoc.name);
                                System.out.println("Mins till next Course = "+minsTillNextCourse);
                                System.out.println("Current time = "+currentTimeEncoded);
                                System.out.println("POI leave time  ="+poiLeaveTime);
                            }

                        }


                    }

                }

            }
            else{
                if(minsAfterPastCourse>19){
                    hasUpcomingCourse = false;
                    isOnCampus = false;
                    action = ACTION_DISAPPEAR;
                }
                else{
                    //Walking to exit
                    hasUpcomingCourse = false;
                    action = ACTION_WALKING;
                    nextAction = ACTION_DISAPPEAR;

                    PointOfInterest pastCourseLocation = controller.poiController.GetPoiWithName(pastCourse.building);

                    nextLoc = getEnterancePOI();
                    previousLoc = pastCourseLocation;

                    this.path = map.findPath(previousLoc.xCoords,previousLoc.yCoords,nextLoc.xCoords,nextLoc.yCoords);
                    double randProgres = rand.nextDouble()/4+(20-minsAfterPastCourse)/20;
                    double pathLength = path.getPathLength();
                    path.move(pathLength*randProgres);
                    this.locationNodeX = path.getCurrentNodeOnMap()[0];
                    this.locationNodeY = path.getCurrentNodeOnMap()[1];
                    calculateOffsetOnWalking();

                    if(shouldPrint){
                        System.out.println("--------");
                        System.out.println("Walking from ("+previousLoc.name+") to ("+nextLoc.name+")");
                        System.out.println("Walking from ("+previousLoc.xCoords+","+previousLoc.yCoords+") to " +
                                "("+nextLoc.xCoords+","+nextLoc.yCoords+")");
                        System.out.println("Curent location = ("+path.getCurrentNodeOnMap()[0]+","+path.getCurrentNodeOnMap()[1]+")");
                        System.out.println("Progress = "+path.getPathProgressRatio());
                    }

                }
            }

        }
        else{


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

    public PointOfInterest getEnterancePOI(){
        return MainController.getInstance().poiController.getEnterance();
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
