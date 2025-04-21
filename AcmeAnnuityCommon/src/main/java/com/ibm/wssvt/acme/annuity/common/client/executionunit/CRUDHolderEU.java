package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
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

public class CRUDHolderEU extends AbastractAnnuityExecutionUnit {

    private static final long serialVersionUID = -8980258038714550844L;

    private AcmeLogger logger;

    public void execute() {
        setScenarioVariables();

        logger.fine("Creating AnnuityHolder");
        IAnnuityHolder holder = null;
        try {
            holder = createAnnuityHolder();
        } catch (Exception e) {
            logger.warning("Failed to create AnnuityHolder. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Create AnnuityHolder");
        try {
            verifyAnnuityHolderValue(holder);
        } catch (Exception e) {
            logger.info("Create AnnuityHolder verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Updating AnnuityHolder");
        IAnnuityHolder updated = null;
        try {
            updated = updateAnnuityHolder(holder);
        } catch (Exception e) {
            logger.warning("Failed to update AnnuityHolder. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Update AnnuityHolder");
        try {
            verifyAnnuityHolderValue(updated);
        } catch (Exception e) {
            logger.info("Update AnnuityHolder verification failed. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Deleting AnnuityHolder");
        try {
            deleteAnnuityHolder(updated);
        } catch (Exception e) {
            logger.info("Failed to delete AnnuityHolder. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Delete AnnuityHolder");
        try {
            verifyDelete(updated);
        } catch (Exception e) {
            logger.info("Delete AnnuityHolder verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }
    }

    private void setScenarioVariables() {
        logger = getLogger(getClass().getName());
    }

    private IAnnuityHolder createAnnuityHolder() throws EntityAlreadyExistsException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, RemoteException {
        IAnnuityHolder holder = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        holder.setCategory(getRandomEnum(AnnuityHolderCategory.class));
        holder.setConfiguration(getConfiguration());
        holder = getServerAdapter().createAnnuityHolder(holder);
        return holder;
    }

    private IAnnuityHolder updateAnnuityHolder(IAnnuityHolder before)
    throws EntityNotFoundException,
    ServerAdapterCommunicationException, InvalidArgumentException,
    ServerInternalErrorException, RemoteException {
        IAnnuityHolder after = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        after.setId(before.getId());
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().findHolderById(after);
        after.setGovernmentId(after.getGovernmentId() + "-updated");
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateAnnuityHolder(after);
        return after;
    }

    private void deleteAnnuityHolder(IAnnuityHolder holder)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
        IAnnuityHolder removed = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        removed.setId(holder.getId());
        removed.setConfiguration(getConfiguration());
        getServerAdapter().deleteAnnuityHolder(removed);
    }

    private void verifyAnnuityHolderValue(IAnnuityHolder holder) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the holder with id.
        IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
        results.setId(holder.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findHolderById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, holder, results,
                "AnnuityHolder from client is not equal to DB value",
        "AnnuityHolder Create mismacth was found.");
    }


    private void verifyDelete(IAnnuityHolder holder) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException, InvalidExecutionUnitParameterException, InterruptedException {
        AsynchMethodHelper.verifyHolderDelete(holder, this, logger);
    }
}
