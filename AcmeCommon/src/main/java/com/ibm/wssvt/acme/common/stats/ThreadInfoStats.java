package com.ibm.wssvt.acme.common.stats;

import java.io.Serializable;
import java.util.Date;

public class ThreadInfoStats implements Serializable{
		
	private static final long serialVersionUID = 463715022730423883L;
	private int id;
	private long loopCount =0;	
	private ExecutionStatus executionStatus;
	private Date lastStatusDate;
	private long executionClockTime =0;
	private long executionActiveTime =0;
	private Date threadStartTime;
	private Date threadEndTime;
	
	public Date getThreadStartTime() {
		return threadStartTime;
	}
	public void setThreadStartTime(Date threadStartTime) {
		this.threadStartTime = threadStartTime;
	}
	public Date getThreadEndTime() {
		return threadEndTime;
	}
	public void setThreadEndTime(Date threadEndTime) {
		this.threadEndTime = threadEndTime;
	}
	
	public long getExecutionClockTime() {
		return executionClockTime;
	}
	public void setExecutionClockTime(long executionClockTime) {
		this.executionClockTime = executionClockTime;
	}
	public long getExecutionActiveTime() {
		return executionActiveTime;
	}
	public void setExecutionActiveTime(long executionActiveTime) {
		this.executionActiveTime = executionActiveTime;
	}
	public Date getLastStatusDate() {
		return lastStatusDate;
	}
	public void setLastStatusDate(Date lastStatusDate) {
		this.lastStatusDate = lastStatusDate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getLoopCount() {
		return loopCount;
	}
	public void setLoopCount(long loopCount) {
		this.loopCount = loopCount;
	}	
	
	public ExecutionStatus getExecutionStatus() {
		if (this.executionStatus == null){
			return ExecutionStatus.UNKNOWN;
		}
		return executionStatus;
	}
	
	public void setExecutionStatus(ExecutionStatus executionStatus) {
		if (executionStatus == null){
			this.executionStatus = ExecutionStatus.UNKNOWN;
		}else{
			this.executionStatus = executionStatus;
		}
		setLastStatusDate(new Date());
	}
	
	public static ThreadInfoStats newInstance(int threadId, ExecutionStatus status){
		ThreadInfoStats value = new ThreadInfoStats();
		value = new ThreadInfoStats();
		value.setId(threadId);						
		value.setExecutionStatus(status);			
		value.setLoopCount(0);
		value.setLastStatusDate(new Date());
		return value;
				
	}
}
