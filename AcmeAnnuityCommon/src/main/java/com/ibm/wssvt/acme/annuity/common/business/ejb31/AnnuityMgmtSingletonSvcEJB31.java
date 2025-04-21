//***********************************************************
// The AnnuityMgmtSingletonSvcEJB31 interface is for the 
// EJB31SingletonServerAdapter adapter for testing 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.business.ejb31;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.bean.Configrable;


public interface AnnuityMgmtSingletonSvcEJB31  {

    public void updateConfigDataInDB(IConfigData configData) throws InvalidArgumentException, ServerInternalErrorException;
    public void refreshConfigDataFromDB() throws EntityNotFoundException, 
    InvalidArgumentException, ServerInternalErrorException;
    public IConfigData getServerConfigData(Configrable<String, String> configuration);
    public void setServerConfigData(IConfigData configData);
    public void testReadConcurrency(IConfigData configData);
    public void testWriteConcurrency(IConfigData configData);
    
	// mgmt
	public IConfigData createConfigData(IConfigData configData) 
		throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException;
	public IConfigData updateConfigData(IConfigData configData) 
		throws ServerInternalErrorException, InvalidArgumentException;
	public void deleteConfigData(IConfigData configData) 
		throws ServerInternalErrorException, InvalidArgumentException;	
				
	// search
	public IConfigData findConfigDataById(IConfigData configData) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;

}
