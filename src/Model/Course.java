package Model;


public class Course {

    private static final String CAMPUS_CODE = "CK";
    private static final String separator = ".";

    int semester;
    String  course_no;
    String course_name, partition, room_code;
    int day;
    String starting_time, finishing_time;

    String building, room;

    public Course(int semester, String course_name, String partition, String course_no, int day, String room_code, String starting_time, String finishing_time){
        this.semester = semester;
        this.course_name = course_name;
        this.partition = partition;
        this.course_no = course_no;
        this.day = day;
        this.room_code = room_code;
        this.starting_time = starting_time;
        this.finishing_time = finishing_time;
        parse_room_code();
    }

    private void parse_room_code(){
        if(room_code != null && room_code.length()<2&& room_code.length() > 10){
            String remaining = room_code;
            int inds1 = remaining.indexOf(separator);
            String location = remaining.substring(0, inds1);
            if(location.equals(CAMPUS_CODE)){
                remaining = remaining.substring(inds1 + 1);
                int inds2 = remaining.indexOf(separator);
                building = remaining.substring(0, inds2);
                remaining = remaining.substring(inds2 + 1);
                room = remaining;
                //System.out.println("Location : " + location + "  Building : " + building + "  Room : " + room);
            }
            else{
                //System.out.println("Error : Room Code -> Outside of the Campus");
            }
        }
        else{
            //System.out.println("Error : Invalid Argument for Room-Code Parsing.");
        }
    }

    public String getCourse_no() {
        return course_no;
    }

    public int getSemester() {
        return semester;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getPartition() {
        return partition;
    }

    public int get_Day() {
        return day;
    }

    public String getFinishing_time() {
        return finishing_time;
    }

    public String getStarting_time() {
        return starting_time;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoom() {
        return room;
    }


}
