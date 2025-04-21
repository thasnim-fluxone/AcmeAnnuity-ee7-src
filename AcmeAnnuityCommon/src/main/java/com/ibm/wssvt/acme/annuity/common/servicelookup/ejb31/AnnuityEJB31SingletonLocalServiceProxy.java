//***********************************************************
// The AnnuityEJB31SingletonLocalServiceProxy class 
// provides the local service proxy service for the  
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
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtSingletonSvcEJB31Local;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.bean.Configrable;

public class AnnuityEJB31SingletonLocalServiceProxy implements IAnnuitySingletonService {
	private AnnuityMgmtSingletonSvcEJB31Local local;		
	
	public AnnuityEJB31SingletonLocalServiceProxy(AnnuityMgmtSingletonSvcEJB31Local local) {
		super();
		this.local = local;
	}
		
	public void refreshConfigDataFromDB() throws EntityNotFoundException, InvalidArgumentException, ServerInternalErrorException  {		
		local.refreshConfigDataFromDB();
	}
	public IConfigData createConfigData(IConfigData configData) throws ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException {		
		return local.createConfigData(configData);
	}
	public IConfigData updateConfigData(IConfigData configData) throws ServerInternalErrorException, InvalidArgumentException {
		return local.updateConfigData(configData);
	}
	public void deleteConfigData(IConfigData configData) throws ServerInternalErrorException, InvalidArgumentException {
		local.deleteConfigData(configData);
	}
	public IConfigData findConfigDataById(IConfigData configData) throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException  {		
		return local.findConfigDataById(configData);
	}
    public IConfigData getServerConfigData(Configrable<String, String> configuration) {
       return local.getServerConfigData(configuration);
    }

    public void setServerConfigData(IConfigData configData) {
        local.setServerConfigData(configData);
    }

    public void updateConfigDataInDB(IConfigData configData)
            throws InvalidArgumentException, ServerInternalErrorException {
        local.updateConfigDataInDB(configData);
    }


    public void testReadConcurrency(IConfigData configData) {
        local.testReadConcurrency( configData);
    }

    public void testWriteConcurrency(IConfigData configData) {
        local.testWriteConcurrency( configData);
    }
    
   

}
