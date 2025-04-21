/* 
  * $Rev: 817 $
  * $Date: 2009-08-21 09:29:59 $
  * $Author: rhaque $
  * $LastChangedBy: rhaque $
  * Need to run the populateHoldersEU in the Annuity Database before running this.
  * Make sure the holderID start - ie. startId and numberOfHolderEntries is the same
  * for this EU and the populateHoldersEU.
  * This will populate the Policy Database with Policies, Funds, Beneficiaries - and Benecontact.
*/
package com.ibm.wssvt.acme.annuity.common.client.executionunit.policy;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.BeneContactId;
import com.ibm.wssvt.acme.annuity.common.bean.jpa.JPABeansFactory;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.BasicExecutionUnitLibrarry;
import com.ibm.wssvt.acme.annuity.common.client.executionunit.ExecutionUnitVerificationHelper;
import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.ISingleRunExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.ISingleThreadedExecutionUnit;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
public class PopulatePolicyDatabaseEU extends AbastractPolicyExecutionUnit implements ISingleThreadedExecutionUnit, ISingleRunExecutionUnit{
	
	private static final long serialVersionUID = -5421478878181609584L;
	private int startId;
	private boolean useCharPrefixID = true;
	private int numberOfHolderEntries;
	private int maxNumberOfPoliciesPerHolder;
	private int maxNumberOfFundsPerPolicy;
	private int maxNumberOfBeneficiariesPerHolder;
	private int maxNumberOfBeneContactsPerBeneficiary;
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
							
		logger.info("Starting id=" + startId + " Count=" + numberOfHolderEntries);						
		//IAnnuityHolder annuityHolder = null;
		//IContact contact = null;
		String id;
		String hid;
		Long startTime = System.currentTimeMillis();
		
