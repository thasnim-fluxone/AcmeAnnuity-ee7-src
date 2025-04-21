//***********************************************************
// The ISingletonServerAdapter interface is for the 
// CRUDConfigDataEU execution unit for testing of the 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;

public interface ISingletonServerAdapter extends IServerAdapter{


    //14880 begin
	public IConfigData getServerConfigData(Configrable<String, String> configuration) throws ServerAdapterCommunicationException ;
	public void setServerConfigData(IConfigData configData) throws ServerAdapterCommunicationException ;
	public void refreshConfigDataFromDB() throws EntityNotFoundException, 
    InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException;
	public void updateConfigDataInDB(IConfigData configData) throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException;
    public void testReadConcurrency(IConfigData configData) throws ServerAdapterCommunicationException;
    public void testWriteConcurrency(IConfigData configData) throws ServerAdapterCommunicationException;
	//14880 end
	
	
	public IConfigData createConfigData(IConfigData configData) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;	
	public IConfigData updateConfigData(IConfigData configData) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException;
	public void deleteConfigData(IConfigData configData) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException;
	public IConfigData findConfigDataById(IConfigData configData) 
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException;		
	
}
