package com.ibm.wssvt.acme.common.log;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.StringConfigrable;
import com.ibm.wssvt.acme.common.util.StringUtils;
/**
 * $Rev: 738 $
 * $Date: 2007-09-04 11:22:48 -0500 (Tue, 04 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class AcmeLoggerFactory {

	private static final String CLIENT = "client";
	private static final String SERVER = "server";
	private static Map<String, AcmeLogFileHandler> handlersCache = new HashMap<String, AcmeLogFileHandler>();
	private static final String defaultFilePattern = "AcmeLog%g.log";
	private static Logger thisLogger = Logger.getLogger(AcmeLoggerFactory.class.getName());
	
	public synchronized static AcmeLogger getAcmeServerLogger(Configrable<String, String> configrable, String name) {
		return new AcmeLogger(getLogger(SERVER, null, configrable, name));		
	}
	
	public synchronized static AcmeLogger getAcmeClientLogger(Logger root,
			Configrable<String, String> configrable, String name) {
		return new AcmeLogger(getLogger(CLIENT, root, configrable, name));		
	}
	
	public synchronized static Logger getServerLogger(Configrable<String, String> configrable, String name) {
		return getLogger(SERVER, null, configrable, name);		
	}
	
	public synchronized static Logger getClientLogger(Logger root,
			Configrable<String, String> configrable, String name) {
		return getLogger(CLIENT, root, configrable, name);		
	}

	private synchronized static Logger getLogger(String prefix, Logger root,
			Configrable<String, String> configrable, String name) {	
		
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getLogger", "requestto get logger with prefix: " + prefix
				+ " with root set to (might be null): " + root + " with name: " + name);
		Logger logger = Logger.getLogger(name);		
				
		if (configrable == null) {
			configrable = new StringConfigrable();
		}
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getLogger", "root logger is:" + root);
		if (root == null) {			
			root = Logger.getLogger("com.ibm.wssvt.acme");
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getLogger", "root logger is set to Acme: com.ibm.wssvt.acme");
		}				
		
		// the method setParent will actualy create a new entry in Parent.childs array list - over long time,
		// this might cause memory leak.  hence, set the parent only when the parent name is not equal to the root.getName()
		if (logger.getParent() == null) {
			logger.setParent(root);
		}else if (!(logger.getParent().getName().equals(root.getName()))){
			logger.setParent(root);
		}			
		
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getLogger", "the logger parent is now set to root." +
				"logger name is: " + logger.getName()
				+ " parent name is:" + logger.getParent().getName());
		
		try {				
			configHandler(prefix, configrable, root);
		} catch (Throwable t) {			
			
			thisLogger.log(Level.SEVERE, AcmeLoggerFactory.class.getName() +
					" Cought a throwable while trying to get a Annuity Logger named: " + name
					+ " defaulting to this logger:" + thisLogger.getName()
					+ "... see error message for more details."
					, t);
			logger = thisLogger;
		}
		logger.setLevel(root.getLevel());			
		return logger;
	}
	

	private static synchronized void configHandler(String prefix, Configrable<String, String> configrable, Logger logger) throws IOException {		
		String fileName = getFileName(prefix, configrable);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "configHandler", "configrable file name is: " + fileName);
		AcmeLogFileHandler fileHandler = handlersCache.get(fileName);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "configHandler", "handler from cache: " + fileHandler);

		// if not in cache - then its either new or replacement
		if (fileHandler == null) {
			Handler[] handlers = logger.getHandlers();
			for (Handler handler: handlers) {
				if (handler instanceof AcmeLogFileHandler){
					((AcmeLogFileHandler)handler).flush();
					((AcmeLogFileHandler)handler).close();
					logger.removeHandler(handler);
					handlersCache.remove(((AcmeLogFileHandler)handler).getFileNamePattern());
				}
			}
			fileHandler = getFileHandler(prefix, configrable);
			logger.addHandler(fileHandler);
			setLoggersLevel(prefix, configrable);
			setLoggerUseParentFlag(prefix, configrable, logger);
			fileHandler.flush();
		} else{
			// already in cache, update only if refresh request
			if (isRefreshRequest(prefix, configrable)) {
				thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "configHandler", "refresh request ...");
				setLoggersLevel(prefix, configrable);
				setLoggerUseParentFlag(prefix, configrable, logger);
			}
		}
				
	}
	

	private static synchronized boolean isRefreshRequest(String prefix, Configrable<String, String> configrable) {		
		String refresh = AcmeLoggerConfig.getRefreshMode(prefix, configrable);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "isRefreshRequest", "refresh: " + refresh);
		return Boolean.parseBoolean(refresh); 		
	}

	
	private static synchronized void  setLoggerUseParentFlag(String prefix, Configrable<String, String> configrable, Logger logger) {			
		String useParentStr = AcmeLoggerConfig.getUseParentFlag(prefix, configrable);		
		boolean useParent = Boolean.parseBoolean(useParentStr);	
		logger.setUseParentHandlers(useParent);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "setLoggerUseParentFlag", "use parent called : " + useParent);
	}

	/*
	 * parses the patterns
	 *  name=log.level value=com.ibm.xyz=foo;com.ibm.blah=bar
	 *  name=log.currentLoggerLevel=Foo or name=log.level value=com.ibm.wssvt.*
	 *  
	 */
	private static synchronized void setLoggersLevel(String prefix, Configrable<String, String> configrable) {
		String levelStr = AcmeLoggerConfig.getLevelAttribute(prefix, configrable);
		if (levelStr == null) {
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(levelStr, ";");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.indexOf("=")>0) {
				String[] splits = token.split("=");
				if (splits.length <2) {
					thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), "setLoggersLevel",
							"Invalid" + prefix + ".log.level param, does not contain = sign correctly: " + token
							+ ".  No modification changes for this section.");
					continue;
				}
				String loggerName = splits[0];
				String levelName = splits[1];
				thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "setLoggersLevel",
						"logger name: " + loggerName + " is to be set to level: " + levelName);
				if (loggerName.endsWith("*")) {					
					try{
						String loggerFixedName  = loggerName.substring(0,loggerName.length()-2);
						processLevelForAllLoggers(loggerFixedName, levelName);
					}catch (Throwable t){
						thisLogger.logp(Level.WARNING, AcmeLoggerFactory.class.getName(), "setLoggersLevel",
								"invalid configuration for .log.level.  " +
								"The value must be at least 3 char. No changes are made to the logging level.");
					}					
				}else{ 
					Logger logger = Logger.getLogger(loggerName);				
					try {
						if (levelName != null && levelName.trim().length() > 0) {
							logger.setLevel(Level.parse(levelName));																					
						}					
					} catch (Throwable t) {
						thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), "setLoggersLevel",
								"invalid level - using default: " + logger.getLevel());					
					}				
				}
			}
		}
			
	}

	
	private static synchronized void processLevelForAllLoggers(String loggerName, String levelName) {
		Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();				
		while (loggerNames.hasMoreElements()) {
			String name = loggerNames.nextElement();
			if (name.contains(loggerName)) {					
				Logger logger = Logger.getLogger(name);				
				try {
					if (levelName != null && levelName.trim().length() > 0) {
						thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "processLevelForAllLoggers",
								"logger name: " + logger.getName() + " is to be set to level: " + levelName);
						logger.setLevel(Level.parse(levelName));
					}					
				} catch (Throwable t) {					
					thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), 
							"processLevelForAllLoggers","invalid level - using level (if null means using parent level): " + logger.getLevel());					
				}
			}
		}
		
	}

	private synchronized static String getFileName(String prefix, Configrable<String, String> configrable) {
		String fileName = AcmeLoggerConfig.getFileNamePattern(prefix, configrable);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileName",
				"file name from configrable is: " + fileName);
		thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileName",
				"configrable is: " + configrable.getClass().getName());
		if (fileName == null || fileName.trim().length() < 1) {
			fileName = defaultFilePattern;
		}
		return fileName;
	}
	
	private synchronized static AcmeLogFileHandler getFileHandler(String prefix,
			Configrable<String, String> configrable) throws SecurityException,
			IOException {		
		String fileName = getFileName(prefix, configrable);
		AcmeLogFileHandler handler;
		String maxBytes = AcmeLoggerConfig.getMaxBytesAttribute(prefix, configrable);
		String maxCount = AcmeLoggerConfig.getMaxCountAttribute(prefix, configrable);
		String formatterStr = AcmeLoggerConfig.getFormatAttribute(prefix, configrable);
		String appendStr = AcmeLoggerConfig.getFileAppendFlagAttribute(prefix, configrable);
		boolean appendFlag = false;
		if ("true".equalsIgnoreCase(appendStr)) {
			appendFlag = true;
		}
		int maxBytesInt;
		try {
			maxBytesInt = StringUtils.toInt(maxBytes);
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler",
					"maxBytes: " + maxBytesInt);
		} catch (NumberFormatException e) {
			maxBytesInt = 5000000;
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler", "inalid maxbytes, use default max bytes: " + maxBytesInt);
		}

		int maxCountInt;
		try {
			maxCountInt = StringUtils.toInt(maxCount);
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler", "maxCount: " + maxCountInt);
		} catch (NumberFormatException e) {
			maxCountInt = 1000;
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler", "invalid maxCount, use default max count: " + maxCountInt);
		}
		Formatter formatter;
		try {
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler", "formatter class is: " + formatterStr);
			thisLogger.logp(Level.FINEST, AcmeLoggerFactory.class.getName(), "getFileHandler", "file name is: " + fileName);
			formatter = (Formatter) Class.forName(formatterStr).newInstance();
		} catch (Throwable t) {
			thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), "getFileHandler", "invalid formatter class : " + formatterStr);
			if (fileName.endsWith(".xml")) {
				thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), "getFileHandler", "file name ends with xml, using xml formatter");
				formatter = new XMLFormatter();
			} else {
				thisLogger.logp(Level.INFO, AcmeLoggerFactory.class.getName(), "getFileHandler", "file name does not end with xml, using simple formatter");
				formatter = new SimpleFormatter();
			}
		}
		handler = new AcmeLogFileHandler(fileName, maxBytesInt, maxCountInt,
				appendFlag); 		
		handler.setFormatter(formatter);		
		handlersCache.put(fileName, handler);
		return handler;
	}

	
}
