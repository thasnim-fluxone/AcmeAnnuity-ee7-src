package com.ibm.wssvt.acme.common.executionunit;

import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.bean.IBeansFactory;
import com.ibm.wssvt.acme.common.util.IManagedClientEntity;

public interface IExecutionUnit extends Configrable<String, String>, IManagedClientEntity { 
	public void setDescription(String description);
	public String getDescription();
	public void setServerAdapter(IServerAdapter adapter);
	public IServerAdapter getServerAdapter();
		
	public void execute();
	
	public void addEventListener(IExecutionUnitEventListener listener);
	public boolean removeEventListener(IExecutionUnitEventListener listener);	
	public void clearEvents();
	public void clearListeners();
	public void fireToListeners();
	public void setBeansFactory(IBeansFactory beansFactory);
	public IBeansFactory getBeansFactory();
	

}
