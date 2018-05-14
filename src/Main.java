import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import Controller.*;
import GUI.*;
import GUI.AnalysisViewFiles.AnalysisView;
import Model.CampusTime;
import Model.Course;
import Model.DataReader;
import Model.DijkstraModel.Graph;
import Model.MapModel.CampusMap;
import Model.MapModel.Path;
import Model.Student;

public class Main {

    public static void main(String[] args){
        Configurations.initialize();

        MainController.createInstance();
        MainController controller = MainController.getInstance();
        MainView view = new MainView();

        controller.mainView = view;
        controller.initialize();

        StudentController studentController = new StudentController();
        studentController.loadData();

        controller.studentController = studentController;
        controller.start();



        //testStuff();

        //testStuff_dataread();

        testStuff_combobox_time();



    }


    public static void testStuff_combobox_time(){
        // params
        int hour = 20;
        int min = 55;
        String day = "Tuesday";

        CampusTime ct = new CampusTime(hour, min, day);
        AnalysisView.getInstance().menuPanel.updateSimulationTime(ct);
    }


    public static void testStuff_dataread(){
        DataReader reader = new DataReader();
        String course_path = "data/Courses.csv";
        String student_path = "data/Students.csv";

        // Fill Courses
        reader.readCourses(course_path);

        // Fill Students
        reader.readStudents(student_path);

        /*
        * WARNING: Order is important here. Courses must be filled before Students!
        * */

        ArrayList<Student> students = reader.getStudents();
        System.out.println("Number of Students : " + students.size());

        // Choose an example student to test.
        ArrayList<Course> fall_courses = students.get(15).get_fall_courses();
        for(int i = 0; i < fall_courses.size(); i++){
            System.out.println(fall_courses.get(i).getSemester() + "  " + fall_courses.get(i).getCourse_name() + " " + fall_courses.get(i).getCourse_no()+" "
            +fall_courses.get(i).getBuilding());
        }

    }


    public static void testStuff(){
        CampusMap cm = CampusMap.getCampusMap();
        cm.InitializeGraph();
        Path p = cm.findPath(259,161,269,156);

        System.out.println("Simulating movement with speed 0.5");
        for(int i = 0;i<20;i++){

            System.out.println(p);
            p.move(0.5);
        }

        p.reset();

        System.out.println("");
        System.out.println("Simulating movement with speed 3");

        for(int i = 0;i<20;i++){
            System.out.println(p);
            p.move(3);
        }
    }

}
