package Controller;

import Model.CampusTime;
import Model.DataReader;
import Model.Student;

import java.awt.*;
import java.util.ArrayList;

public class StudentController {
    public ArrayList<Student> students;
    private DataReader reader;

    public StudentController(){
        reader = new DataReader();
        students = new ArrayList<Student>();
    }

    public void loadData(){
        reader.readCourses(Configurations.COURSE_FILEPATH);
        reader.readStudents(Configurations.STUDENT_FILEPATH);
    }

    public void initializeStudents(CampusTime simTime){
        System.out.println("HEllo there");

        students = reader.getStudents();

        for(Student s : students){
            s.initializeStudent(simTime);
        }
    }

    public void simulateStudents(CampusTime simTime,double simSpeed){
        for(Student s : students){
            s.act(simTime,simSpeed);
        }
    }

    public void paintStudents(Graphics g, int x, int y, double zoomFactor){
        for(Student s:students){
            s.paint(g,x,y,zoomFactor);
        }
    }
}
