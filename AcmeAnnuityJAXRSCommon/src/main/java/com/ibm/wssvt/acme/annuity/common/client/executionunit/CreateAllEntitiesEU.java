package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.RiderType;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class CreateAllEntitiesEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8986934038714550844L;
	private AcmeLogger logger;
	boolean enableVerify = true; // so that we can default to true
	
	private static final String ENABLE_VERIFY_KEY = "enableVerify";
		
	public void execute() {								
		setScenarioVariables();	// this will set the logger as well.
		logger.fine("creating contact");
		IContact contact = null;
		try {		
			contact = createContact();		
		} catch (Exception e) {
			//logger.warning("failed to create Contact Successfully.  Error: " + e);
			getExecutionUnitEvent().addException(e);			
			return;
		}
		
		try{
			if (enableVerify) { verifyContactValues(contact); }
		}catch (Exception e) {
			logger.info("Create Contact verification failed.  Error: " + e);
			getExecutionUnitEvent().addException(e);
			// do not return, as this might be a small bug that we can bypass
		}
			
		
		logger.fine("crating annuity holder");
		IAnnuityHolder annuityHolder = null;
		try{
			annuityHolder = createAnnuityHolder(contact);			
		}catch (Exception e) {
			logger.warning("failed to create Annuity Holder Successfully. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}	
		try {
			if (enableVerify) { verifyAnnuityHolderValues(annuityHolder); }
		} catch (Exception e) {
			logger.info("failed to verify create annuity holder successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("creating payor");
		IPayor payor = null;
		try{
			payor = createPayor();
		}catch(Exception e) {
			logger.warning("failed to create payor successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		try {
			if (enableVerify) { verifyPayorValues(payor); }
		} catch (Exception e) {
			logger.info("failed to verify create payor successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
		}
		
		logger.fine("creating annuity");
		IAnnuity annuity =null;		
		AnnuityType annuityType = getRandomEnum(AnnuityType.class);
		try {			
			annuity = createAnnuityWithRider(annuityType);
		} catch (Exception e) {
			logger.warning("failed to create annuity successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
			
		}
		try {
			logger.info("verify annuity with rider");
			if (enableVerify) { verifyAnnuityValues(annuity, annuityType); }			
		} catch (Exception e) {
			logger.info("failed to verify create annuity successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
		}		
		
		logger.fine("upating annuity");
		try {
			logger.info("create annuity with payout");
			annuity = createAnnuityPayout(annuity);
		} catch (Exception e) {
			logger.warning("failed to create annuity successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
			
		}
		try {
			if (enableVerify) { verifyAnnuityValues(annuity, annuityType); }			
		} catch (Exception e) {
			logger.info("failed to verify create annuity successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
		}		
		
		logger.fine("upating annuity");
		try{
			annuity.getPayors().add(payor);
			annuity.setAnnuityHolderId(annuityHolder.getId());
			annuity.setConfiguration(getConfiguration());
			getServerAdapter().updateAnnuity(annuity);			
		}catch (Exception e) {
			logger.warning("failed to update annuity successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		try {
			if (enableVerify) { 
				verifyAnnuityValues(annuity, annuityType); 
			}
		} catch (Exception e) {
			logger.info("failed to verify annuity update successfuly. Error: " +e);
			getExecutionUnitEvent().addException(e);		
		}	
		
		logger.fine("scenario: " + getDescription() + " completed.");
	}



	private IAnnuity createAnnuityPayout(IAnnuity annuity) 
	throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, RemoteException {
		IPayout payout = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());		
		payout.setAnnuity(annuity);
		payout.setConfiguration(getConfiguration());
		payout = getServerAdapter().createPayout(payout);
		annuity.getPayouts().add(payout);		
		return annuity;

	}


	private IAnnuity createAnnuityWithRider(AnnuityType annuityType) 
	throws EntityAlreadyExistsException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException , RemoteException{
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), annuityType);
		IRider rider1 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());		
		IRider rider2 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		IRider rider3 = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		rider3.setType(RiderType.REPLACE);
		annuity.getRiders().add(rider1);
		annuity.getRiders().add(rider2);	
		annuity.getRiders().add(rider3);
		annuity.setConfiguration(getConfiguration());
		annuity = getServerAdapter().createAnnuity(annuity);
		return annuity;
	}


	private void setScenarioVariables() {		
		logger = getLogger(getClass().getName());
		
		try {
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		} catch (InvalidExecutionUnitParameterException e1) {
			// the enable attribute is missing, or invalid - no big deal, set to true as default
			// and issue warning
			logger.warning("Missing or invalid value for: " + ENABLE_VERIFY_KEY + 
					" parameter.  for scenario: " + getDescription() + "Defaulting to true." +
					" current value is: " + getConfiguration().getParameterValue(ENABLE_VERIFY_KEY));
			enableVerify = true;
		}
	}
	
			
	private void verifyAnnuityValues(IAnnuity annuity, AnnuityType annuityType) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, 
	ServerInternalErrorException, ExecutionUnitVerificationException , RemoteException {
		IAnnuity results = null;
		if (AnnuityType.BASIC.equals(annuityType))  results = getAnnuityBeansFactory().createAnnuity();
		if (AnnuityType.EQUITY.equals(annuityType))  results = getAnnuityBeansFactory().createAnnEquity();
		if (AnnuityType.FIXED.equals(annuityType))  results = getAnnuityBeansFactory().createAnnFixed();
		results.setId(annuity.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findAnnuityById(results);
		if (annuity instanceof IFixedAnnuity){
			ExecutionUnitVerificationHelper.assertEqual(this, (IFixedAnnuity)annuity, (IFixedAnnuity)results,
				"Fixed Annuity from Client is not equal to DB value", "Mismacth was found.");
		}else if (annuity instanceof IEquityAnnuity){
			ExecutionUnitVerificationHelper.assertEqual(this, (IEquityAnnuity)annuity, (IEquityAnnuity)results,
					"Equity Annuity from Client is not equal to DB value", "Mismacth was found.");			
		}else{
			ExecutionUnitVerificationHelper.assertEqual(this, annuity, results,
					"Basic Annuity from Client is not equal to DB value", "Mismacth was found.");			
		}
	
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayouts(), results.getPayouts(), 
				"Annuity payouts from Client is not equal to DB value", "Mismacth was found in number of payouts");		
		boolean found = false;
		if (annuity.getPayouts() != null) {
			IPayout clientPayout = null;
			for (int i=0; i<annuity.getPayouts().size(); i++) {		
				found = false;  // reset
				clientPayout = annuity.getPayouts().get(i);
				for (IPayout resultPayout: results.getPayouts()) {
					if (clientPayout.getId().equals(resultPayout.getId())){
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this, clientPayout, resultPayout, 
							"Annuity Payout from Client is not equal to DB value at location: " +i , "Mismacth was found");
					}else{
						continue;
					}
				}
				if (!(found) && clientPayout != null) {
					throw new ExecutionUnitVerificationException("Annuity Payout from client is not equal to DB.  " +
							"Found Payout with id: " + clientPayout.getId() + 
							" on the client side, but not in the database for annuity id:" + annuity.getId());
					
				}
			}
		}
		
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getRiders(), results.getRiders(), 
				"Annuity rider from Client is not equal to DB value", "Mismacth was found in number of rider");		
		if (annuity.getRiders() != null) {
			IRider clientRider = null;
			for (int i=0; i<annuity.getRiders().size(); i++) {		
				found = false;  // reset
				clientRider = annuity.getRiders().get(i);
				for (IRider resultRider : results.getRiders()) {
					if (clientRider.getId().equals(resultRider.getId())){
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this, clientRider, resultRider, 
							"Annuity rider from Client is not equal to DB value at location: " +i , "Mismacth was found");
					}else{
						continue;
					}
				}
				if (!(found) && clientRider != null) {
					throw new ExecutionUnitVerificationException("Annuity rider from client is not equal to DB.  " +
							"Found rider with id: " + clientRider.getId() + 
							" on the client side, but not in the database for annuity id:" + annuity.getId());
					
				}
			}
		}
		
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayors(), results.getPayors(), 
				"Annuity Payor from Client is not equal to DB value", "Mismacth was found.");
		if (annuity.getPayors() != null) {
			IPayor clientPayor = null;
			for (int i=0; i<annuity.getPayors().size(); i++) {		
				found = false;  // reset
				clientPayor = annuity.getPayors().get(i);
				for (IPayor resultPayor : results.getPayors()) {
					if (clientPayor.getId().equals(resultPayor.getId())){
						found = true;
						ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayors().get(i), resultPayor, 
							"Annuity payor from Client is not equal to DB value at location: " +i , "Mismacth was found");
					}else{
						continue;
					}
				}
				if (!(found) && clientPayor != null) {
					throw new ExecutionUnitVerificationException("Annuity payor from client is not equal to DB.  " +
							"Found payor with id: " + clientPayor.getId() + 
							" on the client side, but not in the database for annuity id:" + annuity.getId());
					
				}
			}
		}
	}
	
	private void verifyPayorValues(IPayor payor) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, 
	ServerInternalErrorException, ExecutionUnitVerificationException , RemoteException{
		IPayor results = getAnnuityBeansFactory().createPayor();
		results.setId(payor.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPayorById(payor);
		ExecutionUnitVerificationHelper.assertEqual(this, payor, results,
				"Payor from Client is not equal to DB value", "Mismacth was found.");
	}

	private IPayor createPayor() 
	throws EntityAlreadyExistsException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException , RemoteException{
		IPayor payor = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		payor.setConfiguration(getConfiguration());
		payor = getServerAdapter().createPayor(payor);
		return payor;
		
	}

	private void verifyAnnuityHolderValues(IAnnuityHolder annuityHolder) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, 
	ServerInternalErrorException, ExecutionUnitVerificationException , RemoteException{
		IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
		results.setId(annuityHolder.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findHolderById(results);		
		ExecutionUnitVerificationHelper.assertEqual(this,annuityHolder, results, 
			"Annuity Holder from Client is not equal to DB value", "Mismacth was found.");
		ExecutionUnitVerificationHelper.assertEqual(this, annuityHolder.getContact(), results.getContact(), 
				"Annuity Holder Contact from Client is not equal to DB value", "Mismacth was found.");
	}
	
	private IAnnuityHolder createAnnuityHolder(IContact contact) 
	throws EntityAlreadyExistsException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException , RemoteException{
		IAnnuityHolder annuityHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
		annuityHolder.setCategory(getRandomEnum(AnnuityHolderCategory.class));
		annuityHolder.setContact(contact);
		annuityHolder.setConfiguration(getConfiguration());
		annuityHolder = getServerAdapter().createAnnuityHolder(annuityHolder);
		return annuityHolder;
	}

	private void verifyContactValues(IContact contact) 
		throws EntityNotFoundException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException , RemoteException{
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this,contact, results, 
			"contact from Client is not equal to DB value", "Mismacth was found.");		
		
	}

	private IContact createContact() 
		throws EntityAlreadyExistsException, InvalidArgumentException, 
		ServerAdapterCommunicationException, ServerInternalErrorException , RemoteException{
		IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());		
		contact.setConfiguration(getConfiguration());
		contact.setContactType(getRandomEnum(ContactType.class));
		contact = getServerAdapter().createContact(contact);
		return contact;
	}

}
