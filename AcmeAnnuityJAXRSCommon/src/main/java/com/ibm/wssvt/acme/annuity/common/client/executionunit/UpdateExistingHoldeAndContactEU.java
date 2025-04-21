package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionMapper;
import com.ibm.wssvt.acme.annuity.common.util.ServerExceptionType;
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

public class UpdateExistingHoldeAndContactEU extends AbastractAnnuityExecutionUnit {

	private static final long serialVersionUID = -8978758038714550844L;

	private static final String START_HOLDER_ID = "startHolderId";

	private static final String END_HOLDER_ID = "endHolderId";

	private int startHolderId;

	private int endHolderId;

	private int holderRange; // 0 to end-start
	
	private int overRideIdint = 0;
	
	private boolean enableVerify = true;
	
	private int maximumThreshold = 2;
	
	private int retryThinkTime = 5000;
	private int threshold = 0;	

	private AcmeLogger logger;

	public void execute() {
		
		try {
			setScenarioVariables();
		} catch (Exception e) {
			logger.warning("Invalid scenario parameters for scenario description:"
							+ getDescription() + "  Error is: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		String randomHolderId = ""
				+ (startHolderId + getRandomInteger(0, holderRange));
		logger.fine("Got the random holder ID - " + randomHolderId);
		logger.fine("Find the holder from existing database");
		IAnnuityHolder holder = null;
		try {
			holder = getAnnuityBeansFactory().createAnnuityHolder();
			holder.setId(randomHolderId);
			holder.setConfiguration(getConfiguration());
			holder = getServerAdapter().findHolderById(holder);
			logger.fine("holder category - " + holder.getCategory());
			logger.fine("holder first name - " + holder.getFirstName());
			logger.fine("holder last name - " + holder.getLastName());
		} catch (Exception e) {
			logger.warning("Failed to find holder. ID - " + randomHolderId
					+ " Error: " + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (enableVerify) {
			logger.fine("Verifying annuity holder from db: "+randomHolderId);
			try {
				verifyAnnuityHolderValue(holder);
			} catch (Exception e) {
				logger.severe("Find AnnuityHolder verification failed.  Error: " + e);
				getExecutionUnitEvent().addException(e);
			}
		}

		logger.fine("Getting Contact from AnnuityHolder:"+randomHolderId);
		IContact contact = null; 
		try {
			contact = holder.getContact();
			if(contact == null)
			{
				logger.severe("Failed to read holder Contact. Contact = null. HolderId: "+randomHolderId);
				return;
			}
		} catch (Exception e) {
			logger.severe("Failed to get Contact. Error: "+randomHolderId + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (enableVerify) {
			logger.fine("Verifying Create Contact:"+randomHolderId);
			try {
				verifyContactValue(contact, holder);
			} catch (Exception e) {
				logger.severe("Create Contact verification failed.  Error: "+randomHolderId + e);
				getExecutionUnitEvent().addException(e);
			}
		}

		logger.fine("Updating Contact:"+randomHolderId);
		IContact updatedContact = null;

		try {
			updatedContact = updateContact(contact, holder);
			if( updatedContact == null)
			{
				logger.severe("Failed to update Contact. HolderId: "+randomHolderId);
				return;
			}
		} catch (Exception e) {
			logger.severe("Failed to update Contact. Error: "+randomHolderId + e);
			getExecutionUnitEvent().addException(e);
			return;
		}
				
		if (enableVerify) {
			logger.fine("Verifying Update Contact:"+updatedContact.getId());
			try {
				verifyContactValue(updatedContact,holder);
			} catch (Exception e) {
				logger.severe("Update Contact verification failed. Error: " + e);
				getExecutionUnitEvent().addException(e);
			}
		}

		logger.fine("Updating AnnuityHolder:"+randomHolderId);
		IAnnuityHolder updatedHolder = null;
		try {
			updatedHolder = updateAnnuityHolder(holder, updatedContact);
		} catch (Exception e) {
			logger.severe("Failed to update AnnuityHolder. Error: "+randomHolderId + e);
			getExecutionUnitEvent().addException(e);
			return;
		}

		if (enableVerify) {
			logger.fine("Verifying Update AnnuityHolder:"+randomHolderId);
			try {
				verifyAnnuityHolderValue(updatedHolder);
			} catch (Exception e) {
				logger.severe("Update AnnuityHolder verification failed. Error: "+ e);
				getExecutionUnitEvent().addException(e);
			}
		}
	}

	private void setScenarioVariables()
			throws InvalidExecutionUnitParameterException {
		final String OVERRIDE_ID_KEY = "overRideId";
		final String MAX_THRESHOLD = "maximumThreshold";
		final String ENABLE_VERIFY_KEY = "enableVerify";
		final String RETRY_THINK_TIME="retryThinkTime";
		
		logger = getLogger(getClass().getName());
		// retrieve the parms from the config file
		startHolderId = getParameterValueInt(START_HOLDER_ID);
		endHolderId = getParameterValueInt(END_HOLDER_ID);
		try {
			maximumThreshold = getParameterValueInt(MAX_THRESHOLD);
		} catch(Exception e) {
			logger.warning("maximumThreshold parameter not specified, using default");
			maximumThreshold = 2;
		}
		try {
			retryThinkTime = getParameterValueInt(RETRY_THINK_TIME);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ RETRY_THINK_TIME+" is missing for scenario: " + getDescription()+" .Setting the default to 5 seconds");
			retryThinkTime = 5000;
		}
		try {
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		} catch(Exception e) {
			logger.warning(ENABLE_VERIFY_KEY +" parameter is not set, using default true.");
			enableVerify = true;
		}
		// optional, but must be 0 or more
		try {
			overRideIdint = getParameterValueInt(OVERRIDE_ID_KEY);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ OVERRIDE_ID_KEY+" is missing for scenario: " + getDescription()+" .Setting the default to zero");
			overRideIdint = 0;
		}
		if (startHolderId <= 0) {
			throw new InvalidExecutionUnitParameterException(START_HOLDER_ID
					+ " is less or equal to 0.  "
					+ "Invalid value for scenario with description: "
					+ getDescription() + ".  Value must be one or more.");
		}
		if (endHolderId <= 0) {
			throw new InvalidExecutionUnitParameterException(END_HOLDER_ID
					+ " is less or equal to 0.  "
					+ "Invalid value for scenario with description: "
					+ getDescription() + ".  Value must be one or more.");
		}
		// check for the over-ride id, if set, run the read only 1 time and exit
		// else read from startId -> startId+count
		if (overRideIdint > 0) {
			startHolderId = overRideIdint;
			holderRange = 0;
		} else {
		holderRange = endHolderId - startHolderId;
		}
	}

	private IAnnuityHolder updateAnnuityHolder(IAnnuityHolder before,
			IContact updatedContact) throws EntityNotFoundException, EntityAlreadyExistsException, 
			ServerAdapterCommunicationException, InvalidArgumentException,
			ServerInternalErrorException,ExecutionUnitVerificationException, RemoteException  {

		// Find this holder first
		IAnnuityHolder after = null;
		threshold = 0;
		while(threshold <= maximumThreshold ){	
			after = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
			after.setId(before.getId());
			after.setConfiguration(getConfiguration());
			after = getServerAdapter().findHolderById(after);

			// Update this holder with new information
			after.setCategory(getRandomEnum(AnnuityHolderCategory.class));
			Date dob = after.getDateOfBirth();
			Calendar newdob = Calendar.getInstance();
			newdob.setTime(dob);
			newdob.add(Calendar.MONTH, 1);
			after.setDateOfBirth(newdob.getTime());
			after.setLastName(getUpdatedString(after.getLastName()));
			try{
				after.setConfiguration(getConfiguration());
				after = getServerAdapter().updateAnnuityHolder(after);
				break;
			}catch(Exception e){
				threshold = recoverableExceptionCheck( e, threshold, after.getId());
				if( threshold == 0 ){
					logger.severe("Failed to update holder ID:"+before.getId());
					getExecutionUnitEvent().addException(e);
					throw  new ExecutionUnitVerificationException("updateAnnuityHolder: returning null after contact object");
				}


			}
		}
		if(after == null){
			throw  new ExecutionUnitVerificationException("updateAnnuityHolder: returning null after object");
		}
		return after;
	}

	private IContact updateContact(IContact before, IAnnuityHolder holder)
			throws EntityNotFoundException, EntityAlreadyExistsException,
			ServerAdapterCommunicationException, InvalidArgumentException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		threshold = 0;
		IContact after = null;
		while(threshold <= maximumThreshold ){		
			// Update the contact
			
			after = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			after.setId(before.getId());
			after.setConfiguration(getConfiguration());

			try{
				after = getServerAdapter().findContactById(after);	
			}catch(Exception e){
				logger.severe("Failed: HolderID:"+holder.getId()+" to find Contact ID:"+before.getId());
				getExecutionUnitEvent().addException(e);
			}

			after.setPhone(getUpdatedString(after.getPhone()));
			after.setEmail(getUpdatedString(after.getEmail()));

			try{
				after.setConfiguration(getConfiguration());
				after = getServerAdapter().updateContact(after);
				break;
			}catch(Exception e){
				threshold = recoverableExceptionCheck( e, threshold, after.getId());
				if( threshold == 0 ){   
					logger.severe("Failed: HolderID:"+holder.getId()+" to update Contact ID:"+before.getId());
					getExecutionUnitEvent().addException(e);
					throw  new ExecutionUnitVerificationException("updateContact: returning null after contact object");
				} 
				// re-read the contact?

			}
		}
		
		
		
		// Remove the contact
		// Before remove, we need to recreate this contact again
		threshold = 0;
		IContact newContact = null;
		IAnnuityHolder updatedHolder = null;
		while(threshold <= maximumThreshold ){
			newContact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
			newContact.setId(after.getId());

			// Remove contact from holder first
			updatedHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
			updatedHolder.setId(holder.getId());
			updatedHolder.setConfiguration(getConfiguration());
			updatedHolder = getServerAdapter().findHolderById(updatedHolder);
			updatedHolder.setContact(null);


			try{
				updatedHolder.setConfiguration(getConfiguration());
				updatedHolder = getServerAdapter().updateAnnuityHolder(updatedHolder);	
				break;
			}catch(Exception e){
				threshold = recoverableExceptionCheck( e, threshold, updatedHolder.getId());
				if( threshold == 0 ){  
					logger.severe("Failed: HolderID:"+holder.getId()+" to update Holder with new contact ID:"+after.getId());
					getExecutionUnitEvent().addException(e);
					throw  new ExecutionUnitVerificationException("updateContact: returning null after contact object");
				}
			}
		}
		
		// Now it is safe to remove contact
		threshold = 0;
		while(threshold <= maximumThreshold ){
			try{
				after.setConfiguration(getConfiguration());
				getServerAdapter().deleteContact(after);
				break;
			}catch(Exception e){
				threshold = recoverableExceptionCheck( e, threshold, after.getId());
				if( threshold == 0 ){  
					logger.severe("Failed: HolderID:"+holder.getId()+" to delete contact ID:"+after.getId());
					getExecutionUnitEvent().addException(e);
					throw  new ExecutionUnitVerificationException("updateContact: returning null after contact object");
				}
			}
		}
		
		// Create the contact with the same ID again and update it back to the same holder as well
		threshold = 0;
		while(threshold <= maximumThreshold ){
			newContact.setConfiguration(getConfiguration());
			after = getServerAdapter().createContact(newContact);
			updatedHolder.setContact(after);
			updatedHolder.setConfiguration(getConfiguration());

			try{
				updatedHolder = getServerAdapter().updateAnnuityHolder(updatedHolder);
				break;
			}catch(Exception e){
				threshold = recoverableExceptionCheck( e, threshold, after.getId());
				if( threshold == 0 ){  
					logger.severe("Failed: HolderID:"+holder.getId()+" to updte with new contact ID:"+after.getId());
					getExecutionUnitEvent().addException(e);
					throw  new ExecutionUnitVerificationException("updateContact: returning null after contact object");
				}
			}
		}
		
		if(after == null){
			throw  new ExecutionUnitVerificationException("updateContact: returning null after contact object");
		}
		return after;
	}
	
	private int recoverableExceptionCheck( Exception e, int threshold, String id){
		boolean tryAgain = false;
		ServerExceptionType exType = ServerExceptionMapper.getExceptionType(e);
		if (ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION.equals(exType)) {
			logger.info("OPTIMISTIC LOCKING EXCEPTION encountered while updating contact or holder id: " + id
					+".  Trying again to a max of: " + maximumThreshold + " times. This is attempt: " + (threshold +1) );
			tryAgain = true;
		}
		else if(ServerExceptionType.DEADLOCK_EXCEPTION.equals(exType)){
			logger.info("DEADLOCK EXCEPTION encountered while updating contact or holder id: " + id
					+".  Trying again to a max of: " + maximumThreshold + " times. This is attempt: " + (threshold +1) );
			tryAgain = true;
			}
		
		if( tryAgain){
			threshold++;
			if( threshold == maximumThreshold) threshold = 0;
			try{	
				Thread.sleep(retryThinkTime);
			} catch (InterruptedException eSleep) {			
				logger.severe("Thread Sleep has been interrupted"+ eSleep.toString());
				getExecutionUnitEvent().addException(eSleep);
			}
		}
		return threshold;
	}
	private void verifyAnnuityHolderValue(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, InvalidArgumentException,
			ServerAdapterCommunicationException, ServerInternalErrorException,
			ExecutionUnitVerificationException, RemoteException {
		IAnnuityHolder results = getAnnuityBeansFactory().createAnnuityHolder();
		results.setId(annuityHolder.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findHolderById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, annuityHolder, results,
				"AnnuityHolder from Client is not equal to DB value",
				"Mismacth was found.");
		ExecutionUnitVerificationHelper.assertEqual(this,
				annuityHolder.getContact(), results.getContact(),
				"AnnuityHolder Contact from Client is not equal to DB value",
				"Mismacth was found.");
	}

	private void verifyContactValue(IContact contact, IAnnuityHolder holder) throws EntityNotFoundException,
			InvalidArgumentException, ServerAdapterCommunicationException,
			ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		// read the contact with id.
		IContact results = getAnnuityBeansFactory().createContact();
		if(results == null){
			throw  new ExecutionUnitVerificationException("HolderId:"+holder.getId()+" Create contact getAnnuityBeansFactory().createContact() is null");
		}
		if(contact == null){
			throw  new ExecutionUnitVerificationException("HolderId:"+holder.getId()+" passed in contact object is null");
		}
		
		results.setId(contact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, contact, results,
				"HolderId:"+holder.getId()+" Contact from client is not equal to DB value",
				"Contact Create mismacth was found.");
	}

	private String getUpdatedString(String st) {
		String newName = "";
		String first = "";
		String last = "";
		if (st.indexOf("**") < 0) {
			newName = st + "**0";
		} else {
			StringTokenizer parser = new StringTokenizer(st, "**");		
			first = parser.nextToken();
			last = parser.nextToken();
			newName = first + "**" + (Integer.valueOf(last).intValue()+1);
		}		
		return newName;
	}
}
