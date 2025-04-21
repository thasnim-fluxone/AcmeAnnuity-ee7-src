package com.ibm.wssvt.acme.common.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.common.adapter.IServerAdapterEventListener;
import com.ibm.wssvt.acme.common.envconfig.ClientConfiguration;
import com.ibm.wssvt.acme.common.envconfig.IApplicationEnvConfig;
import com.ibm.wssvt.acme.common.envconfig.exception.ConfigurationException;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitEventListener;
import com.ibm.wssvt.acme.common.executionunit.ISingleThreadedExecutionUnit;
import com.ibm.wssvt.acme.common.stats.ExecutionStatsFormatter;
import com.ibm.wssvt.acme.common.stats.IExecutionStatsFormatter;
import com.ibm.wssvt.acme.common.util.ClientContext;
import com.ibm.wssvt.acme.common.util.ClientType;

/**
 * $Rev: 631 $
 * $Date: 2007-08-09 14:01:30 -0500 (Thu, 09 Aug 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */

public class AcmeJavaClient {
	
	private List<Thread> runners = new ArrayList<Thread>();
	private static Logger logger = null;	
	/**
	 * 
	 * @param applicationEnvConfig must be provided
	 * @param clientContext must be provided
	 * @param executionUnitEventListenerList optional/additional listeners
	 * @param serverAdapterEventListenerList optional/additional listeners
	 * @param reportedMessageVisitor if null, default will be used which logs to local logger
	 * @param executionStatsFormatter if null, default will be used which is Basic
	 */
	public void executeClient(IApplicationEnvConfig applicationEnvConfig, 
			ClientContext clientContext, 
			List<IExecutionUnitEventListener> executionUnitEventListenerList, 
			List<IServerAdapterEventListener> serverAdapterEventListenerList,
			IReportedMessageVisitor reportedMessageVisitor, 
			IExecutionStatsFormatter executionStatsFormatter) {
		if (applicationEnvConfig == null) {
			throw new IllegalArgumentException("The param applicationEnvConfig is not valid.  It is null.");
		}
		Date clientStartTime = new Date();
		try {			
			if (applicationEnvConfig.getExecutionUnitsCount() > 0 || applicationEnvConfig.getExecutionUnitStacks().size() >0) {
				logger = clientContext.getRootLogger();
				if (logger == null) {
					logger = Logger.getLogger(AcmeJavaClient.class.getName());
				}
				ClientConfiguration clientConfig = applicationEnvConfig.getClientConfiguration();
				logger.logp(Level.INFO, getClass().getName(), "executeClient",
					"Found: " + applicationEnvConfig.getExecutionUnitsCount() + " Execution Units (without Stack EUs)" +
					" and " + applicationEnvConfig.getExecutionUnitStacksCount() + " Execution Unit Stacks. "
					+ " to execute in: " + clientConfig.getThreadCount() + " thread(s) for: " + clientConfig.getRunTime() + " milliseconds.");
				
			//	logger.logp(Level.INFO, getClass().getName(), "executeClient",
			//			"Thread Execution Delay (threadExecDelay): " + clientConfig.getThreadExecDelay());
				for (int i =1; i <= clientConfig.getThreadCount(); i++){	
					ClientContext threadClientContext = getThreadViewClientContext(clientContext, i);
					ExecutionUnitRunner runner = null;
					IExecutionUnitRunnerMonitor runnerMonitor = new ExecutionUnitRunnerMonitor(applicationEnvConfig);
					runner = new ExecutionUnitRunner(applicationEnvConfig, threadClientContext, runnerMonitor,
							executionUnitEventListenerList, serverAdapterEventListenerList, reportedMessageVisitor, executionStatsFormatter);														
					if (i == 1) {
						removeSingleThreadedScenarios(applicationEnvConfig.getExecutionUnits());
						if (applicationEnvConfig.getExecutionUnits().size() > 1) {
							//logger.logp(Level.FINE, getClass().getName(), "executeClient",
								//	"filtered list size: " + applicationEnvConfig.getExecutionUnits().size());
						} else {
							logger.logp(Level.WARNING, getClass().getName(), "executeClient",
									"The size of the scenarios after they have been filtered is 0.  " +
									"Only the first thread: 1 will be running.");							
						}
					}
					
					runner.setName("" + i);					
					if (ClientType.WEB.equals(clientContext.getClientType())){
						// use a blocking call
						runner.executeRun();
					}else{
						// run as a thread.
						runner.start();							
						runners.add(runner);
						if (clientConfig.getThreadExecDelay() >0) {
							try {
								logger.info("ThreadExecDelay is set.  Waiting for: " + clientConfig.getThreadExecDelay() + " to start the thread id: " + i);
								Thread.sleep(clientConfig.getThreadExecDelay());
							} catch (InterruptedException e) {
								String msg ="received an InterruptedException while trying to sleep for threadExecDelay " +
									"Returning a Runtime Exception.";
								logger.logp(Level.SEVERE, getClass().getName(), "executeClient",msg);
								e.printStackTrace();
								throw new RuntimeException(msg,e);
							}
						}
					}									
				}	
				//logger.info("All threads were started ...");
			}else{
				logger = clientContext.getRootLogger();
				if (logger == null) {
					logger = Logger.getLogger(AcmeJavaClient.class.getName());
				}
				logger.logp(Level.SEVERE, getClass().getName(), "executeClient",
						"Configurations were loaded, but found no Execution Units or Execution Unit Stacks to execute ...");				
			}
		}catch (ConfigurationException e) {
			logger.logp(Level.SEVERE, getClass().getName(), "executeClient",
					"Error while loading the configurations. Error is: " + e);
		}
		
		for (Thread thread : runners) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.logp(Level.WARNING, getClass().getName(), "executeClient",
						"cought InterruptedException for the thread join. error: " + e);
			}
		}	
		Date clientEndTime = new Date();
		ExecutionStatsFormatter statsFormatter = new ExecutionStatsFormatter(ExecutionStatsFormatter.FILE_NEW_LINE);
		String stats = statsFormatter.format();
		
		//logger.logp(Level.INFO, getClass().getName(), "executeClient","********* FINAL STATS *******");		
		//logger.logp(Level.INFO, getClass().getName(), "executeClient", "Execution Started: " + clientStartTime + 
		//		" Execution Ended: " + clientEndTime + " which is: " + (clientEndTime.getTime() - clientStartTime.getTime()) + " milliseconds. (clock time)");
		//logger.logp(Level.INFO, getClass().getName(), "executeClient", ". Final Stats are:" + stats);				
	}
				

	private ClientContext getThreadViewClientContext(ClientContext in, int threadId) {
		ClientContext out = new ClientContext(in.getClientId(), threadId );
		out.setInjectedObjects(in.getInjectedObjects());
		out.setRootLogger(in.getRootLogger());
		out.setRootLoggerName(in.getRootLoggerName());		
		out.setClientType(in.getClientType());
		return out;
		
	}


	private static void removeSingleThreadedScenarios(List<IExecutionUnit> iExecutionUnits) {
		for (Iterator<IExecutionUnit> itr = iExecutionUnits.iterator(); itr.hasNext();){
			IExecutionUnit s = itr.next();
			if (s instanceof ISingleThreadedExecutionUnit){
				itr.remove();
			}
		}
	}
		
}
