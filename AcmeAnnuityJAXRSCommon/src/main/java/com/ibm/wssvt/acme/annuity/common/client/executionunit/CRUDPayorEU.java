package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/* 
 * $Rev$
 * $Date$
 * $Author$
 * $LastChangedBy$
 */

public class CRUDPayorEU extends AbastractAnnuityExecutionUnit {

    private static final long serialVersionUID = -8980124038714550844L;

    private AcmeLogger logger;

    public void execute() {
        setScenarioVariables();

        logger.fine("Creating Payor");
        IPayor payor = null;
        try {
            payor = createPayor();
        } catch (Exception e) {
            logger.warning("Failed to create Payor. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Create Payor");
        try {
            verifyPayorValue(payor);
        } catch (Exception e) {
            logger.info("Create Payor verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Updating Payor");
        IPayor updated = null;
        try {
            updated = updatePayor(payor);
        } catch (Exception e) {
            logger.warning("Failed to update Payor. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Update Payor");
        try {
            verifyPayorValue(updated);
        } catch (Exception e) {
            logger.info("Update Payor verification failed. Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Deleting Payor");
        try {
            deletePayor(updated);
        } catch (Exception e) {
            logger.info("Failed to delete Payor. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Delete Payor");
        try {
            verifyDelete(updated);
        } catch (Exception e) {
            logger.info("Delete Payor verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }
    }

    private void setScenarioVariables() {
        logger = getLogger(getClass().getName());
    }

    private IPayor createPayor() throws EntityAlreadyExistsException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, RemoteException {
        IPayor payor = BasicExecutionUnitLibrarry
        .getPayor(getAnnuityBeansFactory());
        payor.setConfiguration(getConfiguration());
        payor = getServerAdapter().createPayor(payor);
        return payor;
    }

    private IPayor updatePayor(IPayor before)
    throws EntityNotFoundException,
    ServerAdapterCommunicationException, InvalidArgumentException,
    ServerInternalErrorException, RemoteException {
        IPayor after = BasicExecutionUnitLibrarry
        .getPayor(getAnnuityBeansFactory());
        after.setId(before.getId());
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().findPayorById(after);
        after.setName(after.getName() + "-updated");
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updatePayor(after);
        return after;
    }

    private void deletePayor(IPayor payor)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
        IPayor removed = BasicExecutionUnitLibrarry
        .getPayor(getAnnuityBeansFactory());
        removed.setId(payor.getId());
        removed.setConfiguration(getConfiguration());
        getServerAdapter().deletePayor(removed);
    }

    private void verifyPayorValue(IPayor payor) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the contact with id.
        IPayor results = getAnnuityBeansFactory().createPayor();
        results.setId(payor.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findPayorById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, payor, results,
                "Payor from client is not equal to DB value",
        "Payor Create mismacth was found.");
    }

    private void verifyDelete(IPayor payor) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException {
        //AsynchMethodHelper.verifyPayerDelete(payor, this, logger);
    }
}
