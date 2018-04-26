package Model;

public class CampusTime {

    static final int MONDAY = 1;
    static final int TUESDAY = 2;
    static final int WEDNESDAY = 3;
    static final int THURSDAY = 4;
    static final int FRIDAY = 5;
    static final int SATURDAY = 6;
    static final int SUNDAY = 7;

    int hour, min;
    String day;

    public CampusTime(int hour, int min, String day){
        this.day = day;
        this.hour = hour;
        this.min = min;
    }

    public int getHour(){ return hour; }

    public int getMin(){ return min; }

    public int getDay(){
        int day_code = -1;
        switch (day) {
            case "Monday":
                day_code = MONDAY;
                break;
            case "Tuesday":
                day_code = TUESDAY;
                break;
            case "Wednesday":
                day_code = WEDNESDAY;
                break;
            case "Thursday":
                day_code = THURSDAY;
                break;
            case "Friday":
                day_code = FRIDAY;
                break;
            case "Saturday":
                day_code = SATURDAY;
                break;
            case "Sunday":
                day_code = SUNDAY;
                break;
            default:
                System.out.println("Wrong Day Value");
                return -1;
        }
        return day_code;
    }
}
