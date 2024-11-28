package Components.Registration;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class RegistrationMain {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
        long componentId = eventBus.register();
        System.out.println("** RegistrationMain(ID:" + componentId + ") is successfully registered.\n");

        RegistrationComponent registrationList = new RegistrationComponent();
        Event event = null;
        boolean done = false;

        while (!done) {
            try {
            	Thread.sleep(1000);
            }catch(InterruptedException e) {
            	e.printStackTrace();
            }
        	EventQueue eventQueue = eventBus.getEventQueue(componentId);
            for (int i = 0; i < eventQueue.getSize(); i++) {
                event = eventQueue.getEvent();
                switch (event.getEventId()) {
                    case Registration:
                        printLogEvent("Get", event);
                        eventBus.sendEvent(new Event(EventId.ClientOutput, addRegistration(registrationList, event.getMessage())));
                        break;
                    case ListRegistration: 
                        printLogEvent("Get", event);
                        eventBus.sendEvent(new Event(EventId.ClientOutput, getAllRegistrations(registrationList)));
                        break;
                    case QuitTheSystem:
                        printLogEvent("Get", event);
                        eventBus.unRegister(componentId);
                        done = true;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static String addRegistration(RegistrationComponent registrationList, String message) {
        // 메시지 형식: "StudentID CourseID"
        String[] tokens = message.split(" ");
        String studentId = tokens[0];
        String courseId = tokens[1];

        if (registrationList.isAlreadyRegistered(studentId, courseId)) {
            return "Registration failed: Student " + studentId + " is already registered for Course " + courseId;
        }

        registrationList.addRegistration(message);
        return "Registration successful: Student " + studentId + " is now registered for Course " + courseId;
    }

    private static String getAllRegistrations(RegistrationComponent registrationList) {
    	String returnString = "";
		for (int j = 0; j < registrationList.vRegistration.size(); j++) {
			returnString += registrationList.getAllRegistrations().get(j).getString() + "\n";
		}
		return returnString;
    }

    private static void printLogEvent(String comment, Event event) {
        System.out.println("\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
    }
}