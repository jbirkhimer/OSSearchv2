package edu.si.ossearch.scheduler.util;

public class ServerResponseCode {
	
	//SPECIFIC ERROR CODES
	public static final int JOB_WITH_SAME_NAME_EXIST = 400;
	public static final int JOB_NAME_NOT_PRESENT = 404;
	
	public static final int JOB_ALREADY_IN_RUNNING_STATE = 409;
	public static final int JOB_NOT_IN_PAUSED_STATE = 409;
	public static final int JOB_NOT_IN_RUNNING_STATE = 409;
	
	public static final int JOB_DOESNT_EXIST = 404;
	
	//GENERIC ERROR
	public static final int ERROR = 500;
	
	//SUCCESS CODES
	public static final int SUCCESS = 200;
	public static final int CREATED = 201;
}
