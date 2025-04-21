package com.ibm.wssvt.acme.common.stats;

import java.util.HashMap;
import java.util.Map;

public class ExecutionStatsFormatter implements IExecutionStatsFormatter {
	public static final String HTML_NEW_LINE = "<BR>";
	public static final String FILE_NEW_LINE = "\n";

	private String newLineMarker;

	public ExecutionStatsFormatter(String newLineMarker) {
		this.newLineMarker = newLineMarker;
		if (this.newLineMarker == null) {
			this.newLineMarker = FILE_NEW_LINE;
		}
	}

	public String getNewLineMarker() {
		return this.newLineMarker;
	}

	public String format() {
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb.append("Execution Stats Data Snapshot:");
		sb.append(getExceptionTotals());
		sb.append(getNewLineMarker());
		sb.append(getServerInfoTotals());
		sb.append(getNewLineMarker());
		sb.append(getThreadStatus());
		sb.append(getNewLineMarker());
		sb.append(getCRUDTotals());
		sb.append(getNewLineMarker());
		sb.append(getRequestTotals());
		sb.append(getNewLineMarker());
		return sb.toString();
	}

	protected String getRequestTotals() {
		long requests = ExecutionStats.getInstance().getRequest();
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb.append("The following is the total Server Requests: ");
		sb.append(getNewLineMarker());
		sb.append("Overall Requests : ").append(requests);
		return sb.toString();
	}

	protected String getCRUDTotals() {
		long total = ExecutionStats.getInstance().getCRUDTotal();
		long create = ExecutionStats.getInstance().getCreate();
		long read = ExecutionStats.getInstance().getRead();
		long update = ExecutionStats.getInstance().getUpdate();
		long delete = ExecutionStats.getInstance().getDelete();
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb.append("The following are the CRUD Counts: ");
		if (total == 0) {
			sb.append(getNewLineMarker());
			sb.append("No CRUD Operations.");
		} else {
			sb.append(getNewLineMarker());
			sb.append("All CRUD Count   : ").append(total);
			sb.append(getNewLineMarker());
			sb.append("Total Create     : ").append(create).append(
					" which is: ").append((create * 100 / total)).append(
					"% of all CRUD operations");
			sb.append(getNewLineMarker());
			sb.append("Total Read       : ").append(read).append(" which is: ")
					.append((read * 100 / total)).append(
							"% of all CRUD operations");
			sb.append(getNewLineMarker());
			sb.append("Total Update     : ").append(update).append(
					" which is: ").append((update * 100 / total)).append(
					"% of all CRUD operations");
			sb.append(getNewLineMarker());
			sb.append("Total Delete     : ").append(delete).append(
					" which is: ").append((delete * 100 / total)).append(
					"% of all CRUD operations");
		}
		return sb.toString();
	}

	protected String getServerInfoTotals() {
		Map<String, Long> serverRunInfo = ExecutionStats.getInstance()
				.getServerRunInfo();
		Map<String, Long> overallServerRunInfo = ExecutionStats.getInstance()
				.getOverallServerRunInfo();
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb.append("The following are the Servers and their hit count: ");
		if (serverRunInfo.isEmpty()) {
			sb.append(getNewLineMarker());
			sb.append("No Server Run Info Available.");
		} else if (overallServerRunInfo.isEmpty()) {
			synchronized (serverRunInfo) {
				for (String serverName : serverRunInfo.keySet()) {
					sb.append(getNewLineMarker());
					long hitCount = serverRunInfo.get(serverName);
					sb.append("Server: ").append(serverName).append(
							" was hit: (approximate) ").append(hitCount)
							.append((hitCount > 1) ? " times." : " time.");
				}
			}
		} else {
			synchronized (overallServerRunInfo) {
				for (String serverName : overallServerRunInfo.keySet()) {
					sb.append(getNewLineMarker());
					long hitCount;
					long overallHitCount = overallServerRunInfo.get(serverName);
					if (serverRunInfo.get(serverName) == null) {
						hitCount = 0;
					} else {
						hitCount = serverRunInfo.get(serverName);
					}
					sb.append("Server: ").append(serverName).append(
							" was hit: (approximate) ").append(hitCount)
							.append((hitCount > 1) ? " times" : " time");
					sb.append(" by the client, and ").append(overallHitCount)
							.append((overallHitCount > 1) ? " times" : " time")
							.append(" overall.");
				}
			}
		}
		return sb.toString();
	}

