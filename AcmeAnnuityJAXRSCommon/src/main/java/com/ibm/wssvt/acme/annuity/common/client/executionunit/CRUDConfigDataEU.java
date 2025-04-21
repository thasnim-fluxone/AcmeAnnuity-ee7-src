//***********************************************************
// The CRUDConfigDataEU execution unit is for testing of the 
// @Singleton Mbean support in EJB 3.1.
//
// Randy Erickson 07/02/2009
//***********************************************************

package com.ibm.wssvt.acme.annuity.common.client.executionunit;

// Amirhossein Kiani 09/28/2009 - Added the ConfigData Map store configuration values
//                               (tests concurrency).
//                              - Added the "count" variable to check for concurrency. 

import java.rmi.RemoteException;
import java.util.Map;
import java.util.UUID;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class CRUDConfigDataEU extends AbstractSingletonExecutionUnit {

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
        configData = null;

        logger.fine("Creating New ConfigData");
        try {
            configData = createConfigData();
        } catch (Exception e) {
            logger.warning("Failed to create new ConfigData. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Create New ConfigData");
        try {
            verifyCreate(configData);
        } catch (Exception e) {
            logger.info("Create New ConfigData verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }


        logger.fine("Updating ConfigData");
        IConfigData updated = null;
        try {
            updated = updateConfigData(configData);
        } catch (Exception e) {
            logger.warning("Failed to update ConfigData. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Update ConfigData");
        try {
            verifyUpdate(updated);
        } catch (Exception e) {
            logger.info("Update ConfigData verification failed. Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Deleting ConfigData");
        try {
            deleteConfigData(updated);
        } catch (Exception e) {
            logger.info("Failed to delete ConfigData. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Delete ConfigData");
        try {
            verifyDelete(updated);
        } catch (Exception e) {
            logger.info("Delete ConfigData verification failed.  Error: " + e);
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

    private IConfigData createConfigData() throws EntityAlreadyExistsException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, RemoteException {
        IConfigData configData = BasicExecutionUnitLibrarry
        .getConfigData(getAnnuityBeansFactory());
        configData.setConfiguration(getConfiguration());
        configData.setId(UUID.randomUUID().toString());
        configData = getServerAdapter().createConfigData(configData);
        return configData;
    }

    private IConfigData updateConfigData(IConfigData before)
    throws EntityNotFoundException,
    ServerAdapterCommunicationException, InvalidArgumentException,
    ServerInternalErrorException, RemoteException {
        IConfigData after = BasicExecutionUnitLibrarry
        .getConfigData(getAnnuityBeansFactory());
        after.setId(before.getId());
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().findConfigDataById(after);
        Map<String, String> updatedMap= after.getConfigMap();
        updatedMap.put("Updated", "Updated");
        after.setConfigMap(updatedMap);
        after.getConfigMap();
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateConfigData(after);
        return after;
    }


    private void deleteConfigData(IConfigData configData)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
        IConfigData removed = BasicExecutionUnitLibrarry
        .getConfigData(getAnnuityBeansFactory());
        removed.setId(configData.getId());
        removed.setConfiguration(getConfiguration());
        getServerAdapter().deleteConfigData(removed);
    }

    private void verifyRetrieve(IConfigData configData) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the configData with id.
        ExecutionUnitVerificationHelper.assertValidId(this, configData,
                "ConfigData retrieved from server NOT valid",
        "ConfigData Retrieve mismatch.");
    }

    private void verifyCreate(IConfigData configData) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the configData with id.
        IConfigData results = getAnnuityBeansFactory().createConfigData();
        results.setId(configData.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findConfigDataById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, configData, results,
                "ConfigData from client is not equal to DB value",
        "ConfigData Create mismacth was found.");
    }

    private void verifyUpdate(IConfigData configData) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the configData with id.
        IConfigData results = getAnnuityBeansFactory().createConfigData();
        results.setId(configData.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findConfigDataById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, configData, results,
                "ConfigData from client is not equal to DB value",
        "ConfigData Update mismacth was found.");
    }

    private void verifyDelete(IConfigData configData) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the configData with id.
        IConfigData results = getAnnuityBeansFactory().createConfigData();
        results.setId(configData.getId());
        results.setConfiguration(getConfiguration());
        try {
            results = getServerAdapter().findConfigDataById(results);
            if (results != null) {
                throw new ExecutionUnitVerificationException("Deletion of ConfigData with ID = " + configData.getId() 
                        + "was not successful! The object still exists in DB." );
            }
        } catch (EntityNotFoundException e) {

        }
    }
}
