//***********************************************************
// The SingletonConcurrencyEU execution unit is for testing of the 
// Singleton bean concurrency support in EJB 3.1.
//
// Amirhossein Kiani 09/29/09
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.client.executionunit;


import java.rmi.RemoteException;
import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class SingletonConcurrencyEU extends AbstractSingletonExecutionUnit {

    private static final long serialVersionUID = -4657309483111671699L;

    private AcmeLogger logger;

    public void execute() {
        setScenarioVariables();

        IConfigData configData = null;

        logger.fine("Retrieve Initial ConfigData");
        try {
            configData = retrieveConfigData();
        } catch (Exception e) {
            logger.warning("Failed to retrieve ConfigData. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying retrieve ConfigData");
        try {
            verifyRetrieve(configData);
        } catch (Exception e) {
            logger.info("Retrieve ConfigData verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }
        
        logger.fine("Verifying Singleton Container Managed Concurrency");
        try {
            configData.setConfiguration(getConfiguration());

            getServerAdapter().testReadConcurrency(configData);
            getServerAdapter().testWriteConcurrency(configData);
        } catch (Exception e) {
            logger.info("Singleton Container Managed Concurrency check faild!  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

    }

    private void setScenarioVariables() {
        logger = getLogger(getClass().getName());
    }

    private IConfigData retrieveConfigData() throws EntityAlreadyExistsException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, RemoteException {
        IConfigData configData = BasicExecutionUnitLibrarry
        .getConfigData(getAnnuityBeansFactory());
        configData.setConfiguration(getConfiguration());
        configData = getServerAdapter().getServerConfigData(configData);
        return configData;
    }

    private void verifyRetrieve(IConfigData configData) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the configData with id.
        ExecutionUnitVerificationHelper.assertValidId(this, configData,
                "ConfigData retrieved from server NOT valid",
        "ConfigData Retrieve mismatch.");
    }
}