		for (int i = startId; i < startId + numberOfHolderEntries; i++) {
			id = hid = "" + i;

			// output performance info every 1000 holders - changed to 10
			if( (i%10)== 0 && i != startId){
				logger.info("10 holders+children created in:"+(System.currentTimeMillis()-startTime)+" ms. "+ (1000000/(System.currentTimeMillis()-startTime))+" Holders/sec."+ (i-startId)+" holders created." );
				startTime = System.currentTimeMillis();
			}
		
			int numberOfPoliciesForThisHolder = 
				randomizeSelection ? 
					getRandomInteger(1, maxNumberOfPoliciesPerHolder) : maxNumberOfPoliciesPerHolder;
			for (int j =1; j <= numberOfPoliciesForThisHolder; j++) {
				IPolicy policy;
				Map<Integer,IFund> fundsMap = new HashMap <Integer, IFund>();
				IFund fund;			
				id = hid + "-" + j;
				policy = getPolicy(id);
				// set the holder id
				policy.setAnnuityHolderId(hid);
				policy.setFunds(fundsMap);
				//policy.setAnnuityHolderId(annuityHolder.getId());
				int numberOfFundsForThisPolicy  = 
					randomizeSelection ? 
						getRandomInteger(1, maxNumberOfFundsPerPolicy) : maxNumberOfFundsPerPolicy;
				for (int k =1; k <= numberOfFundsForThisPolicy; k++) {
					String fundId = hid + "-" + j + "-" + k; 
					fund = getFund(fundId);
					fundsMap = policy.getFunds();
					fundsMap.put(new Integer(k), fund);
					policy.setFunds(fundsMap);					//annuity.getRiders().add(rider);
					}
				
				
				try{
					policy.setConfiguration(getConfiguration());
					policy = getServerAdapter().createPolicy(policy);
				}catch (Exception e) {
					logger.warning("Failed to create Policy. Error is: " +e);
					getExecutionUnitEvent().addException(e);				
					return;
				}
				
				try {
					if (enableVerify) {
						verifyPolicy(policy);
					}
				} catch (Exception e) {
					logger.warning("Failed to verify create policy. Error is: " +e);
					getExecutionUnitEvent().addException(e);								
				}
				
			}
			
			int numberOfBeneficiariesForThisHolder =
				randomizeSelection ?
						getRandomInteger(1, maxNumberOfBeneficiariesPerHolder) : maxNumberOfBeneficiariesPerHolder;
			for (int j = 1; j <= numberOfBeneficiariesForThisHolder; j++) {
				id = hid + "-" + j;
				IBeneficiary beneficiary;
				List<IBeneContact> beneContacts = new ArrayList<IBeneContact>();
				beneficiary = getBeneficiary(id);
				beneficiary.setAnnuityHolderId(hid);
				beneficiary.setBeneContacts(beneContacts);
				try{
					beneficiary.setConfiguration(getConfiguration());
					beneficiary = getServerAdapter().createBeneficiary(beneficiary);
				}catch (Exception e) {
					logger.warning("Failed to create Beneficiary. Error is: " +e);
					getExecutionUnitEvent().addException(e);				
					return;
				}
				
				try {
					if (enableVerify) {
						verifyBeneficiary(beneficiary);
					}
				} catch (Exception e) {
					logger.warning("Failed to verify create beneficiary. Error is: " +e);
					getExecutionUnitEvent().addException(e);								
				}
				int numberOfBeneContactsForThisBeneficiary =
					randomizeSelection ?
							getRandomInteger(1, maxNumberOfBeneContactsPerBeneficiary) : maxNumberOfBeneContactsPerBeneficiary;
				for (int k = 0; k < numberOfBeneContactsForThisBeneficiary; k++) {
					IBeneContact beneContact;
					BeneContactId bcid = new BeneContactId();
					bcid.setBeneficiaryPK(beneficiary.getId());
					bcid.setContactType((ContactType.values()[k]));
					beneContact = getBeneContact(bcid);
					//Rumana added 02/2015 for JPA21 for defect 147548
					beneContact.setBeneficiary(beneficiary);
					try{
						beneContact.setConfiguration(getConfiguration());
						beneContact = getServerAdapter().createBeneContact(beneContact);
					}catch (Exception e) {
						logger.warning("Failed to create Beneficiary contact. Error is: " +e);
						getExecutionUnitEvent().addException(e);				
						return;
					}
					
					try {
						if (enableVerify) {
							verifyBeneContact(beneContact);
						}
					} catch (Exception e) {
						logger.warning("Failed to verify create beneficiary contact. Error is: " +e);
						getExecutionUnitEvent().addException(e);								
					}
				}
			}
			
			
		}		
	}

	private void setAndVerifyScenarioParams() throws InvalidExecutionUnitParameterException {
		final String START_ID_KEY = "startId";
		final String NUMBER_OF_HOLDER_ENTRIES_KEY = "numberOfHolders";	
		final String MAX_NUMBER_OF_POLICIES_PER_HOLDER_KEY = "maxNumberOfPoliciesPerHolder";
		final String MAX_NUMBER_OF_FUNDS_PER_POLICY_KEY = "maxNumberOfFundsPerPolicy";
		final String MAX_NUMBER_OF_BENEFICIARIES_PER_HOLDER_KEY = "maxNumberOfBeneficiariesPerHolder";
		final String MAX_NUMBER_OF_BENE_CONTACTS_PER_BENEFICIARY_KEY = "maxNumberOfBeneContactsPerBeneficiary";
		
		final String ENABLE_VERIFY_KEY = "enableVerify";
		final String RANDOMIZE_SELECTION_KEY = "randomizeSelection";
		final String USE_ID_CHAR_PREFIX = "useIdCharPrefix";
		// retrieve the params from the config file
		startId = getParameterValueInt(START_ID_KEY);
		numberOfHolderEntries = getParameterValueInt(NUMBER_OF_HOLDER_ENTRIES_KEY);
		maxNumberOfPoliciesPerHolder = getParameterValueInt(MAX_NUMBER_OF_POLICIES_PER_HOLDER_KEY);
		maxNumberOfFundsPerPolicy = getParameterValueInt(MAX_NUMBER_OF_FUNDS_PER_POLICY_KEY);
		maxNumberOfBeneficiariesPerHolder = getParameterValueInt(MAX_NUMBER_OF_BENEFICIARIES_PER_HOLDER_KEY);
		maxNumberOfBeneContactsPerBeneficiary = getParameterValueInt(MAX_NUMBER_OF_BENE_CONTACTS_PER_BENEFICIARY_KEY);
				
		if (startId <=0){
			throw new InvalidExecutionUnitParameterException (START_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (numberOfHolderEntries <=0){
			throw new InvalidExecutionUnitParameterException (NUMBER_OF_HOLDER_ENTRIES_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfPoliciesPerHolder <1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_POLICIES_PER_HOLDER_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfFundsPerPolicy <1){
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_FUNDS_PER_POLICY_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfBeneficiariesPerHolder <1) {
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_BENEFICIARIES_PER_HOLDER_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (maxNumberOfBeneContactsPerBeneficiary <1) {
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_BENE_CONTACTS_PER_BENEFICIARY_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be in range of 1-3.");
		}
		if (maxNumberOfBeneContactsPerBeneficiary >3) {
			throw new InvalidExecutionUnitParameterException (MAX_NUMBER_OF_BENE_CONTACTS_PER_BENEFICIARY_KEY + " is greater than 3.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be in range of 1-3.");
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
	
	
	
	private void verifyPolicy(IPolicy policy)throws EntityNotFoundException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException, 
	ExecutionUnitVerificationException , RemoteException
	{
		// read the policy with id.
		IPolicy results = ((JPABeansFactory) getAnnuityBeansFactory()).createPolicy();
		results.setId(policy.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findPolicyById(results);
		ExecutionUnitVerificationHelper.assertEqual(this,policy, results, 
			"policy from Client is not equal to DB value", "Mismacth was found.");		
	}
	
	private void verifyBeneficiary(IBeneficiary beneficiary)throws EntityNotFoundException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException, 
	ExecutionUnitVerificationException , RemoteException
	{
		IBeneficiary results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneficiary();
		results.setId(beneficiary.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneficiaryById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneficiary, results,
			"beneficiary from Client is not equal to DB value", "Mismacth was found.");
	}
	
	private void verifyBeneContact(IBeneContact beneContact)throws EntityNotFoundException, InvalidArgumentException, 
	ServerAdapterCommunicationException, ServerInternalErrorException, 
	ExecutionUnitVerificationException , RemoteException
	{
		IBeneContact results = ((JPABeansFactory) getAnnuityBeansFactory()).createBeneContact();
		results.setId(beneContact.getId());
		results.setConfiguration(getConfiguration());
		results = getServerAdapter().findBeneContactById(results);
		ExecutionUnitVerificationHelper.assertEqual(this, beneContact, results,
			"beneficiary contact from Client is not equal to DB value", "Mismacth was found.");
	}

	private IPolicy getPolicy(String sPolicyId){
		IPolicy policy = BasicExecutionUnitLibrarry.getPolicy(getAnnuityBeansFactory());
		if(useCharPrefixID) policy.setId("P"+ sPolicyId);
			else policy.setId(sPolicyId);	
		return policy;
	}
	
	private IFund getFund(String sFundId){
		String[] states = {"TX","CA","NY","MN","NC","IL","VA"};
		
		IFund fund = BasicExecutionUnitLibrarry.getFund(getAnnuityBeansFactory());
		fund.setFundName(sFundId+ " Company");
		//Double indexRate = new Double(populateRandom.nextDouble() * 100);
		
		//Get random double - and change it to be 2 digit precision
		
		Double rand = new Double(populateRandom.nextDouble() * 10000);
		Double randInt = new Double(rand.intValue());
		Double indexRate = new Double(randInt/100);
		
		fund.setIndexRate(indexRate);
		IAddress faddress = fund.getAddress();
		faddress.setLine1(sFundId + "Main Street");
		faddress.setLine2("Apt # " + sFundId);
		Random r = new Random();
		faddress.setState(states[r.nextInt(states.length)]);
		fund.setAddress(faddress);
		
		return fund;
	}
	
	private IBeneficiary getBeneficiary(String sBeneficiaryId) {
		IBeneficiary beneficiary = BasicExecutionUnitLibrarry.getBeneficiary(getAnnuityBeansFactory());
		if(useCharPrefixID)
			beneficiary.setId("B"+sBeneficiaryId);
		else
			beneficiary.setId(sBeneficiaryId);
		//TODO: add meaningful data
		//first name
		//last name
		//relationship
		return beneficiary;
	}
	
	private IBeneContact getBeneContact(BeneContactId id) {
		String[] domains = {"us.ibm.com","ca.ibm.com","uk.ibm.com","in.ibm.com","cn.ibm.com","websphere.com","apache.org"};
		IBeneContact beneContact = BasicExecutionUnitLibrarry.getBeneContact(getAnnuityBeansFactory(), id.getBeneficiaryPK());
		beneContact.getId().setContactType(id.getContactType());
		Random r = new Random();
		beneContact.setPhone((100+ r.nextInt(900)) + "-" + (100 + r.nextInt(900)) + "-" + (1000 + r.nextInt(9000)));
		beneContact.setEmail("user"+r.nextInt()+"@"+domains[r.nextInt(domains.length)]);
		return beneContact;
	}
		
}