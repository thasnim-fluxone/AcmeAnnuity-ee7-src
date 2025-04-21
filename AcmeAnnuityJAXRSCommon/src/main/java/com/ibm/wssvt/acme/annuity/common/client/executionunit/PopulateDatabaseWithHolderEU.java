/* 
  * $Rev: 817 $
  * $Date: 2007-09-25 09:29:59 -0500 (Tue, 25 Sep 2007) $
  * $Author: steven $
  * $LastChangedBy: steven $
  * Populate only Holders and Contact in the Annuity Database.
  * Will be required before populating the Policy Database.
*/
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.rmi.RemoteException;
import java.util.Date;
//import java.util.Random;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.ISingleThreadedExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
public class PopulateDatabaseWithHolderEU extends AbastractAnnuityExecutionUnit implements ISingleThreadedExecutionUnit, ISingleRunExecutionUnit{
	
	private static final long serialVersionUID = -5421478878181609584L;
	private int startId;
	private boolean useCharPrefixID = true;
	private int numberOfHolderEntries;

	boolean enableVerify = true; // so that we can default to true
	boolean randomizeSelection = true;
	

		
//	private Random populateRandom = new Random();

	private AcmeLogger logger =null;
	public void execute() {		
		logger = getLogger(getClass().getName());		
		try {
			setAndVerifyScenarioParams();
		} catch (Exception e) {
			logger.warning("Invalid scenario parameters for scenario description:" + getDescription() +
					"  Error is: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}			
							
		logger.info("Starting id=" + startId + " Count=" + numberOfHolderEntries);						
		IAnnuityHolder annuityHolder = null;
		IContact contact = null;
		String id;
		//String hid;
		Long startTime = System.currentTimeMillis();
		
		for (int i = startId; i < startId + numberOfHolderEntries; i++) {
			//id = hid = "" + i;
			id = "" + i;
			// output performance info every 1000 holders - changed to 10
			if( (i%10)== 0 && i != startId){
				logger.info("10 holders+children created in:"+(System.currentTimeMillis()-startTime)+" ms. "+ (1000000/(System.currentTimeMillis()-startTime))+" Holders/sec."+ (i-startId)+" holders created." );
				startTime = System.currentTimeMillis();
			}
			try{							
				contact = getContact(id);
				contact.setConfiguration(getConfiguration());
				contact = getServerAdapter().createContact(contact);
			}catch (Exception e) {
				logger.warning("Failed to create contact. Error is: " +e);
				getExecutionUnitEvent().addException(e);
				return;
			}

			try {
				if (enableVerify){ 
					verifyContact(contact);
				}
			}catch (Exception e) {
				logger.warning("Failed to verify create contact. Error is: " +e);
				getExecutionUnitEvent().addException(e);			
			}			
			try {
				annuityHolder = getAnnuityHolder(id);
				annuityHolder.setContact(contact);
				annuityHolder.setConfiguration(getConfiguration());
				logger.fine("ID:"+id+" Saving annuityHolder");
				annuityHolder = getServerAdapter().createAnnuityHolder(annuityHolder);				
			} catch (Exception e) {
				logger.warning("Failed to create AnnuityHolder. Error is: " +e);
				getExecutionUnitEvent().addException(e);
				return;
			}
			try {
				if (enableVerify){ 
					verifyHolder(annuityHolder);
				}	
			} catch (Exception e) {
				logger.warning("Failed to verify create AnnuityHolder. Error is: " +e);
				getExecutionUnitEvent().addException(e);				
			}
						
		}		
	}

	
	private void setAndVerifyScenarioParams() throws InvalidExecutionUnitParameterException {
		final String START_ID_KEY = "startId";
		final String NUMBER_OF_HOLDER_ENTRIES_KEY = "numberOfHolders";	
		final String ENABLE_VERIFY_KEY = "enableVerify";
		final String RANDOMIZE_SELECTION_KEY = "randomizeSelection";
		final String USE_ID_CHAR_PREFIX = "useIdCharPrefix";
		// retrieve the parms from the config file
		startId = getParameterValueInt(START_ID_KEY);
		numberOfHolderEntries = getParameterValueInt(NUMBER_OF_HOLDER_ENTRIES_KEY);
			
		if (startId <=0){
			throw new InvalidExecutionUnitParameterException (START_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (numberOfHolderEntries <=0){
			throw new InvalidExecutionUnitParameterException (NUMBER_OF_HOLDER_ENTRIES_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}

		try{
			useCharPrefixID = getParameterValueBoolean(USE_ID_CHAR_PREFIX);
		}catch (InvalidExecutionUnitParameterException e) {
			logger.warning("the attribute: " + USE_ID_CHAR_PREFIX + " is missing for scenario: " + getDescription() +
					".  Setting the default to true.");
			useCharPrefixID = true;
		}
		
		try{
			enableVerify = getParameterValueBoolean(ENABLE_VERIFY_KEY);
		}catch (InvalidExecutionUnitParameterException e) {
			logger.warning("the attribute: " + ENABLE_VERIFY_KEY + " is missing for scenario: " + getDescription() +
					".  Setting the default to true.");
			enableVerify = true;
		}
		
		try{
			randomizeSelection = getParameterValueBoolean(RANDOMIZE_SELECTION_KEY);
		}catch (InvalidExecutionUnitParameterException e) {
			logger.warning("the attribute: " + RANDOMIZE_SELECTION_KEY + " is missing for scenario: " + getDescription() +
					".  Setting the default to true.");
			randomizeSelection = true;
		}		
	}

	private void verifyHolder(IAnnuityHolder annuityHolder) throws EntityNotFoundException, 
	InvalidArgumentException,ServerAdapterCommunicationException, ServerInternalErrorException,
	ExecutionUnitVerificationException, RemoteException{
		IAnnuityHolder Holdresult = getAnnuityBeansFactory().createAnnuityHolder();
		Holdresult.setId(annuityHolder.getId());
		Holdresult.setConfiguration(getConfiguration());
		Holdresult = getServerAdapter().findHolderById(Holdresult);		
		ExecutionUnitVerificationHelper.assertEqual(this,annuityHolder, Holdresult, 
			"Annuity Holder from Client is not equal to DB value", "Mismatch was found.");
		ExecutionUnitVerificationHelper.assertEqual(this, annuityHolder.getContact(), Holdresult.getContact(), 
				"Annuity Holder Contact from Client is not equal to DB value", "Mismatch was found."); 
		
	}

	private void verifyContact(IContact contact)throws EntityNotFoundException, InvalidArgumentException,
	ServerAdapterCommunicationException,ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException{
		IContact conresult = getAnnuityBeansFactory().createContact();
		conresult.setId(contact.getId());
		conresult.setConfiguration(getConfiguration());
		conresult = getServerAdapter().findContactById(conresult);	
		ExecutionUnitVerificationHelper.assertEqual(this, contact, conresult,
				"Contact from Client is not equal to DB value", "Mismatch was found.");	
		}

	private IContact getContact(String sContactId) {
		IContact contact = BasicExecutionUnitLibrarry.getContact(getAnnuityBeansFactory());
		if(useCharPrefixID) contact.setId("C"+ sContactId);
		else contact.setId(sContactId);
		contact.setEmail(sContactId+"@domain.com");
		contact.setPhone(sContactId+"-"+sContactId+"-"+sContactId);
		contact.setContactType(getRandomEnum(ContactType.class));
		
		IAddress address = BasicExecutionUnitLibrarry.getAddress(getAnnuityBeansFactory());
		address.setLine1(sContactId+" Main Street");
		address.setLine2("APT # "+sContactId);
		address.setCity("Austin");
		address.setState("Texas");
		address.setZipCode("78758");
		address.setCountry("USA");
		contact.setAddress(address);
		return contact;
	}

	private IAnnuityHolder getAnnuityHolder(String  sHolderId) {
		IAnnuityHolder annuityHolder = BasicExecutionUnitLibrarry.getAnnuityHolder(getAnnuityBeansFactory());
		annuityHolder.setId(sHolderId);
		annuityHolder.setDateOfBirth(new Date());
		annuityHolder.setFirstName(sHolderId+" FirstName");
		annuityHolder.setLastName(sHolderId+" LastName");
		annuityHolder.setGovernmentId(sHolderId+"-111-22-3333");
		annuityHolder.setLastUpdateDate(new Date());
		annuityHolder.setTimeOfBirth(new Date());
		annuityHolder.setCategory(getRandomEnum(AnnuityHolderCategory.class));
		return annuityHolder;
	}

	
}