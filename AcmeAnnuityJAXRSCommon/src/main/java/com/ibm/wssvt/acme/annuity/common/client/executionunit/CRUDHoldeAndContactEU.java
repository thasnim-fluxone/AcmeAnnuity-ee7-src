package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
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

public class CRUDHoldeAndContactEU extends AbastractAnnuityExecutionUnit {

    private static final long serialVersionUID = -8978758038714550844L;

    private AcmeLogger logger;

    public void execute() {
        setScenarioVariables();

        logger.fine("Creating Contact");
        IContact contact = null;
        try {
            contact = createContact();
        } catch (Exception e) {
            logger.warning("Failed to create Contact. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Create Contact");
        try {
            verifyContactValue(contact);
        } catch (Exception e) {
            logger.info("Create Contact verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Creating AnnuityHolder");
        IAnnuityHolder holder = null;
        try {
            holder = createAnnuityHolder(contact);
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
        }

        logger.fine("Updating Contact");
        IContact updatedContact = null;
        try {
            updatedContact = updateContact(contact);
        } catch (Exception e) {
            logger.warning("Failed to update Contact. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Update Contact");
        try {
            verifyContactValue(updatedContact);
        } catch (Exception e) {
            logger.info("Update Contact verification failed. Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Updating AnnuityHolder");
        IAnnuityHolder updatedHolder = null;
        try {
            updatedHolder = updateAnnuityHolder(holder, updatedContact);
        } catch (Exception e) {
            logger.warning("Failed to update AnnuityHolder. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Update AnnuityHolder");
        try {
            verifyAnnuityHolderValue(updatedHolder);
        } catch (Exception e) {
            logger.info("Update AnnuityHolder verification failed. Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Deleting AnnuityHolder");
        try {
            deleteAnnuityHolder(updatedHolder);
        } catch (Exception e) {
            logger.info("Failed to delete AnnuityHolder. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Delete AnnuityHolder");
        try {
            verifyDelete(updatedHolder);
        } catch (Exception e) {
            logger.info("Delete AnnuityHolder verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }

        logger.fine("Deleting Contact");
        try {
            deleteContact(updatedContact);
        } catch (Exception e) {
            logger.info("Failed to delete Contact. Error: " + e);
            getExecutionUnitEvent().addException(e);
            return;
        }

        logger.fine("Verifying Delete Contact");
        try {
            verifyDelete(updatedContact);
        } catch (Exception e) {
            logger.info("Delete Contact verification failed.  Error: " + e);
            getExecutionUnitEvent().addException(e);
        }
    }

    private void setScenarioVariables() {
        logger = getLogger(getClass().getName());
    }

    private IAnnuityHolder createAnnuityHolder(IContact contact)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
        IAnnuityHolder holder = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        holder.setCategory(getRandomEnum(AnnuityHolderCategory.class));
        holder.setContact(contact);
        holder.setConfiguration(getConfiguration());
        holder = getServerAdapter().createAnnuityHolder(holder);
        return holder;
    }

    private IAnnuityHolder updateAnnuityHolder(IAnnuityHolder before,
            IContact updatedContact) throws EntityNotFoundException,EntityAlreadyExistsException,
            ServerAdapterCommunicationException, InvalidArgumentException,
            ServerInternalErrorException, RemoteException {
        IAnnuityHolder after = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        after.setId(before.getId());
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().findHolderById(after);
        after.setGovernmentId(after.getGovernmentId() + "-updated");
        after.setContact(updatedContact);
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateAnnuityHolder(after);
        after.setContact(null);
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateAnnuityHolder(after);
        IContact newc = createContact();
        after.setContact(newc);
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateAnnuityHolder(after);
        return after;
    }

    private void deleteAnnuityHolder(IAnnuityHolder holder)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
        String contactId = holder.getContact().getId();
        IAnnuityHolder removed = BasicExecutionUnitLibrarry
        .getAnnuityHolder(getAnnuityBeansFactory());
        removed.setId(holder.getId());
        removed.setConfiguration(getConfiguration());
        getServerAdapter().deleteAnnuityHolder(removed);
        IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
        contact.setId(contactId);
        contact.setConfiguration(getConfiguration());
        getServerAdapter().deleteContact(contact);
    }

    private void verifyAnnuityHolderValue(IAnnuityHolder holder)
    throws EntityNotFoundException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException,
    ExecutionUnitVerificationException, RemoteException {
        // read the holder with id.
        IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
        results.setId(holder.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findHolderById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, holder, results,
                "AnnuityHolder from client is not equal to DB value",
        "AnnuityHolder Create mismacth was found.");
    }

    private void verifyDelete(IAnnuityHolder holder)
    throws EntityNotFoundException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException,
    ExecutionUnitVerificationException, RemoteException, InterruptedException, InvalidExecutionUnitParameterException {        
        //AsynchMethodHelper.verifyHolderDelete(holder, this, logger);
    }
    


    
    private IContact createContact() throws EntityAlreadyExistsException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, RemoteException {
        IContact contact = BasicExecutionUnitLibrarry
        .getContact(getAnnuityBeansFactory());
        contact.setConfiguration(getConfiguration());
        contact.setContactType(getRandomEnum(ContactType.class));
        contact = getServerAdapter().createContact(contact);
        return contact;
    }

    private IContact updateContact(IContact before)
    throws EntityNotFoundException,
    ServerAdapterCommunicationException, InvalidArgumentException,
    ServerInternalErrorException, RemoteException {
        IContact after = BasicExecutionUnitLibrarry
        .getContact(getAnnuityBeansFactory());
        after.setId(before.getId());
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().findContactById(after);
        after.setPhone(after.getPhone() + "-updated");
        after.setEmail(after.getEmail() + "-updated");
        after.setConfiguration(getConfiguration());
        after = getServerAdapter().updateContact(after);
        return after;
    }

    private void deleteContact(IContact contact)
    throws EntityAlreadyExistsException, InvalidArgumentException,
    ServerAdapterCommunicationException, ServerInternalErrorException , RemoteException{
        IContact removed = BasicExecutionUnitLibrarry
        .getContact(getAnnuityBeansFactory());
        removed.setId(contact.getId());
        removed.setConfiguration(getConfiguration());
        getServerAdapter().deleteContact(removed);
    }

    private void verifyContactValue(IContact contact) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
        // read the contact with id.
        IContact results = getAnnuityBeansFactory().createContact();
        results.setId(contact.getId());
        results.setConfiguration(getConfiguration());
        results = getServerAdapter().findContactById(results);
        ExecutionUnitVerificationHelper.assertEqual(this, contact, results,
                "Contact from client is not equal to DB value",
        "Contact Create mismacth was found.");
    }

    private void verifyDelete(IContact contact) throws EntityNotFoundException,
    InvalidArgumentException, ServerAdapterCommunicationException,
    ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException, InterruptedException, InvalidExecutionUnitParameterException {
        //AsynchMethodHelper.verifyContactDelete(contact, this, logger);
    }
}
