package pasa.alertantminer.utils;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

public class LoggerSession implements Serializable {
	
	private static final long serialVersionUID = 9126453589188041544L;
	
	public static final int TYPE_NORMAL 	= 100;
	public static final int TYPE_WARNING 	= 250;
	public static final int TYPE_ERROR 		= 300;
	
	private static Logger logger = Logger.getRootLogger();

	private String uniqueNumber;
	private String applicationName;
	protected long startMilliseconds;
	protected long checkPointMilliseconds;
	
	protected LoggerSession() {
		startMilliseconds = System.currentTimeMillis();
		checkPointMilliseconds = startMilliseconds;
	}
	
	public LoggerSession(String sessionId, String applicationName) {
		this();
		this.applicationName = applicationName;
		this.uniqueNumber = sessionId;
	}
	
	public String getSessionId() {
		return uniqueNumber;
	}
	
	public String getApplicationName() {
		return applicationName;
	}
	
	public double getSecondsSinceStart() {
		double seconds = 0;
		seconds = (System.currentTimeMillis() - startMilliseconds) / 1000d;

		return seconds;
	}
	
	public double getCheckPoint() {
		double seconds = 0;
		long currentMilliseconds = 0;
		
		currentMilliseconds = System.currentTimeMillis();
		seconds = ( currentMilliseconds - checkPointMilliseconds) / 1000d;
		checkPointMilliseconds = currentMilliseconds;
		return seconds;
	}

	public void info(Object text){
		logger.info("|" + getSessionId() + "|" + getApplicationName() + "|" + TYPE_NORMAL + "|" + text);
	}

	public void warning(Object text){
		logger.info("|" + getSessionId() + "|" + getApplicationName() + "|" + TYPE_WARNING + "|" + text);
	}

	public void error(Object text) {
		logger.error("|" + getSessionId() + "|" + getApplicationName() + "|" + TYPE_ERROR + "|" + text);
	}
	
	public void error(Throwable ex) {
		final String stackTraceText;
		stackTraceText = Utils.getStackTrace(ex);
		logger.error("|" + getSessionId() + "|" + getApplicationName() + "|" + TYPE_ERROR + "|" + stackTraceText);
	}
	
	public static void main(String[] arg) {
		LoggerSession loggerSession = new LoggerSession("TEST" + System.currentTimeMillis(), "TEST");
		loggerSession.info("hola");
	}
	
	public static File getFileName() {
		FileAppender fileAppender = (FileAppender) logger.getAppender("R");;
		File file = new File(fileAppender.getFile());
		return file;
	}

}
