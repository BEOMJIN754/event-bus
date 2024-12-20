/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Course;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Components.Student.Student;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class CourseMain {
	public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");

		CourseComponent coursesList = new CourseComponent("Courses.txt");
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case ListCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList(coursesList)));
					break;
				case RegisterCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerCourse(coursesList, event.getMessage())));
					break;
				case DeleteCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteCourse(coursesList, event.getMessage())));
					break;
				case ValidateCourse:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.Registration, validateCourse(coursesList, event.getMessage())));
					break;
				case QuitTheSystem:
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	
	private static String registerCourse(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			coursesList.vCourse.add(course);
			return "This course is successfully added.";
		} else
			return "This course is already registered.";
	}
	private static String makeCourseList(CourseComponent coursesList) {
		String returnString = "";
		for (int j = 0; j < coursesList.vCourse.size(); j++) {
			returnString += coursesList.getCourseList().get(j).getString() + "\n";
		}
		return returnString;
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
	private static String deleteCourse(CourseComponent coursesList, String message) {
		String courseId = message.trim();
		for(int i=0;i<coursesList.vCourse.size();i++) {
			Course course = coursesList.vCourse.get(i);
			if(course.match(courseId)){
				coursesList.vCourse.remove(i);
				return "Course with ID" + courseId + "is successfully deleted"; 
			}
		}
		return "Course with ID" + courseId + "does not exist";
	}
	
	private static String validateCourse(CourseComponent coursesList, String message) {
		String[] tokens = message.split(" ");
		String studentId= tokens[0];
		String courseId= tokens[1];
		
		Course course = null;
		for(Course c : coursesList.getCourseList()) {
			if(c.match(courseId)) {
				course = c;
				break;
			}
		}
		if(course==null) return "Validation failed: Course with ID " + courseId + " does not exist.";
		
		for(int i=1;i<tokens.length;i++) {
			for(int j=0;j<course.prerequisiteCoursesList.size();j++) {
				if(tokens[i].equals(course.prerequisiteCoursesList.get(j))||
						tokens[i].equals(courseId)){
					return "Registration failed."; 
				}
			}
		}
		return studentId+ " " + courseId;
	}
}
