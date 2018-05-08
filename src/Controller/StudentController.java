package Controller;

import Model.CampusTime;
import Model.DataReader;
import Model.Student;

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

        ArrayList<Student> readerStudents = reader.getStudents();

        for(Student s : readerStudents){
            s.initializeStudent(simTime);
        }
    }

    public void simulateStudents(CampusTime simTime,double simSpeed){

    }
}
