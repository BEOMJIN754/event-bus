package Components.Registration;

import java.util.ArrayList;

public class RegistrationComponent {
    protected ArrayList<Registration> vRegistration;
    
    public RegistrationComponent() {
    	 vRegistration = new ArrayList<>();
    }

    public void addRegistration(String inputString) {
        Registration registration = new Registration(inputString);
        vRegistration.add(registration);
    }

    public ArrayList<Registration> getAllRegistrations() {
    	return vRegistration;
    }

    public boolean isAlreadyRegistered(String studentId, String courseId) {
        for (Registration reg : vRegistration) {
            if (reg.match(studentId, courseId)) {
                return true;
            }
        }
        return false;
    }
}