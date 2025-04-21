package com.ibm.wssvt.acme.common.stats;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * $Rev: 820 $
 * $Date: 2007-09-26 16:49:27 -0500 (Wed, 26 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ExecutionStats {
	
	private static ExecutionStats instance = null;	
	private Map<String, ExceptionInfoStats> exceptionInfoStatsMap = Collections.synchronizedMap(
			new HashMap<String, ExceptionInfoStats>());	
	private Map<Integer, ThreadInfoStats> threadInfoStatsMap = Collections.synchronizedMap(
			new HashMap<Integer, ThreadInfoStats>());
	private Map<String, Long> serverRunInfo = Collections.synchronizedMap(
			new HashMap<String, Long>());
	private Map<String, Long> overallServerRunInfo = Collections.synchronizedMap(
			new HashMap<String, Long>());
	
	private long request;
	private long create;
	private long read;
	private long update;
	private long delete;
	private long find;

	private ExecutionStats () {
		
	}
	
	public static ExecutionStats getInstance() {
		if (instance == null) {
			instance = new ExecutionStats();
		}
		return instance;
	}
	
	public synchronized void reset() {
		exceptionInfoStatsMap.clear();	
		threadInfoStatsMap .clear();
		serverRunInfo.clear();
		overallServerRunInfo.clear();
		create = 0;
		read = 0;
		update = 0;
		delete = 0;
		find = 0;
		request = 0;
	}
	public synchronized long getCRUDTotal(){
		return create + read + update + delete;
	}
	public synchronized long incrumentCreate(long a){	
		return create += a;		
	}
	public synchronized long incrumentRead(long a){		
		return read  += a;
	}
	public synchronized long incrumentUpdate(long a){
		return update  += a;
	}
	public synchronized long incrumentDelete(long a){
		return delete  += a;
	}
	public synchronized long incrumentFind(long a){
		return find  += a;
	}
	
	public synchronized long getCreate() {
		return create;
	}

	public synchronized long getDelete() {
		return delete;
	}

	public synchronized long getFind() {
		return find;
	}

	public synchronized long getRead() {
		return read;
	}

	public synchronized long getUpdate() {
		return update;
	}
	
	public synchronized long getRequest() {
		return request;
	}

	public synchronized long incrumentRequest(long a) {	
		return this.request += a;		

	}
	
	public synchronized ExceptionInfoStats getExceptionInfoStats(Exception exception){
		return this.exceptionInfoStatsMap.get(exception.getClass().getName());
	}
	
	public synchronized long incrumentExceptionStatsCount(Exception exception, int threadId, Date date, String euDesc, int maxMsgSize, int exceptionMaxCount) {		
		ExceptionInfoStats exInfoStats = exceptionInfoStatsMap.get(exception.getClass().getName());
		if (exInfoStats == null) { 
			exInfoStats = new ExceptionInfoStats();
			exInfoStats.setCount(0);
			exInfoStats.setName(exception.getClass().getName());			
			exceptionInfoStatsMap.put(exception.getClass().getName(), exInfoStats);
		}		
		exInfoStats.setCount(exInfoStats.getCount() +1);
		exInfoStats.addExceptionData(exInfoStats.getCount(),threadId, date, euDesc, exception.getMessage(), maxMsgSize, exceptionMaxCount);		
		return exInfoStats.getCount();
	}
	
	public synchronized void setThreadStartTime(int threadId, Date value) {
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		if (threadStats != null){
		threadStats.setThreadStartTime(value);
		}
	}
	public synchronized void setThreadEndTime(int threadId, Date value) {		
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		if (threadStats != null){
		threadStats.setThreadEndTime(value);
		}
	}	
	public synchronized long incrumentThreadClockTime(int threadId, long value) {
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		//System.out.println("Rumana added - threadStats: "+ threadStats +" threadId: "+ threadId +" value: "+ value +" threadInfoStatsMap: "+ threadInfoStatsMap   );
		if (threadStats != null){
			threadStats.setExecutionClockTime(threadStats.getExecutionClockTime() + value);		
			return threadStats.getExecutionClockTime();
		}
		else
			return value;
	}
	public synchronized long incrumentThreadCPUTime(int threadId, long value) {
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		if (threadStats != null){
			threadStats.setExecutionActiveTime(threadStats.getExecutionActiveTime() + value);		
			return threadStats.getExecutionActiveTime();
		}
		else
			return value;
	}
	
	public synchronized long incrumentThreadLoopCount(int threadId) {
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		if (threadStats != null){
			threadStats.setLoopCount(threadStats.getLoopCount() + 1);		
			return threadStats.getLoopCount();
		}
		else
			return 1;
	}

	public synchronized ExecutionStatus getThreadExecutionStatus(int threadId){
		ThreadInfoStats value = threadInfoStatsMap.get(threadId);		
		return value.getExecutionStatus();
	}
	
	public synchronized void setThreadExecutionStatus(int threadId, ExecutionStatus status){
		ThreadInfoStats threadStats = threadInfoStatsMap.get(threadId);
		if (threadStats != null){
			threadStats.setExecutionStatus(status);	
		}
	}
		
	public synchronized Map<String, ExceptionInfoStats> getExceptionCount() {
		return exceptionInfoStatsMap;
	}

	public synchronized Map<Integer, ThreadInfoStats> getThreadInfoStatsMap() {
		return threadInfoStatsMap;
	}
	
	public synchronized Map<String, Long> getServerRunInfo() {
		return this.serverRunInfo;
	}
	
	public synchronized long incrumentServerRunInfo(String serverName, long value) {
		Long currentValue = serverRunInfo.get(serverName);			
		if (currentValue == null) { 
			currentValue = 0L;
		}		
		serverRunInfo.put(serverName, currentValue + value);
		return value;
	}
	
	public synchronized Map<String, Long> getOverallServerRunInfo() {
		return this.overallServerRunInfo;
	}
	
	public synchronized long incrumentOverallServerRunInfo(String serverName, long value) {
		Long currentValue = overallServerRunInfo.get(serverName);			
		if (currentValue == null) { 
			currentValue = 0L;
		}		
		overallServerRunInfo.put(serverName, currentValue + value);
		return value;
	}

	public synchronized void addThreadToStats(int threadId) {
		ThreadInfoStats value = threadInfoStatsMap.get(threadId);		
		if (value == null) { 
			value = ThreadInfoStats.newInstance(threadId, ExecutionStatus.RUNNING);			
		}
		threadInfoStatsMap.put(threadId, value);
		//System.out.println("In addThreadToStats - threadId: " + threadId +" value: " +value);
	}

	
	
}
