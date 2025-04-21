/* 
 * $Rev: 790 $
 * $Date: 2007-09-17 16:49:17 -0500 (Mon, 17 Sep 2007) $
 * $Author: jeff $
 * $LastChangedBy: jeff $
 */
package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class ReadAllSystemDataEU extends AbastractAnnuityExecutionUnit {

	/**
	 * This scenario requires that the Annuity database be pre-populated using
	 * the the project database population scenario There is a specific
	 * relationship where the holder id is the prefix for the annuityid, payout
	 * id and rider id
	 */
	private static final long serialVersionUID = -2363569260137693290L;
	private int startId = 0;
	private int idCount = 0;
	private int overRideIdint = 0;
	
	public void execute() {
		String sHolderId = null;
		IAnnuityHolder annuityHolder = null;
		IAnnuityHolder annuityHolderResults = null;
		AcmeLogger logger = getLogger(getClass().getName());

		try {
			setAndVerifyScenarioParams(logger);
		} catch (Exception e) {
			logger.warning("Invalid scenario parameters for scenario description:" + getDescription() +
					"  Error is: " +e);
			getExecutionUnitEvent().addException(e);
			return;
		}			


		// loop on the annuity holder id until all requested objects have been
		// read and validated
		for (int i = startId; i < startId + idCount; i++) {
			try {
				sHolderId = Integer.toString(i);
				// create an annuityHolder object
				annuityHolder = BasicExecutionUnitLibrarry
						.getAnnuityHolder(getAnnuityBeansFactory());
				// set the id
				annuityHolder.setId(sHolderId);
			} catch (Exception e) {
				logger.severe("Setting up holder ID info: " + sHolderId
						+ "Failed. Exception="+e);
				getExecutionUnitEvent().addException(e);
				// get out fatal error
				break;
			}

			try {
				// get the holder object
				logger.fine("Looking up holder ID: " + sHolderId);
				// set the execution config
				annuityHolder.setConfiguration(getConfiguration());
				// retrieve the annuityHolder + annuities + payors + riders + payouts
				annuityHolderResults = getServerAdapter().findHolderById(
						annuityHolder);
				logger.fine("Found holder ID: " + sHolderId);
			} catch (Exception e) {
				logger.severe("Looking for holder id: " + sHolderId
						+ " Failed. Exception="+e);
				getExecutionUnitEvent().addException(e);
				// get out fatal error
				break;
			}

			try {
				// verify annuity Holder id that the id retrived matches what we
				// requested
				ExecutionUnitVerificationHelper
						.assertEquals(this, sHolderId, annuityHolderResults
								.getId(), " ERROR: requested holder id: "
								+ sHolderId, " Retrieved holder id: "
								+ annuityHolderResults.getId());

				logger.fine("Validated holder ID: " + sHolderId);
			} catch (Exception e) {
				logger.severe("Validating holder ID: " + sHolderId + "Failed.");
				getExecutionUnitEvent().addException(e);
			}

			// validate the contact id
			try {
				// verify the contact id equals the sHolderId
				ExecutionUnitVerificationHelper.assertIdContains(this, 
						annuityHolderResults.getContact().getId(),
						sHolderId,
						" ERROR: requested contact id: " + sHolderId,
						" Retrieved contact id: "
								+ annuityHolderResults.getContact().getId());

				logger.fine("Validated contact ID: "
						+ annuityHolderResults.getContact().getId());
			} catch (Exception e) {
				logger
						.severe("Validating contact ID: " + sHolderId
								+ "Failed.");
				getExecutionUnitEvent().addException(e);
			}

			// validate the person info using the firstName field. It is
			// prefixed with the sHolderId
			try {
				logger.fine("Validating person firstName: "
						+ annuityHolderResults.getFirstName()
						+ " for Holder ID: " + sHolderId);
				ExecutionUnitVerificationHelper.assertIdContains(this,
						annuityHolderResults.getFirstName(), sHolderId,
						"ERROR: requested Holder FirstName: "
								+ annuityHolderResults.getFirstName(),
						" postMsg-does not contain holder id: " + sHolderId);
				logger.fine("Validated person firstName: "
						+ annuityHolder.getFirstName() + " for Holder ID: "
						+ sHolderId);
			} catch (Exception e) {
				logger.severe("Validating person Firstname: "
						+ annuityHolderResults.getFirstName()
						+ " for sHolderId: " + sHolderId + " Failed.");
				getExecutionUnitEvent().addException(e);
			}

			// get the list of annuities that the holder has if any
			List<IAnnuity> annuityListResults = null;
			try {
				logger.fine("Looking up annuities for holder ID: "
						+ annuityHolder.getId());
				annuityHolder.setConfiguration(getConfiguration());

				annuityListResults = getServerAdapter().findHolderAnnuities(
						annuityHolder);

				logger.fine("Looked up annuities for holder ID: "
						+ annuityHolder.getId());
			} catch (Exception e) {
				logger.severe("Error Holder ID: " + annuityHolder.getId()
						+ " failed finding annuities. Exception: " + e);
				getExecutionUnitEvent().addException(e);
			}

			if (annuityListResults != null) {
				// how many annuities / holder?
				logger.fine("HolderId: " + sHolderId + " has: "
						+ annuityListResults.size() + " annuities");

				// iterate through the annuity list
				Iterator<IAnnuity> it = annuityListResults.iterator();
				// validate the FK_HOLDER_ID in the anniuty ensuring it matches
				// the
				// requested holder id
				// the annuity id has the holder id prefixed. example holder id
				// = 20000
				// annuity = 20000-1
				while (it.hasNext()) {
					try {
						IAnnuity p = it.next();
						logger.fine("HolderId: " + p.getAnnuityHolderId()
								+ " Annuity ID: " + p.getId());
						ExecutionUnitVerificationHelper.assertIdContains(this, p
								.getAnnuityHolderId(), sHolderId,
								"ERROR: requested annuity id: "
										+ p.getAnnuityHolderId(),
								" does not contain holder id: " + sHolderId);
						// --------------------------------------------------------------------
						// check the riders
						Iterator<IRider> itRider = p.getRiders().iterator();
						logger.fine("HolderID: " + p.getAnnuityHolderId()
								+ " AnnuityID: " + p.getId() + " #riders: "
								+ p.getRiders().size());
						while (itRider.hasNext()) {
							IRider rider = itRider.next();
							logger.fine("Validating - HolderID: "
									+ p.getAnnuityHolderId() + " AnnuityID: "
									+ p.getId() + " rider: " + rider.getId());
							ExecutionUnitVerificationHelper
									.assertIdContains(this, rider.getId(),
											sHolderId,
											"ERROR: requested rider id: "
													+ rider.getId(),
											" does not contain holder id: "
													+ sHolderId);
						}
						// --------------------------------------------------------------------
						// check the payors
						Iterator<IPayor> itPayor = p.getPayors().iterator();
						logger.fine("HolderID: " + p.getAnnuityHolderId()
								+ " AnnuityID:" + p.getId() + " #payors: "
								+ p.getPayors().size());
						while (itPayor.hasNext()) {
							IPayor payor = itPayor.next();
							logger.fine("Validating - HolderID: "
									+ p.getAnnuityHolderId() + " AnnuityID: "
									+ p.getId() + " payor: " + payor.getId());
							// Since this test case did not create the
							// Holder/Annuity/Rider/Payout/Payor
							// relationships, it cannot validate the payor
							// relationship
						}
						// --------------------------------------------------------------------
						// check the payouts
						// the payout id is prefixed by the sHolderId
						Iterator<IPayout> itPayout = p.getPayouts().iterator();
						logger.fine("HolderID: " + p.getAnnuityHolderId()
								+ " AnnuityID:" + p.getId() + " #payouts: "
								+ p.getPayouts().size());
						//int index = 0;
						while (itPayout.hasNext()) {
							IPayout payout = itPayout.next();
							logger.fine("Validating - HolderID: "
									+ p.getAnnuityHolderId() + " AnnuityID: "
									+ p.getId() + " payout: " + payout.getId());
							
							verbosePayoutTrace( p, payout, logger );
							
							ExecutionUnitVerificationHelper
									.assertIdContains(this, payout.getId(),
											sHolderId,
											"ERROR: requested payout id: "
													+ payout.getId(),
											" does not contain holder id: "
													+ sHolderId);
						}
					} catch (Exception e) {
						logger.severe("validating holder ID: " + sHolderId
								+ " Failed for annuity: ");
						getExecutionUnitEvent().addException(e);
					}
				}

			} else {
				logger.fine("Holder ID: " + sHolderId + " has 0 annuities");
			}
		}
	} // end annuity holder lookup loop
	/*
	private void verboseHolderTrace( IAnnuityHolder holder, Logger logger) {
		logger.fine("holderID="+holder.getId()+
				" PayoutId="+holder.getId()+
				" holderFirstName="+holder.getFirstName()+
				" holderLastName="+ holder.getLastName()+
				" holderGovernmentId="+ holder.getGovernmentId()+
				" holderDateOfBirth="+holder.getDateOfBirth().toString()+
				" holder="+holder.getLastUpdateDate().toString()+
				" holder="+holder.getTimeOfBirth().toString());
	}
	*/
	private void verbosePayoutTrace( IAnnuity annuity, IPayout payout, AcmeLogger logger) {
		logger.fine("annuityID="+annuity.getId()+
				" PayoutId="+payout.getId()+
				" payoutTax="+payout.getTaxableAmount().toString()+
				" payoutEndDate="+ payout.getEndDate().toString()+
				" payoutStartDate="+ payout.getStartDate().toString()+
				" payoutLastupdateDate="+payout.getLastUpdateDate().toString());
	}
	/*
	private void verboseRiderTrace( IAnnuity annuity, IRider rider, Logger logger) {
		logger.fine("annuityID="+annuity.getId()+
				" RiderId="+rider.getId()+
				" riderRule="+rider.getRule()+
				" riderEffectiveDate="+ rider.getEffectiveDate().toString()+
				" riderLastUpdateDate="+ rider.getLastUpdateDate().toString()+
				" riderType="+rider.getType().toString());
	}
	
	private void verbosePayorTrace( IAnnuity annuity, IPayor payor, Logger logger) {
		logger.fine("annuityID="+annuity.getId()+
				" PayorId="+payor.getId()+
				" payorName="+payor.getName()+
				" payorLastUpdateDate="+ payor.getLastUpdateDate().toString());
	}
	*/
	private void setAndVerifyScenarioParams(AcmeLogger logger) throws InvalidExecutionUnitParameterException {
		final String START_ID_KEY = "startId";
		final String ID_COUNT_KEY = "idCount";
		final String OVERRIDE_ID_KEY = "overRideId";
		// if randomRead var is defined and not 0 then we will read a single intstance of the holder and all its children
		final String RANDOM_READ_KEY = "randomRead";
		boolean randomRead = false;
		// retrieve the parms from the config file
		startId = getParameterValueInt(START_ID_KEY);
		idCount = getParameterValueInt(ID_COUNT_KEY);
		
		// if randomRead is true, it takes precedence over the override id
		try{
			randomRead = getParameterValueBoolean(RANDOM_READ_KEY);
		}catch (InvalidExecutionUnitParameterException e) {
			logger.warning("the attribute: " + RANDOM_READ_KEY + " is missing for scenario: " + getDescription() +
					".  Setting the default to false.");
			randomRead = false;
		}
		
		// optional, but must be 0 or more
		try {
			overRideIdint = getParameterValueInt(OVERRIDE_ID_KEY);
		} catch(Exception e){
//			 if parm not there just continue
			logger.warning( "the attribute:"+ OVERRIDE_ID_KEY+" is missing for scenario: " + getDescription()+" .Setting the default to zero");
			overRideIdint = 0;
		}
		

		
		if (startId <=0){
			throw new InvalidExecutionUnitParameterException (START_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (idCount <=0){
			throw new InvalidExecutionUnitParameterException (ID_COUNT_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		if (overRideIdint <0){
			throw new InvalidExecutionUnitParameterException (OVERRIDE_ID_KEY + " is less or equal to 0.  " +
					"Invalid value for scenario with description: " + getDescription() + ".  Value must be one or more.");
		}
		
		if( randomRead ){
			// generate a random id between startId and startId+count
			overRideIdint = getRandomInteger(startId, startId+(idCount-1));
		}
		// check for the over-ride id, if set, run the read only 1 time and exit
		// else read from startId -> startId+count
		if (overRideIdint > 0) {
			startId = overRideIdint;
			idCount = 1;
		}
	}
}
