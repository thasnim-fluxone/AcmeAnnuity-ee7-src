package com.ibm.wssvt.acme.annuity.common.util;

//import com.ibm.websphere.management.AdminService;
//import com.ibm.websphere.management.AdminServiceFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;

public class RunningServerInfo {
	private String cellName;
	private String nodeName;
	private String processName;
	private static RunningServerInfo instance = null;
	
	private RunningServerInfo (){
//		AdminService admin = AdminServiceFactory.getAdminService();
//		cellName = admin.getCellName();
//		nodeName = admin.getNodeName();
//		processName = admin.getProcessName();
	}
	
	public synchronized static RunningServerInfo getInstance() {
		if (instance == null) {
			instance = new RunningServerInfo();
		}
		return instance;
	}
	
	// this will only get the server on which we are currently running
	public String getRunningServerInfo(){
		StringBuffer name = new StringBuffer();
		try{	                                    
			name.append(cellName);
			name.append("/");
			name.append(nodeName);
			name.append("/");
			name.append(processName);
		}catch(Exception e){
			name.append(" Unable to get serverName");
		}		
		return name.toString();
	}
	
	// this will set all the servers we have run on for the current EU - not just the current server
	public void setRunningServerInfo(Configrable<String, String> configrable){
		if (configrable == null){
			return;
		}
		String runningOnServers = configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo");
		if (runningOnServers == null) {
			configrable.getConfiguration().addParameter("internal.runningOnServerInfo", getRunningServerInfo());
		} else {
			configrable.getConfiguration().addParameter("internal.runningOnServerInfo", runningOnServers + ";;" + getRunningServerInfo());
		}
	}

	public String getCellName() {
		if (null == cellName ) {
//			AdminService admin = AdminServiceFactory.getAdminService();
//			cellName = admin.getCellName();
		}
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}
}
