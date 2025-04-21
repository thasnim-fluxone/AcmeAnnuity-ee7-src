package com.ibm.wssvt.acme.common.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.common.adapter.IServerAdapterEventListener;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.envconfig.ClientConfiguration;
import com.ibm.wssvt.acme.common.envconfig.IApplicationEnvConfig;
import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitEventListener;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitStack;
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IStackableExecutionUnit;
import com.ibm.wssvt.acme.common.log.AcmeLoggerConfig;
import com.ibm.wssvt.acme.common.log.AcmeLoggerFactory;
import com.ibm.wssvt.acme.common.stats.ExecutionStats;
import com.ibm.wssvt.acme.common.stats.ExecutionStatsFormatter;
import com.ibm.wssvt.acme.common.stats.ExecutionStatus;
import com.ibm.wssvt.acme.common.stats.IExecutionStatsFormatter;
import com.ibm.wssvt.acme.common.util.ClientContext;
import com.ibm.wssvt.acme.common.util.StringUtils;

/**
 * $Rev: 820 $
 * $Date: 2007-09-26 16:49:27 -0500 (Wed, 26 Sep 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class ExecutionUnitRunner extends Thread implements IExecutionUnitRunner, IReportedMessageVisitor {
	
	private static final String PRINT_STATS_INVTERVAL = "print.stats.interval";	
	private long threadLoopCounter;	
	private Date endTime = new Date(System.currentTimeMillis() + 1);
	private List<IExecutionUnit> executionUnits;	
	private List<IExecutionUnitStack> executionUnitStacks;
	private boolean suspendThread = false;
	private Logger logger;
	private ClientConfiguration clientConfiguration;
	private ClientContext clientContext;
	private List<IServerAdapterEventListener> serverAdapterEventListenerList;
	private List<IExecutionUnitEventListener> executionUnitEventListenerList;
	private IExecutionUnitRunnerMonitor executionUnitRunnerMonitor;
	private String reportedMessage;
	private IReportedMessageVisitor reportedMessageVisitor;
	private IExecutionStatsFormatter statsFormatter;
		
					
	public ExecutionUnitRunner(
			IApplicationEnvConfig applicationEnvConfig, 
			ClientContext ctx, 
			IExecutionUnitRunnerMonitor executionUnitRunnerMonitor,
			List<IExecutionUnitEventListener> executionUnitEventListenerList, 
			List<IServerAdapterEventListener> serverAdapterEventListenerList,
			IReportedMessageVisitor reportedMessageVisitor, 
			IExecutionStatsFormatter executionStatsFormatter) throws ConfigurationException{		
		
		this.executionUnits = applicationEnvConfig.getExecutionUnits();		
		this.executionUnitStacks = applicationEnvConfig.getExecutionUnitStacks();
		this.clientConfiguration = applicationEnvConfig.getClientConfiguration();
		Date now = new Date();
		this.endTime = new Date(now.getTime() + clientConfiguration.getRunTime());
		this.clientContext = ctx;

		this.executionUnitRunnerMonitor = executionUnitRunnerMonitor;		
		this.executionUnitRunnerMonitor.setRunner(this);

		//this.clientContext.setRootLoggerName("com.ibm.wssvt.acme.annuity");
		this.serverAdapterEventListenerList = serverAdapterEventListenerList;
		this.executionUnitEventListenerList = executionUnitEventListenerList;
		if (this.serverAdapterEventListenerList == null) { 
			this.serverAdapterEventListenerList = new ArrayList<IServerAdapterEventListener>();
		}
		if (this.executionUnitEventListenerList == null) {
			this.executionUnitEventListenerList = new ArrayList<IExecutionUnitEventListener>();
		}

		this.serverAdapterEventListenerList.add(this.executionUnitRunnerMonitor);
		this.executionUnitEventListenerList.add(this.executionUnitRunnerMonitor);
		
		this.reportedMessageVisitor = reportedMessageVisitor;		
		if (this.reportedMessageVisitor == null) {
			this.reportedMessageVisitor = this;
		}
		
		this.statsFormatter = executionStatsFormatter;
		if (statsFormatter == null) {
			statsFormatter = new ExecutionStatsFormatter(ExecutionStatsFormatter.FILE_NEW_LINE);
		}
		initializeLogger(clientContext);			
		
	}
		
	public int getThreadId(){
		return this.clientContext.getThreadId();
	}
	
	public long getThreadLoopCounter(){
		return this.threadLoopCounter;
	}
	
	public void run(){
		executeRun();
	}
	
	public void executeRun(){	
		ExecutionStats.getInstance().addThreadToStats(getThreadId());
		ExecutionStats.getInstance().setThreadStartTime(getThreadId(), new Date());
		int printStatsInterval =0; 
		try{
			printStatsInterval = StringUtils.toInt(clientConfiguration.getConfiguration().getParameterValue(PRINT_STATS_INVTERVAL));
		}catch (NumberFormatException e) {
			logger.logp(Level.WARNING, getClass().getName(), "executeRun",
				"The parameter: " + PRINT_STATS_INVTERVAL + " in the client config is not valid.  " +
				"The default is set to 25.  Current value is: " + 
				clientConfiguration.getConfiguration().getParameterValue(PRINT_STATS_INVTERVAL) );
			printStatsInterval = 25;
		}
		int printStatsLoopCounter =0;
		// suspend thread can be true if we had an error in the constructor
		Date now = new Date();		
		if (!(suspendThread)){		
			long initialActiveTime =-1;			
			long totalExecTime =0;
			do{		
				Date startLoopTime = new Date();
				long totalEUDelayTime =0;
				threadLoopCounter++;				
				ExecutionStats.getInstance().incrumentThreadLoopCount(getThreadId());			
				// execute the normal EUs.
				for (IExecutionUnit executionUnit: executionUnits) {
					if (threadLoopCounter > 1 && executionUnit instanceof ISingleRunExecutionUnit){
						continue;
					}			
					
					if ( executionUnit instanceof IStackableExecutionUnit){
						String msg ="Invalid Configuration.  Found an Execution Unit that is instance of IStackableExecutionUnit but NOT inside a ExecutionUnitStack Element.  " +
							".  Ensure that EVERY The Executin Unit that is instance of IStackableExecutionUnit is inside an ExecutionUnitStack ." +						
							".  EU Description: " + executionUnit.getDescription() +
							".  The Execution will stop.";
						System.out.println(msg);
						reportMessage(msg);
						suspendThread = true;
						break;
					}
					
					reportMessage("executeRun: Thread Id: " + getThreadId() + " in loop counter: " + threadLoopCounter 
							+ " will execute EU: " + executionUnit.getDescription() 
							+ " on adapter: " + executionUnit.getServerAdapter().getClass().getName());					
					totalEUDelayTime = runTheEU(totalEUDelayTime, executionUnit);										
					reportMessage("executeRun: Thread Id: " + getThreadId() + " in loop cntr: " + threadLoopCounter
							+ " completed Execution for EU: " + executionUnit.getDescription());					
					if (suspendThread){						
						break;
					}
					printStatsLoopCounter++;
					if (printStatsLoopCounter >= printStatsInterval){
						reportMessage("executeRun: *** PRINT STATS THREASHOLD MET: PRINTING STATS ****");						
						printStats();
						printStatsLoopCounter =0;
					}
				}
				
				// run the Stacks
				reportMessage("Ready to run ExecutionUnitStacks.");
				for(IExecutionUnitStack stack: executionUnitStacks){
					reportMessage("ready to execute stack: " + stack.getDescription());
					Map<String, Object> stackMap = new HashMap<String, Object>();
					for (IExecutionUnit stackEU : stack.getStackExecutionUnits()){						
						IStackableExecutionUnit eu = (IStackableExecutionUnit) stackEU;
						eu.setStackMap(stackMap);
						reportMessage("executeRun: Thread Id: " + getThreadId() + " in loop counter: " + threadLoopCounter 
								+ " will execute Stack EU: " + eu.getDescription() 
								+ " on adapter: " + eu.getServerAdapter().getClass().getName());					
						totalEUDelayTime = runTheEU(totalEUDelayTime, eu);										
						reportMessage("executeRun: Thread Id: " + getThreadId() + " in loop cntr: " + threadLoopCounter
								+ " completed Execution for Stack EU: " + eu.getDescription());
						if (suspendThread){						
							break;
						}
					}
					stackMap.clear();
					
					if (suspendThread){						
						break;
					}
				}
				
				logger.logp(Level.FINER, getClass().getName(), "executeRun",
						"Thread Id " + getThreadId() + " in loop #: " + threadLoopCounter);
				now = new Date();
				logger.logp(Level.FINE, getClass().getName(), "executeRun",
						"endTime: " + endTime +  " is after now? " + now + " " + endTime.after(now));			
				Date endLoopTime = new Date();
				long clockTime = endLoopTime.getTime() - startLoopTime.getTime() ;
				long activeTime = clockTime -  totalEUDelayTime;
				if (initialActiveTime == -1){
					initialActiveTime = activeTime;
				}
				totalExecTime += activeTime;
				long averageActiveTime = (totalExecTime) / threadLoopCounter;
				reportMessage("Thread id: " + getThreadId() + " loop number: " + threadLoopCounter + 
						" started at: " + startLoopTime + " and ended at:" +  endLoopTime +
						" which is: " + clockTime + " milliseconds (wall clock time)" +
						".  Execution Unit Delay total was: " + totalEUDelayTime + 
						".  Hence Execution time was: " + activeTime + " milliseconds (active time)" +
						".  First Loop Active Time is: " + initialActiveTime +
						".  Average Active Time for all loops in this thread: " + averageActiveTime);
				
				ExecutionStats.getInstance().incrumentThreadClockTime(getThreadId(), clockTime);
				ExecutionStats.getInstance().incrumentThreadCPUTime(getThreadId(), activeTime);
			}while (endTime.after(now) && !suspendThread);
		} // end if
										
		if (suspendThread){			
			ExecutionStats.getInstance().setThreadExecutionStatus(getThreadId(), ExecutionStatus.FAILED);
			ExecutionStats.getInstance().setThreadEndTime(getThreadId(), new Date());
			printStats();
			String msg = "Thread id: " + getThreadId() + " STOPPED and did NOT complete normally. Test has FAILED.";
			reportMessage(msg);
		}else{
			ExecutionStats.getInstance().setThreadExecutionStatus(getThreadId(), ExecutionStatus.PASSED);
			ExecutionStats.getInstance().setThreadEndTime(getThreadId(), new Date());
			printStats();
			reportMessage("Thread id: " + getThreadId() + " COMPLETED normally. This thread has PASSED.");
		}		
	}

	private long runTheEU(long totalEUDelayTime, IExecutionUnit executionUnit) {
		// set the scenario for execution
		executionUnit.setClientContext(clientContext);
		
		for(IExecutionUnitEventListener exEventListener : this.executionUnitEventListenerList) {
			executionUnit.addEventListener(exEventListener);
		}
		for (IServerAdapterEventListener serverAdapterEventListener : this.serverAdapterEventListenerList) {
			executionUnit.getServerAdapter().addEventListener(serverAdapterEventListener);					
		}
		executionUnit.getServerAdapter().setClientContext(clientContext);
		
		totalEUDelayTime += processExecutionUnitStartDelay(executionUnit);
		executionUnit.execute();	

		// now process events
		executionUnit.fireToListeners();
		executionUnit.getServerAdapter().fireToListeners();
		
		// clean up
		for(IExecutionUnitEventListener exEventListener : this.executionUnitEventListenerList) {
			executionUnit.removeEventListener(exEventListener);
		}
		for (IServerAdapterEventListener serverAdapterEventListener : this.serverAdapterEventListenerList) {
			executionUnit.getServerAdapter().removeEventListener(serverAdapterEventListener);					
		}
		return totalEUDelayTime;
	}

	private long processExecutionUnitStartDelay(IExecutionUnit executionUnit) {
		String executionUnitDelayType = executionUnit.getConfiguration().getParameterValue("executionUnitDelayType");
		String executionUnitDelayValueStr = executionUnit.getConfiguration().getParameterValue("executionUnitDelayValue");
		long  executionUnitDelayValue =0;
		if ("random".equalsIgnoreCase(executionUnitDelayType) || "constant".equalsIgnoreCase(executionUnitDelayType)){
			try {
				executionUnitDelayValue = Long.parseLong(executionUnitDelayValueStr);
				if (executionUnitDelayValue <=0) {
					throw new InvalidConfigurationException("invalid value for executionUnitDelayValue: " + executionUnitDelayValue);
				}
				if ("constant".equalsIgnoreCase(executionUnitDelayType)){
					Thread.sleep(executionUnitDelayValue);
					return executionUnitDelayValue;
				}
				if ("random".equalsIgnoreCase(executionUnitDelayType)){
					long randValue = (int)Math.round( Math.random() * executionUnitDelayValue);
					Thread.sleep(randValue);
					return randValue;
				}
			}catch (Throwable t) {
				reportMessage("Invalid property value.  The property: executionUnitDelayValue value is: " + executionUnitDelayValueStr
						+ " this value is invalid.  the value must be a valid long value greater than 0. Execution Unit Delay will be ignored.");
				return 0;
			}
		}else{
			// invalid execution delat type, if its not null, or not empty, issue a warning so that the user can correct it
			if (executionUnitDelayType != null){
				reportMessage("Invalid property value.  The property: executionUnitDelayType value is: " + executionUnitDelayType
						+ " this value is invalid.  Valid values are random, constant.  Execution Unit Delay will be ignored.");
			}
			return 0;
		}
		return 0;
		
	}

	private synchronized void initializeLogger(ClientContext clientContext) {	
		AcmeLoggerConfig.setClientFileNamePattern("", clientContext.getClientId(), clientContext.getThreadId(),
				(Configrable<String, String>)clientConfiguration);
		
		Logger initialLogger = Logger.getLogger(clientContext.getPrefixedRootLoggerName());				
		logger = AcmeLoggerFactory.getClientLogger(initialLogger, (Configrable<String, String>)clientConfiguration, 
				clientContext.getLoggerPrefix() + getClass().getName());		
		clientContext.setRootLogger(initialLogger);
		reportMessage("initializeLogger Client Logging initialized.  Logger name: " + logger.getName());
		
	}

	
	
	private void printStats() {
		String formattedStats = statsFormatter.format();	
		reportMessage(formattedStats);		
	}

	public int getRunnerId() {
		return getThreadId();
	}	
	public void reportMessage(String msg) {
		this.reportedMessage = msg;
		reportedMessageVisitor.visit(this);		
	}

	public String getReportedMessage() {
		return this.reportedMessage;		
	}
	public void stopRunner(String msg) {		
		reportMessage("stopRunner: " + msg);
		logger.logp(Level.SEVERE, getClass().getName(), "stopRunner",
				"*** THREAD EXECUTION SHUTDOWN.  THREAD ID: " + getThreadId());
		this.suspendThread = true;	
	}

	public ClientContext getClientContext() {
		return this.clientContext;
	}

	public void setClientContext(ClientContext clientContext) {
		this.clientContext = clientContext;		
	}

	public void visit(IExecutionUnitRunner runner) {
		logger.logp(Level.INFO, getClass().getName(), "visit",
				runner.getReportedMessage());		
	}



}
