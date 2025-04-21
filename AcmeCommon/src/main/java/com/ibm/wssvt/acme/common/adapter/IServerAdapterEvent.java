package com.ibm.wssvt.acme.common.adapter;

import java.util.Map;

public interface IServerAdapterEvent {

	public void clearAll();

	public long incrumentRequest();

	public long incrumentCreate();

	public long incrumentRead();

	public long incrumentUpdate();

	public long incrumentDelete();

	public long incrumentFind();

	public IServerAdapter getServerAdapter();

	public void setServerAdapter(IServerAdapter adapter);

	public long getCreate();

	public long getDelete();

	public long getFind();

	public long getRead();

	public long getUpdate();
	
	public long getRequest();
	
	public Map<String, Long> getRunServerInfoCount();
	
	public long incrumentRunServerCount(String serverName);
	
	public Map<String, Long> getOverallRunServerInfoCount();
	
	public long incrumentOverallRunServerCount(String serverName);

}