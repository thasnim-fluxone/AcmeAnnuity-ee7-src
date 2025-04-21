/* 
  * $Rev: 817 $
  * $Date: 2007-09-25 09:29:59 -0500 (Tue, 25 Sep 2007) $
  * $Author: steven $
  * $LastChangedBy: steven $
*/
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
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
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.ISingleThreadedExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
public class PopulateDatabaseEU extends AbastractAnnuityExecutionUnit implements ISingleThreadedExecutionUnit, ISingleRunExecutionUnit{
	
	private static final long serialVersionUID = -5421478878181609584L;
	private int startId;
	private boolean useCharPrefixID = true;
	private int numberOfHolderEntries;
	private int numberOfPayorEntries;
	private int maxNumberOfAnnuitiesPerHolder;
	private int maxNumberOfPayoutsPerAnnuity;
	private int maxNumberOfPayorsPerAnnuity;
	private int maxNumberOfRidersPerAnnuity;
	boolean enableVerify = true; // so that we can default to true
	boolean randomizeSelection = true;
	

		
	private Random populateRandom = new Random();

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
		
		logger.info("Create Payors:" + numberOfPayorEntries);
		List<IPayor> payorList = new ArrayList<IPayor>();
		try{			
			payorList = createPayors();
		}catch (Exception e) {
			logger.warning("Failed to create Payors. Error is: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}
		
		try {
			if (enableVerify) { 
				for (IPayor payor : payorList) {
					verifyPayor(payor);	
				}				
			}				
		} catch (Exception e) {
			logger.warning("Failed to verify create payor. Error is: " +e);
			getExecutionUnitEvent().addException(e);			
		}
							
		logger.info("Starting id=" + startId + " Count=" + numberOfHolderEntries);						
		IAnnuityHolder annuityHolder = null;
		IContact contact = null;
		String id;
		String hid;
		Long startTime = System.currentTimeMillis();
		
		for (int i = startId; i < startId + numberOfHolderEntries; i++) {
			id = hid = "" + i;
			// output performance info every 1000 holders
			if( (i%1000)== 0 && i != startId){
				logger.info("1000 holders+children created in:"+(System.currentTimeMillis()-startTime)+" ms. "+ (1000000/(System.currentTimeMillis()-startTime))+" Holders/sec."+ (i-startId)+" holders created." );
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
						
			int numberOfAnnuitiesForThisHolder = 
				randomizeSelection ? 
					getRandomInteger(1, maxNumberOfAnnuitiesPerHolder) : maxNumberOfAnnuitiesPerHolder;
			for (int j =1; j <= numberOfAnnuitiesForThisHolder; j++) {
				IAnnuity annuity;
				IRider rider;			
				id = hid + "-" + j;
				annuity = getAnnuity(id);
				// set the holder id
				annuity.setAnnuityHolderId(annuityHolder.getId());
				int numberOfRidersForThisAnnuity  = 
					randomizeSelection ? 
						getRandomInteger(1, maxNumberOfRidersPerAnnuity) : maxNumberOfRidersPerAnnuity;
				for (int k =1; k <= numberOfRidersForThisAnnuity; k++) {
					String riderId = hid + "-" + j + "-" + k; 
					rider = getRider(riderId);
					annuity.getRiders().add(rider);
				}
				
				IPayor randomPayor = null; 
				int numberOfPayorsForThisAnnuity = 
					randomizeSelection? getRandomInteger(1, maxNumberOfPayorsPerAnnuity): maxNumberOfPayorsPerAnnuity;												
				while (annuity.getPayors().size() < numberOfPayorsForThisAnnuity ){
					randomPayor = payorList.get(getRandomInteger(0, payorList.size()-1));
					boolean exists = false;
					for (IPayor payor: annuity.getPayors()){
						if (payor.getId().equals(randomPayor.getId())){
							exists = true;
							break;
						}
					}
					if (!exists) {
						annuity.getPayors().add(randomPayor);
					}				
				}
				
				try{
					annuity.setConfiguration(getConfiguration());
					annuity = getServerAdapter().createAnnuity(annuity);
				}catch (Exception e) {
					logger.warning("Failed to create Annuity. Error is: " +e);
					getExecutionUnitEvent().addException(e);				
					return;
				}
				// we will verify annuity after creating payouts
				int numberOfPayoutsForThisAnnuity = 
					randomizeSelection ? getRandomInteger(1, maxNumberOfPayoutsPerAnnuity): maxNumberOfPayoutsPerAnnuity;
				for (int k=1; k <= numberOfPayoutsForThisAnnuity; k++) {
					String payoutId = id + "-" + k;
					IPayout payout = getPayout(payoutId);
					payout.setAnnuity(annuity);					
					try {
						payout.setConfiguration(getConfiguration());
						payout = getServerAdapter().createPayout(payout);
						annuity.getPayouts().add(payout);   // needed for verification
					} catch (Exception e) {
						logger.warning("Failed to create payout. Error is: " +e);
						getExecutionUnitEvent().addException(e);				
						return;						
					}
				}
				
				try {
					if (enableVerify) {
						verifyAnnuity(annuity);
					}
				} catch (Exception e) {
					logger.warning("Failed to verify create annuity. Error is: " +e);
					getExecutionUnitEvent().addException(e);								
				}
				
			}
		}		
	}

	private List<IPayor> createPayors() 
	throws EntityAlreadyExistsException, InvalidArgumentException, ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, RemoteException {
		List<IPayor> payorList = new ArrayList<IPayor>();
		for (int i=startId; i < startId + numberOfPayorEntries; i++){
			IPayor payor = getPayor(Integer.toString(i));
			payor.setConfiguration(getConfiguration());				
			payor = getServerAdapter().createPayor(payor);	
			payorList.add(payor);		
		}	
		return payorList;
	}

	private void setAndVerifyScenarioParams() throws InvalidExecutionUnitParameterException {
		final String START_ID_KEY = "startId";
		final String NUMBER_OF_HOLDER_ENTRIES_KEY = "numberOfHolders";	
		final String NUMBER_OF_PAYOR_ENTRIES_KEY = "numberOfPayors";
		final String MAX_NUMBER_OF_PAYOUTS_PER_ANNUITY_KEY = "maxNumberOfPayoutsPerAnnuity";
		final String MAX_NUMBER_OF_PAYORS_PER_ANNUITY_KEY = "maxNumberOfPayorsPerAnnuity";
		final String MAX_NUMBER_OF_RIDERS_PER_ANNUITY_KEY = "maxNumberOfRidersPerAnnuity";
		final String MAX_NUMBER_OF_ANNUITIES_PER_HOLDER_KEY = "maxNumberOfAnnuitiesPerHolder";
		final String ENABLE_VERIFY_KEY = "enableVerify";
		final String RANDOMIZE_SELECTION_KEY = "randomizeSelection";
		final String USE_ID_CHAR_PREFIX = "useIdCharPrefix";
		// retrieve the parms from the config file
		startId = getParameterValueInt(START_ID_KEY);
		numberOfHolderEntries = getParameterValueInt(NUMBER_OF_HOLDER_ENTRIES_KEY);
		numberOfPayorEntries = getParameterValueInt(NUMBER_OF_PAYOR_ENTRIES_KEY);		
		maxNumberOfAnnuitiesPerHolder = getParameterValueInt(MAX_NUMBER_OF_ANNUITIES_PER_HOLDER_KEY);
		maxNumberOfPayoutsPerAnnuity  = getParameterValueInt(MAX_NUMBER_OF_PAYOUTS_PER_ANNUITY_KEY);
		maxNumberOfPayorsPerAnnuity   = getParameterValueInt(MAX_NUMBER_OF_PAYORS_PER_ANNUITY_KEY);
		maxNumberOfRidersPerAnnuity   = getParameterValueInt(MAX_NUMBER_OF_RIDERS_PER_ANNUITY_KEY);
				
		if (startId <=0){
			throw new InvalidExecutionUnitParameterException (START_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (numberOfHolderEntries <=0){
			throw new InvalidExecutionUnitParameterException (NUMBER_OF_HOLDER_ENTRIES_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (numberOfPayorEntries <=0){
			throw new InvalidExecutionUnitParameterException (NUMBER_OF_PAYOR_ENTRIES_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfAnnuitiesPerHolder <1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_ANNUITIES_PER_HOLDER_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfPayorsPerAnnuity <1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_PAYORS_PER_ANNUITY_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfPayoutsPerAnnuity <1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_PAYOUTS_PER_ANNUITY_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfRidersPerAnnuity<1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_RIDERS_PER_ANNUITY_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfPayorsPerAnnuity > numberOfPayorEntries) {
			throw new InvalidExecutionUnitParameterException ("The number of payors to create is less " +
				"than the max number of payors per annuity. This is invalid.  Scenario Description: " + getDescription()
				+ "Update the scenario params and make sure that max number of payors is LESS or EQUAL to number of payour entries.");
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

	private void verifyAnnuity(IAnnuity annuity) 
	throws EntityNotFoundException, InvalidArgumentException, ServerAdapterCommunicationException, 
	ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException {
		IAnnuity results = null;
		if (annuity instanceof IFixedAnnuity) {
			results = getAnnuityBeansFactory().createAnnFixed();
			results.setId(annuity.getId());
			results.setConfiguration(getConfiguration());
			results = getServerAdapter().findAnnuityById(results);
			ExecutionUnitVerificationHelper.assertEqual(this, (IFixedAnnuity) annuity, (IFixedAnnuity) results, 
					"Annuity from Client is not equal to DB value", "Mismatch was found.");
		}else if (annuity instanceof IEquityAnnuity){
			results = getAnnuityBeansFactory().createAnnEquity();
			results.setId(annuity.getId());
			results.setConfiguration(getConfiguration());
			results = getServerAdapter().findAnnuityById(results);
			ExecutionUnitVerificationHelper.assertEqual(this, (IEquityAnnuity) annuity, (IEquityAnnuity) results, 
					"Annuity from Client is not equal to DB value", "Mismatch was found.");
		}else {
			results = getAnnuityBeansFactory().createAnnuity();
			results.setId(annuity.getId());
			results.setConfiguration(getConfiguration());
			results = getServerAdapter().findAnnuityById(results);
			ExecutionUnitVerificationHelper.assertEqual(this,  annuity, results, 
					"Annuity from Client is not equal to DB value", "Mismatch was found.");
		}								
					
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayouts(), results.getPayouts(), 
				"Annuity payouts from Client is not equal to DB value", "Mismatch was found in number of payouts");
		boolean found = false;
		IPayout clientPayout = null;
		for (int i=0; i<annuity.getPayouts().size(); i++) {		
			found = false;  // reset
			clientPayout = annuity.getPayouts().get(i);
			for (IPayout resultPayout: results.getPayouts()) {
				if (clientPayout.getId().equals(resultPayout.getId())){
					found = true;
					ExecutionUnitVerificationHelper.assertEqual(this, clientPayout, resultPayout, 
						"Annuity Payout from Client is not equal to DB value at location: " +i , "Mismacth was found");
					// check that the reverse relationship is also OK.
					ExecutionUnitVerificationHelper.assertEqual(this, clientPayout.getAnnuity(), resultPayout.getAnnuity(), 
							"Annuity from payout from Client is not equal to DB value at location: " +i , "Mismatch was found");	
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
	
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getRiders(), results.getRiders(), 
				"Annuity rider from Client is not equal to DB value", "Mismatch was found in number of rider");				

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
		
		ExecutionUnitVerificationHelper.assertEqual(this, annuity.getPayors(), results.getPayors(), 
				"Annuity payor from Client is not equal to DB value", "Mismatch was found");
		ExecutionUnitVerificationHelper.assertNotNull(this, results.getPayors(), 				 
				"Annuity payors from from DB was null.", "invalid value");
		ExecutionUnitVerificationHelper.assertEquals(this, annuity.getPayors().size(), results.getPayors().size(), 				
				"Annuity payors size from Client is not equal to DB value", "Mismatch was found");
				
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

	private void verifyPayor(IPayor payor) throws EntityNotFoundException,InvalidArgumentException ,
	ServerAdapterCommunicationException, ServerInternalErrorException, ExecutionUnitVerificationException, RemoteException  {
		IPayor results = getAnnuityBeansFactory().createPayor();
		results.setId(payor.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPayorById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, payor, results,
				"Payor from Client is not equal to DB value", "Mismatch was found");
		
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

	private IPayor getPayor(String sPayorId) {
		IPayor payor = BasicExecutionUnitLibrarry.getPayor(getAnnuityBeansFactory());
		if(useCharPrefixID) payor.setId("P"+sPayorId);
		else payor.setId(sPayorId);
		payor.setName(sPayorId+ " DB Auto Populate");
		return payor;
	}

	
	private IRider getRider(String sRiderId) {
		IRider rider = BasicExecutionUnitLibrarry.getRider(getAnnuityBeansFactory());
		if(useCharPrefixID) rider.setId("R" + sRiderId);
		else rider.setId(sRiderId);
		rider.setRule(sRiderId+":"+rider.getRule());
		rider.setType(getRandomEnum(RiderType.class));
		return rider;
	}

	private  IPayout getPayout(String sPayoutId) {
		IPayout payout = BasicExecutionUnitLibrarry.getPayout(getAnnuityBeansFactory());
		if(useCharPrefixID)	payout.setId("PT" + sPayoutId);
		else payout.setId(sPayoutId);
		payout.setTaxableAmount(new BigDecimal(populateRandom.nextDouble()*1000));
		// cheat - the large ID's put the date too far forward and back
		int dateDeviation = populateRandom.nextInt(1000);
		Calendar endDate = payout.getEndDate();
		endDate.add(Calendar.DAY_OF_MONTH, dateDeviation);
		payout.setEndDate(endDate);
		Calendar startDate = payout.getStartDate();
		startDate.add(Calendar.DAY_OF_MONTH, -dateDeviation);
		payout.setStartDate(startDate);
		return payout;
	}

	private IAnnuity getAnnuity(String sAnnuityId) {
		
		IAnnuity annuity = BasicExecutionUnitLibrarry.getAnnuity(getAnnuityBeansFactory(), getRandomEnum(AnnuityType.class));
		if(useCharPrefixID) annuity.setId("A"+ sAnnuityId);
		else annuity.setId(sAnnuityId);
		annuity.setAccountNumber(sAnnuityId+"123456");
		annuity.setLastPaidAmt(new Double(populateRandom.nextDouble()));		
		annuity.setAmount(new Double(populateRandom.nextDouble()));
		annuity.setLastUpdateDate(new Date());
		
		return annuity;
	}
   
	
}