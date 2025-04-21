package com.ibm.wssvt.acme.common.log;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AcmeLogger {
	private Logger logger;	
	private List<String> records = new ArrayList<String>();	

	public AcmeLogger(Logger l) {			
		this.logger = l;
	}
		
	public Logger getLogger(){
		return this.logger;
	}
	
	public String getAllLogs() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nSTART of ALL Logs:\n");		
		for (String  record : records) {			
			sb.append(record);
			sb.append("\n");
		}
		sb.append("END of ALL Logs\n");
		records.clear();
		return sb.toString();
	}
	
	public void clearAllLogs(){
		records.clear();
	}
	
	public void config(String msg) {	
		logger.logp(Level.CONFIG, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.CONFIG + " " + msg);		
	}

	
	public void entering(String sourceClass, String sourceMethod, Object param1) {		
		logger.entering(sourceClass, sourceMethod, param1);
		records.add("entering: " + sourceClass  + " " + sourceMethod + " " +  param1);
	}

	
	public void entering(String sourceClass, String sourceMethod, Object[] params) {
		logger.entering(sourceClass, sourceMethod, params);
		records.add("entering: " + sourceClass + " " + sourceMethod + " " + params);
	}

	
	public void entering(String sourceClass, String sourceMethod) {		
		logger.entering(sourceClass, sourceMethod);
		records.add("entering: " + sourceClass + " " + sourceMethod );
	}

	
	public void exiting(String sourceClass, String sourceMethod, Object result) {		
		logger.exiting(sourceClass, sourceMethod, result);
		records.add("exiting: " + sourceClass + " " + sourceMethod  + " " + result);
	}

	
	public void exiting(String sourceClass, String sourceMethod) {		
		logger.exiting(sourceClass, sourceMethod);
		records.add("exiting: " + sourceClass + " " + sourceMethod);
	}

	
	public void fine(String msg) {		
		logger.logp(Level.FINE, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.FINE + " " + msg);
	}

	
	public void finer(String msg) {		
		logger.logp(Level.FINER, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.FINER + " " + msg);
	}

	
	public void finest(String msg) {
		logger.logp(Level.FINEST, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.FINEST + " " + msg);
	}

	
	public void info(String msg) {
		logger.logp(Level.INFO, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.INFO + " " + msg);
	}
	
	
	public void log(Level level, String msg, Object param1) {				
		logger.logp(level, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg, param1);
		records.add(level + " " + msg + " " + param1);
	}

	
	public void log(Level level, String msg, Object[] params) {
		logger.logp(level, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg, params);
		records.add(level + " " +msg + " " + params);
	}

	
	public void log(Level level, String msg, Throwable thrown) {		
		logger.logp(level, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg, thrown);
		records.add(level + " " +msg + " " + thrown);
	}

	
	public void log(Level level, String msg) {
		logger.logp(level, getStackTraceElementForLoggingClass().getClassName(), 
				getStackTraceElementForLoggingClass().getMethodName(), msg);		
		records.add(level + " " +msg);
	}

	
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {		
		logger.logp(level, sourceClass, sourceMethod, msg, param1);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + msg + " " + param1 );
	}

	
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {		
		logger.logp(level, sourceClass, sourceMethod, msg, params);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + msg + " " + params );
	}

	
	public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {		
		logger.logp(level, sourceClass, sourceMethod, msg, thrown);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + msg + " " + thrown );
	}

	
	public void logp(Level level, String sourceClass, String sourceMethod, String msg) {		
		logger.logp(level, sourceClass, sourceMethod, msg);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + msg );
	}

	
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {		
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + bundleName + " " + msg + " " + param1 );
	}

	
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {	
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + bundleName + " " + msg + " " + params );
	}

	
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + bundleName + " " + msg + " " + thrown);
	}

	
	public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
		logger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
		records.add(level + " " + sourceClass + " " + sourceMethod + " " + bundleName + " " + msg );
	}
	
		
	
	public void severe(String msg) {			
		logger.logp(Level.SEVERE, getStackTraceElementForLoggingClass().getClassName(), getStackTraceElementForLoggingClass().getMethodName(), msg);
		records.add(Level.SEVERE + " " + msg);		
	}
	
	
	public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {		
		logger.throwing(sourceClass, sourceMethod, thrown);
		records.add("throwing: " + sourceClass + " " + sourceMethod + " " + thrown);
	}

	
	public void warning(String msg) {
		logger.logp(Level.WARNING, getStackTraceElementForLoggingClass().getClassName(), getStackTraceElementForLoggingClass().getMethodName(), msg);		
		records.add(Level.WARNING + " " +  msg);
	}
		 
	
	
	private StackTraceElement getStackTraceElementForLoggingClass() {		
         StackTraceElement[] elements = (new Throwable()).getStackTrace();
         if (elements.length >= 2){
         	return elements[2];
         }else{
         	return new StackTraceElement(getClass().getName(),"Unknown Method","", 1);
         }	          	   
	 }	
	
}
