package com.ibm.wssvt.acme.common.client;

import java.util.Date;

import com.ibm.wssvt.acme.common.adapter.IServerAdapterEvent;
import com.ibm.wssvt.acme.common.envconfig.IApplicationEnvConfig;
import com.ibm.wssvt.acme.common.exception.ExceptionAction;
import com.ibm.wssvt.acme.common.exception.ExecutionUnitExceptionProcessor;
import com.ibm.wssvt.acme.common.exception.IExceptionHandlerResult;
import com.ibm.wssvt.acme.common.exception.IExecutionUnitExceptionProcessor;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitEvent;
import com.ibm.wssvt.acme.common.stats.ExecutionStats;

public class ExecutionUnitRunnerMonitor implements  IExecutionUnitRunnerMonitor {
	private static final long serialVersionUID = 6112098727998142387L;
	private IExecutionUnitRunner runner;
	private IApplicationEnvConfig applicationEnvConfig;
	public ExecutionUnitRunnerMonitor(IApplicationEnvConfig applicationEnvConfig) {
		this.applicationEnvConfig = applicationEnvConfig;
	}
	public void processExecutionUnitEvent(IExecutionUnitEvent executionUnitEvent) {
		IExecutionUnitExceptionProcessor executionUnitExceptionProcessor = new ExecutionUnitExceptionProcessor();		
		for (Exception exception: executionUnitEvent.getExceptions()){
			IExceptionHandlerResult result;	
			result = executionUnitExceptionProcessor.processException(
				this.applicationEnvConfig.getExceptionHandlers(), 
				exception, executionUnitEvent.getExecutionUnit());	
			if (ExceptionAction.REPORT.equals(result.getAction())) {
				runner.reportMessage(result.getMessage());					
			} else if (ExceptionAction.REPORT_AND_COUNT.equals(result.getAction())) {
				runner.reportMessage(result.getMessage());
				int maxMsgSize = 250;
				int exceptionMaxCount = -1;
				try{
					maxMsgSize = Integer.parseInt( 
						executionUnitEvent.getExecutionUnit().getConfiguration().getParameterValue("ErrorMessageMaxSize"));
				}catch (Throwable t){
					
				}
				try{
					exceptionMaxCount = Integer.parseInt( 
						executionUnitEvent.getExecutionUnit().getConfiguration().getParameterValue("exceptionMaxCount"));
				}catch (Throwable t){
					
				}
				ExecutionStats.getInstance().incrumentExceptionStatsCount(exception, 
						executionUnitEvent.getExecutionUnit().getClientContext().getThreadId(),
						new Date(), executionUnitEvent.getExecutionUnit().getDescription(), maxMsgSize, exceptionMaxCount);				
			} else if (ExceptionAction.STOP.equals(result.getAction())) {			
				int maxMsgSize = 250;
				int exceptionMaxCount = -1;
				try{
					maxMsgSize = Integer.parseInt( 
						executionUnitEvent.getExecutionUnit().getConfiguration().getParameterValue("ErrorMessageMaxSize"));
				}catch (Throwable t){
					
				}
				try{
					exceptionMaxCount = Integer.parseInt( 
						executionUnitEvent.getExecutionUnit().getConfiguration().getParameterValue("exceptionMaxCount"));
				}catch (Throwable t){
					
				}
				ExecutionStats.getInstance().incrumentExceptionStatsCount(exception, 
						executionUnitEvent.getExecutionUnit().getClientContext().getThreadId(), 
						new Date(), executionUnitEvent.getExecutionUnit().getDescription(), maxMsgSize, exceptionMaxCount);
				runner.stopRunner(result.getMessage());
				break;
			} else if (ExceptionAction.IGNORE.equals(result.getAction())) {			
				// as the name says it, ignore it!
			}			
		}
		
	}

	public void processServerAdapterEvent(IServerAdapterEvent serverAdapterEvent) {
		ExecutionStats.getInstance().incrumentRequest(serverAdapterEvent.getRequest());
		ExecutionStats.getInstance().incrumentCreate(serverAdapterEvent.getCreate());
		ExecutionStats.getInstance().incrumentRead(serverAdapterEvent.getRead());
		ExecutionStats.getInstance().incrumentUpdate(serverAdapterEvent.getUpdate());
		ExecutionStats.getInstance().incrumentDelete(serverAdapterEvent.getDelete());
		ExecutionStats.getInstance().incrumentFind(serverAdapterEvent.getFind());
		for (String serverName : serverAdapterEvent.getRunServerInfoCount().keySet()) {
			ExecutionStats.getInstance().incrumentServerRunInfo(
				serverName, serverAdapterEvent.getRunServerInfoCount().get(serverName));
		}
		for (String overallServerName : serverAdapterEvent.getOverallRunServerInfoCount().keySet()) {
			ExecutionStats.getInstance().incrumentOverallServerRunInfo(overallServerName,
					serverAdapterEvent.getOverallRunServerInfoCount().get(overallServerName));
		}
		
		
	}

	public IExecutionUnitRunner getRunner() {
		return runner;
	}

	public void setRunner(IExecutionUnitRunner runner) {
		this.runner = runner;
	}
	
}
