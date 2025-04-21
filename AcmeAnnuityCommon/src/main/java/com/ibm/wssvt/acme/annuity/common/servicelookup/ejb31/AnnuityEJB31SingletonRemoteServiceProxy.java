//***********************************************************
// The AnnuityEJB31SingletonRemoteServiceProxy class 
// provides the remote service proxy service for the  
// EJB31SingletonServiceLookup.
//
// Randy Erickson 07/02/2009
// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//                              - Added the "count" variable to check for concurrency. 
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb31;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuitySingletonService;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtSingletonSvcEJB31;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.bean.Configrable;

public class AnnuityEJB31SingletonRemoteServiceProxy implements IAnnuitySingletonService {
	AnnuityMgmtSingletonSvcEJB31 remote;		
	
	public AnnuityEJB31SingletonRemoteServiceProxy(AnnuityMgmtSingletonSvcEJB31 remote) {		
		this.remote = remote;
	}
		
	public void refreshConfigDataFromDB() throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException  {		
		remote.refreshConfigDataFromDB();
	}
	public IConfigData createConfigData(IConfigData configData) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {		
		return remote.createConfigData(configData);
	}
	public IConfigData updateConfigData(IConfigData configData) throws ServerInternalErrorException, InvalidArgumentException {
		return remote.updateConfigData(configData);
	}
	public void deleteConfigData(IConfigData configData) throws ServerInternalErrorException, InvalidArgumentException {
		remote.deleteConfigData(configData);
	}
	public IConfigData findConfigDataById(IConfigData configData) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException  {		
		return remote.findConfigDataById(configData);
	}

    public IConfigData getServerConfigData(Configrable<String, String> configuration) {
        return remote.getServerConfigData(configuration);
    }

    public void setServerConfigData(IConfigData configData) {
        remote.setServerConfigData(configData);
    }

    public void updateConfigDataInDB(IConfigData configData)
            throws InvalidArgumentException, ServerInternalErrorException {
        remote.updateConfigDataInDB(configData);
    }


    public void testReadConcurrency(IConfigData configData) {
        remote.testReadConcurrency( configData);
    }
    public void testWriteConcurrency(IConfigData configData) {
        remote.testWriteConcurrency( configData);
    }
	
}
