package Components.Registration;

public class Registration {
    private String studentId;  
    private String courseId;   
    
    public Registration(String inputString) {
        String[] tokens = inputString.split(" ");
        this.studentId = tokens[0];
        this.courseId = tokens[1];
    }
    public String getStudentId() {
        return studentId;
    }
    public String getCourseId() {
        return courseId;
    }
    public boolean match(String studentId, String courseId) {
        return this.studentId.equals(studentId) && this.courseId.equals(courseId);
    }
    public String getString() {
        return "Student ID: " + this.studentId + ", Course ID: " + this.courseId;
    }
}