	protected String getThreadStatus() {
		Map<Integer, ThreadInfoStats> threadInfoStatsMap = new HashMap<Integer, ThreadInfoStats>(
				ExecutionStats.getInstance().getThreadInfoStatsMap());
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb
				.append("The following are the threads, their loop count and Status: ");
		if (threadInfoStatsMap.isEmpty()) {
			sb.append(getNewLineMarker());
			sb.append("No Loops executed!");
		} else {
			synchronized (threadInfoStatsMap) {
				for (Integer threadId : threadInfoStatsMap.keySet()) {
					ThreadInfoStats threadStats = threadInfoStatsMap
							.get(threadId);
					sb.append(getNewLineMarker());
					sb
							.append("Thread id: ")
							.append(threadId)
							.append(" Looped: ")
							.append(threadStats.getLoopCount())
							.append(
									(threadStats.getLoopCount() > 1) ? " times"
											: " time")
							.append(".  Status: ")
							.append(threadStats.getExecutionStatus())
							.append(".  Status Date: ")
							.append(threadStats.getLastStatusDate())
							.append(".  Start Time: ")
							.append(threadStats.getThreadStartTime())
							.append(".  End Time: ")
							.append(
									(threadStats.getThreadEndTime() == null) ? "still running ..."
											: threadStats.getThreadEndTime())
							.append(".  Clock Time: ").append(
									threadStats.getExecutionClockTime())
							.append(" milliseconds").append(".  Active Time: ")
							.append(threadStats.getExecutionActiveTime())
							.append(" milliseconds").append(getNewLineMarker());
				}
			}
		}
		return sb.toString();
	}

	protected String getExceptionTotals() {
		long crudTotal = ExecutionStats.getInstance().getCRUDTotal();
		Map<String, ExceptionInfoStats> exceptionCountMap = new HashMap<String, ExceptionInfoStats>(
				ExecutionStats.getInstance().getExceptionCount());
		StringBuffer sb = new StringBuffer();
		sb.append(getNewLineMarker());
		sb
				.append("The following errors were reported so far: (Based on the Exception Handler Configuration)");
		if (exceptionCountMap.isEmpty()) {
			sb.append(" No Errors Reported.");
		} else {
			synchronized (exceptionCountMap) {
				for (String exName : exceptionCountMap.keySet()) {
					ExceptionInfoStats stats = exceptionCountMap.get(exName);
					if (crudTotal == 0) {
						sb.append(getNewLineMarker());
						sb
								.append("Exception Class: ")
								.append(exName)
								.append(" was encountered: ")
								.append(stats.getCount())
								.append(
										" time(s).  Ratio to total CRUD: INFINITY. (Exception Count/Total CURD).  Note: Total CRUD is ZERO\n");
						sb.append(getNewLineMarker());
						sb.append("Detail Data about this Exception:");
						sb.append(getNewLineMarker());
						Map<Long, String> exceptionData = stats
								.getExceptionData();
						synchronized (exceptionData) {
							for (Long count : exceptionData.keySet()) {
								sb.append("   Incident number: ").append(count)
										.append(" in: ").append(
												exceptionData.get(count));
								sb.append(getNewLineMarker());
							}
						}
					} else {
						sb.append(getNewLineMarker());
						sb.append("Exception Class: ").append(exName).append(
								" was encountered: ").append(stats.getCount())
								.append(" time(s).  Ratio to total CRUD: ");
						float f = (stats.getCount() * 100) / crudTotal;
						sb.append(f)
								.append(" % (Exception Count/Total CURD)\n");

						sb.append(getNewLineMarker());
						sb.append("Detail Data about this Exception:");
						sb.append(getNewLineMarker());
						Map<Long, String> exceptionData = stats
								.getExceptionData();
						synchronized (exceptionData) {
							for (Long count : exceptionData.keySet()) {
								sb.append("   Incident number: ").append(count)
										.append(" in: ").append(
												exceptionData.get(count));
								sb.append(getNewLineMarker());
							}
						}
					}
				}
			}
		}
		return sb.toString();
	}

}
