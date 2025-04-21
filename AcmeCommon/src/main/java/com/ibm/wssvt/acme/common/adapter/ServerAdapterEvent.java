package com.ibm.wssvt.acme.common.adapter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ServerAdapterEvent implements Serializable, IServerAdapterEvent{

	private static final long serialVersionUID = 1978445384864558975L;
	private IServerAdapter adapter;
	private long request;
	private long create;
	private long read;
	private long update;
	private long delete;
	private long find;
	private Map<String, Long> serverRunInfo = new HashMap<String, Long>();
	private Map<String, Long> overallServerRunInfo = new HashMap<String, Long>();
		
	public synchronized void clearAll(){
		request =0;
		create =0;
		read=0;
		update =0;
		delete =0;		
		serverRunInfo.clear();
		overallServerRunInfo.clear();
	}
	
	public synchronized long incrumentRequest(){
		return ++request;
	}
	public synchronized long incrumentCreate(){
		return ++create;
	}
	public synchronized long incrumentRead(){
		return ++read;
	}
	public synchronized long incrumentUpdate(){
		return ++update;
	}
	public synchronized long incrumentDelete(){
		return ++delete;
	}
	public synchronized long incrumentFind(){
		return ++find;
	}
	
	public IServerAdapter getServerAdapter() {
		return adapter;
	}

	public void setServerAdapter(IServerAdapter adapter) {
		this.adapter = adapter;
	}

	public long getCreate() {
		return create;
	}

	public long getDelete() {
		return delete;
	}

	public long getFind() {
		return find;
	}

	public long getRead() {
		return read;
	}

	public long getUpdate() {
		return update;
	}
	public long getRequest() {
		return request;
	}

	public Map<String, Long> getRunServerInfoCount() {
		return this.serverRunInfo;
	}

	public long incrumentRunServerCount(String serverName) {
		if (serverName == null || "".equals(serverName.trim())) {
			return 0;
		}
		Long res = this.serverRunInfo.get(serverName);
		if (res == null) res = 0L;
		this.serverRunInfo.put(serverName, ++res);
		return res;
	}
	
	public Map<String, Long> getOverallRunServerInfoCount() {
		return this.overallServerRunInfo;
	}
	
	public long incrumentOverallRunServerCount(String serverName) {
		if (serverName == null || "".equals(serverName.trim())) {
			return 0;
		}
		Long res = this.overallServerRunInfo.get(serverName);
		if (res == null) res = 0L;
		this.overallServerRunInfo.put(serverName, ++res);
		return res;
	}
}
