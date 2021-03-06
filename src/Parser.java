import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class parses the data from the files
 */
public class Parser {

    //the lesson file from which lessons and are going to be parsed
    private final static String LESSONS_PATH = "lessons.txt";
    //the lesson file from which teachers going to be parsed
    private final static String TEACHERS_PATH = "teachers.txt";

    /**
     * this method parses the rooms
     */
    public static List<Room> parseRooms() {
        List<Room> rooms = new ArrayList<Room>();
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(LESSONS_PATH));
            while ((line = bufferedReader.readLine()) != null) {
                String[] lineParams = line.split("#");
                for(int i = 2;i<lineParams.length;i++) {
                    String roomName = lineParams[i].split(",")[0];
                    Room room = new Room(roomName);
                    if(!rooms.contains(room)){
                        rooms.add(room);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * this method parses the teachers
     */
    public static List<Teacher> parseTeachers() {
        List<Teacher> teachers = new ArrayList<Teacher>();
        Teacher teacher;
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(TEACHERS_PATH));
            while ((line = bufferedReader.readLine()) != null) {
                teacher = new Teacher();
                String[] lineParams = line.split("#");
                teacher.setId(Integer.valueOf(lineParams[0]));
                teacher.setName(lineParams[1]);
                for(String lessonId : lineParams[2].split(",")) {
                    teacher.getLessons().add(Integer.valueOf(lessonId));
                }
                for(int i = 1; i <= Schedule.DAYS; i++){
                    if(teacher.getAvailableHoursPerDay() == null){
                        teacher.setAvailableHoursPerDay(new HashMap<String,Integer>(Schedule.DAYS));
                    }
                    teacher.getAvailableHoursPerDay().put(DayOfWeek.of(i).name(),Integer.valueOf(lineParams[3].split(",")[0]));
                }
                teacher.setAvailableHoursPerWeek(Integer.valueOf(lineParams[3].split(",")[1]));
                teachers.add(teacher);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teachers;
    }

    /**
     * this method parses the lessons and populates the teachers
     */
    public static List<Lesson> parseLessons(List<Teacher> teachers) {
        List<Lesson> lessons = new ArrayList<Lesson>();
        Lesson lesson;
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(LESSONS_PATH));
            while ((line = bufferedReader.readLine()) != null) {
                lesson = new Lesson();
                String[] lineParams = line.split("#");
                lesson.setId(Integer.valueOf(lineParams[0]));
                lesson.setName(lineParams[1]);
                for(int i = 2;i<lineParams.length;i++){
                    String roomName = lineParams[i].split(",")[0];
                    int hours = Integer.valueOf(lineParams[i].split(",")[1]);
                    if(lesson.getAvailableHoursPerRoom() == null){
                        lesson.setAvailableHoursPerRoom(new HashMap<Room, Integer>());
                    }
                    lesson.getAvailableHoursPerRoom().put(new Room(roomName),hours);
                }
                lesson.setTeachers(getTeachersForLesson(teachers, lesson.getId()));
                lessons.add(lesson);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lessons;
    }

    /**
     * this method finds a teacher from his ID
     */
    private static List<Teacher> getTeachersForLesson(List<Teacher> teachers, int lessonId){
        List<Teacher> teachersForLesson = new ArrayList<Teacher>();
        for(Teacher teacher : teachers){
            for(int lessId : teacher.getLessons()){
                if(lessId == lessonId){
                    teachersForLesson.add(teacher);
                }
            }
        }
        return teachersForLesson;
    }
}
