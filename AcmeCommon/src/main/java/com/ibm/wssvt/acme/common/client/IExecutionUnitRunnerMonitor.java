package com.ibm.wssvt.acme.common.client;

import com.ibm.wssvt.acme.common.adapter.IServerAdapterEventListener;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnitEventListener;

public interface IExecutionUnitRunnerMonitor extends IExecutionUnitEventListener, IServerAdapterEventListener {

	public IExecutionUnitRunner getRunner();

	public void setRunner(IExecutionUnitRunner runner);

}