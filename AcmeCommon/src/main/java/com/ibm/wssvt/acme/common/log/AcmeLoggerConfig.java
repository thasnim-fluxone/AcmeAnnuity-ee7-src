package com.ibm.wssvt.acme.common.log;

import com.ibm.wssvt.acme.common.bean.Configrable;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class AcmeLoggerConfig {	
	public static final String CLIENT = "client";
	public static final String SERVER = "server";

	public static String getLevelAttribute(String prefix, Configrable<String, String> configrable) {
		String levelStr = configrable.getConfiguration().getParameterValue(prefix+ ".log.level");
		if (levelStr == null || levelStr.trim().length() == 0) {
			levelStr = null;
		}		
		return levelStr;
	}
	
	public static String getUseParentFlag(String prefix, Configrable<String, String> configrable){
		String s  =configrable.getConfiguration().getParameterValue(prefix+ ".log.useParent");
		return getBooleanPropertyAsString(s);
	}

	
	public static String getRefreshMode(String prefix, Configrable<String, String> configrable){
		String s  =configrable.getConfiguration().getParameterValue(prefix+ ".log.refresh");
		return getBooleanPropertyAsString(s);
	} 

	private static String getBooleanPropertyAsString(String s) {
		if (s == null || s.trim().length()==0) {
			return "false";
		}
		if ("true".equalsIgnoreCase(s)){
			return "true";
		}
		return "false";
	}
	


	public static String getFormatAttribute(String prefix, Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue(prefix + ".log.format");
	}
	
	public static String getMaxCountAttribute(String prefix, Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue(prefix + ".log.maxCount");
	}
	
	public static String getMaxBytesAttribute(String prefix, Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue(prefix + ".log.maxBytes");
	}
	public static String getFileAppendFlagAttribute(String prefix, Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue(prefix + ".log.fileAppendFlag");
	}
	
	public static String getFileNamePattern(String prefix, Configrable<String, String> configrable){
		String fileName = configrable.getConfiguration().getParameterValue(prefix + ".log.fileNamePattern");
		if (fileName == null) return null;
		if (CLIENT.equalsIgnoreCase(prefix)) {
			return fileName;
		}
		if (SERVER.equalsIgnoreCase(prefix)) {
			String serverName = configrable.getConfiguration().getParameterValue("internal.serverName");
			if (serverName == null) {
				return fileName;
			} else{
				return getFullServerName(fileName, serverName);
			}			
		}
		return null;
	} 
	
	
	public static String getClientFilePath(Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue("client.log.filePath");				
	}
	
	public static String getClientFilePattern(Configrable<String, String> configrable){
		return configrable.getConfiguration().getParameterValue("client.log.filePattern");		
	}
	
	public static String buildClientFileNamePattern(String mode, String clientId, int threadId, Configrable<String, String> configrable) {
		String path = getClientFilePath(configrable);
		String fileNamePattern = getClientFilePattern(configrable);
		if (path == null || path.trim().equals("")){
			path = "";
		}
		
		if (fileNamePattern == null || fileNamePattern.trim().equals("")){
			fileNamePattern = "AcmeClientLog%g.txt";
		}
		if (mode != null && mode.trim().length() >0) {
			fileNamePattern = mode + "_" + fileNamePattern;
		}
		fileNamePattern = getClientLoggerPrefix(clientId, threadId) + fileNamePattern;
		fileNamePattern = path + fileNamePattern;	
		return fileNamePattern;
	}
	
	public static void setClientFileNamePattern(String mode, String clientId, int threadId, Configrable<String, String> configrable){
		configrable.getConfiguration().removeParameter("client.log.fileNamePattern");
		configrable.getConfiguration().addParameter(
				"client.log.fileNamePattern", buildClientFileNamePattern(mode, clientId, threadId, configrable));		
	}
	
	public static String getClientLoggerPrefix(String clientId, int threadId){
		StringBuffer sb = new StringBuffer("C_").append(clientId).append("_T_").append(threadId).append("_");
		return sb.toString();
	}
	
	public static String getFullServerName(String fileName, String serverName) {
		if (fileName == null){
			return null;
		}
		if (serverName == null){
			return fileName; // no changes
		}
		/*
		 * fileName is always like any of these
		 * c:\\foo\\bar\\name.log
		 * c:/foo/bar/name.log
		 *  /tmp/foo/bar/name.log
		 *  name.log		
		 */
		
		if (fileName.indexOf("\\") >0) {
			fileName = fileName.replace("\\", "/");			
		}		
		String updated;
		
		if (fileName.contains("/")){
			int lastIdx = fileName.lastIndexOf("/");			
			updated = fileName.substring(lastIdx+1, fileName.length()); // just the file name
			updated = serverName + "_" + updated;   // append the servername; example svtwin14_ServerLog.log
			updated = fileName.substring(0, lastIdx+1) + updated; //re build the full path.			
		}else{
			updated = serverName + "_" + fileName;
			
		}		
		return updated;
	}	
}
