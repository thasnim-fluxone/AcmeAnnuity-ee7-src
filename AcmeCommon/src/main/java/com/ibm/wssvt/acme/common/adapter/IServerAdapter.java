package com.ibm.wssvt.acme.common.adapter;

import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.util.IManagedClientEntity;

public interface IServerAdapter extends Configrable<String, String>, IManagedClientEntity {

	public String getId();
	public void setId(String id);
	public void addEventListener(IServerAdapterEventListener listener);
	public boolean removeEventListener(IServerAdapterEventListener listener);	
	public void clearEvents();
	public void clearListeners();
	public void fireToListeners();
	
}
