package com.ibm.wssvt.acme.annuity.common.client.executionunit;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneContact;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.bean.IEquityAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFixedAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.Identifiable;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.IExecutionUnit;

/**
 * $Rev: 801 $ $Date: 2007-07-02 17:58:29 -0500 (Mon, 02 Jul 2007)$ 
 * $Author: gli $ 
 * $LastChangedBy: gli $
 */
public class ExecutionUnitVerificationHelper {
	
	private static final String VERIFY_EXACT_DATES = "verifyExactDates";
	private static DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	public static void assertNotNull(IExecutionUnit executionUnit, Object o, String preMsg, String postMsg) 
	throws ExecutionUnitVerificationException {
		if (o == null) {
			throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg, "The object is null"));
		}
	}

	@SuppressWarnings("unchecked")
	public static void assertValidId(IExecutionUnit executionUnit, Identifiable o, String preMsg, String postMsg) 
	throws ExecutionUnitVerificationException {
		assertNotNull(executionUnit, o, preMsg, postMsg);
		assertNotNull(executionUnit, o.getId(), preMsg, postMsg);
	}

	@SuppressWarnings("unchecked")
	public static void assertEquals(IExecutionUnit executionUnit, Comparable param1, Comparable results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, param1, results, preMsg, postMsg);
		if (results == null)return; // then they are both null
		if (param1.compareTo(results) == 0) {
			return;
		}
		throw new ExecutionUnitVerificationException(formatMessage(executionUnit.getDescription(), preMsg, postMsg,
			"The parameters are not equal.  Param:" + param1 + " is not equal to results: " + results));
	}

	public static void assertCalendarYYYYMMDDEquals(IExecutionUnit executionUnit, Calendar date, Calendar results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, date, results, preMsg, postMsg);
		if (results == null) return; // then they are both null		

		if ("true".equalsIgnoreCase(executionUnit.getConfiguration().getParameterValue(VERIFY_EXACT_DATES))) {							
			if (date.get(Calendar.YEAR) != results.get(Calendar.YEAR)
					|| date.get(Calendar.MONTH) != results.get(Calendar.MONTH)
					|| date.get(Calendar.DAY_OF_MONTH) != results.get(Calendar.DAY_OF_MONTH)) {
				throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg, date + 
						" is not equal to results: " + results + 
						" One of these attributes is different: Year, Month or Day"));
			}			
		}

	}

	public static void assertDateHHMMSSEquals(IExecutionUnit executionUnit, Date date, Date results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, date, results, preMsg, postMsg);
		if (results == null) return; // then they are both null	
		if ("true".equalsIgnoreCase(executionUnit.getConfiguration().getParameterValue(VERIFY_EXACT_DATES))) {
			Calendar dateCal = Calendar.getInstance();
			dateCal.setTime(date);
			Calendar resultsCal = Calendar.getInstance();
			resultsCal.setTime(results);	
			if (dateCal.get(Calendar.HOUR) != resultsCal.get(Calendar.HOUR) || 
				dateCal.get(Calendar.MINUTE) != resultsCal.get(Calendar.MINUTE) || 
				dateCal.get(Calendar.SECOND) != resultsCal.get(Calendar.SECOND)) {
				throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg, date + 
						" is not equal to results: " + results));
			}
		}
		
	}

	public static void assertDateYYYYMMDDEquals(IExecutionUnit executionUnit, Date date, Date results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, date, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		if ("true".equalsIgnoreCase(executionUnit.getConfiguration().getParameterValue(VERIFY_EXACT_DATES))) {
			Calendar dateCal = Calendar.getInstance();
			dateCal.setTime(date);
			Calendar resultsCal = Calendar.getInstance();
			resultsCal.setTime(results);	
			if (dateCal.get(Calendar.YEAR) != resultsCal.get(Calendar.YEAR) || 
				dateCal.get(Calendar.MONTH) != resultsCal.get(Calendar.MONTH) || 
				dateCal.get(Calendar.DAY_OF_MONTH) != resultsCal.get(Calendar.DAY_OF_MONTH)) {
				throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg, date + 
						" is not equal to results: " + results));
			}
		}

	}

	// check to see if d2 is greater than or equal to d1
	public static void assertDateGTorEquals(IExecutionUnit executionUnit, Date d1, Date d2, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		if (d1.compareTo(d2) < 0) {
			throw new ExecutionUnitVerificationException(
				formatMessage(executionUnit.getDescription(), preMsg, postMsg, "date1 is less than d2"));
		}

	}

	public static void assertIdContains(IExecutionUnit executionUnit, String id1, String id2, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		if (!id1.contains(id2)) {
			throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg, "Object id is not a subset"));
		}

	}

	public static void assertEqual(IExecutionUnit executionUnit, IAnnuityHolder annuityHolder, IAnnuityHolder results,
		String preMsg, String postMsg) 
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, annuityHolder, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertEqual(executionUnit, annuityHolder.getPicture(), results.getPicture(),
			preMsg, postMsg + " for AnnuityHolder Picture");
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+" lastUpdateDate validation error",
			" results value is null.");
		assertEquals(executionUnit, annuityHolder.getId(), results.getId(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  Id validation error", postMsg);
		assertEquals(executionUnit, annuityHolder.getFirstName(), results .getFirstName(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  first Name validation error", postMsg);
		assertEquals(executionUnit, annuityHolder.getGovernmentId(), results.getGovernmentId(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  government id validation error",
			postMsg);
		assertEquals(executionUnit, annuityHolder.getLastName(), results.getLastName(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  last Name validation error", postMsg);
		assertEquals(executionUnit, annuityHolder.getCategory(), results.getCategory(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  Category validation error", postMsg);
		assertDateYYYYMMDDEquals(executionUnit, annuityHolder.getDateOfBirth(), results.getDateOfBirth(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  date of birth validation error", postMsg);
		assertDateHHMMSSEquals(executionUnit, annuityHolder.getTimeOfBirth(), results.getTimeOfBirth(),
			preMsg + " AnnuityHolder:"+annuityHolder.getId()+"  time of birth validation error",
			postMsg);

	}

	public static void assertEqual(IExecutionUnit executionUnit, Byte[] param1, Byte[] results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, param1, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		if (param1.length != results.length) {
			throw new ExecutionUnitVerificationException(
					formatMessage(executionUnit.getDescription(), preMsg, postMsg,
						"Array param1 size is not equal to array param2 size.  param1 length:"
						+ param1.length + " param2 length: " + results.length));
		}
		for (int i = 0; i < param1.length; i++) {
			if ((param1[i].compareTo(results[i])) != 0) {
				throw new ExecutionUnitVerificationException(
					formatMessage(
						executionUnit.getDescription(), preMsg, postMsg,	"Array param1 byte at location: "
							+ i	+ " is not equal to array param2 value at the same location."
							+ " param1 value: " + param1[i] + " param2 value: " + results[i]));
			}

		}

	}

	public static void assertEqual(IExecutionUnit executionUnit, IContact contact, IContact results, String preMsg, String postMsg)
		throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, contact, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " Contact:"+contact.getId()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, contact.getId(), results.getId(),
			preMsg + " Contact:"+contact.getId()+"  Id validation error", postMsg);
		assertEquals(executionUnit, contact.getEmail(), results.getEmail(),
			preMsg + " Contact:"+contact.getId()+"  Email validation error", postMsg);
		assertEquals(executionUnit, contact.getPhone(), results.getPhone(),
			preMsg + " Contact:"+contact.getId()+"  Phone validation error", postMsg);
		assertEquals(executionUnit, contact.getContactType(), results.getContactType(),
			preMsg + " Contact:"+contact.getId()+"  ContactType validation error", postMsg);
		assertEqual(executionUnit, contact.getAddress(), results.getAddress(),
			preMsg, postMsg);
	}

	public static void assertEqual(IExecutionUnit executionUnit, IAddress address, IAddress results, String preMsg, String postMsg) 
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, address, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertEquals(executionUnit, address.getLine1(), results.getLine1(),
			preMsg + " Contact.Address Line1 validation error", postMsg);
		assertEquals(executionUnit, address.getLine2(), results.getLine2(),
			preMsg + " Contact.Address Line2 validation error", postMsg);
		assertEquals(executionUnit, address.getCity(), results.getCity(),
			preMsg + " Contact.Address City validation error", postMsg);
		assertEquals(executionUnit, address.getState(), results.getState(),
			preMsg + " Contact.Address State validation error", postMsg);
		assertEquals(executionUnit, address.getCountry(), results.getCountry(),
			preMsg + " Contact.Address Country validation error", postMsg);
		assertEquals(executionUnit, address.getZipCode(), results.getZipCode(),
			preMsg + " Contact.Address Zip Code validation error", postMsg);

	}

	public static void assertEqual(IExecutionUnit executionUnit, IPayor payor, IPayor results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, payor, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " Payor:"+payor.getId()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, payor.getId(), results.getId(),
			preMsg + " Payor:"+payor.getId()+"  id validation error", postMsg);
		assertEquals(executionUnit, payor.getName(), results.getName(),
			preMsg + " Payor:"+payor.getId()+"  name validation error", postMsg);

	}

	public static void assertEqual(IExecutionUnit executionUnit, IFixedAnnuity annuity, IFixedAnnuity results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, annuity, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertEqual(executionUnit, (IAnnuity) annuity, (IAnnuity) results, preMsg, postMsg);
		Double d = annuity.getRate();
		if (d != null) {						
			d = new Double(decimalFormat.format(d));			
		}
		assertEquals(executionUnit, d, results.getRate(), preMsg, postMsg);
	}

	public static void assertEqual(IExecutionUnit executionUnit, IEquityAnnuity annuity, IEquityAnnuity results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, annuity, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertEqual(executionUnit, (IAnnuity) annuity, (IAnnuity) results, preMsg, postMsg);
		Double d = annuity.getIndexRate();
		if (d != null) {						
			d = new Double(decimalFormat.format(d));			
		}
		assertEquals(executionUnit, d, results.getIndexRate(), preMsg, postMsg);
		assertEquals(executionUnit, annuity.getFundNames(), results.getFundNames(), preMsg, postMsg);

	}

	public static void assertEqual(IExecutionUnit executionUnit, IAnnuity annuity, IAnnuity results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, annuity, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + "Annuity:"+annuity.getId()+ " AnnuitylastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, annuity.getId(), results.getId(),
			preMsg + "Annuity:"+annuity.getId()+ " Annuityid validation error", postMsg);
		assertEquals(executionUnit, annuity.getAccountNumber(), results .getAccountNumber(),
			preMsg + "Annuity:"+annuity.getId()+ " Annuity account number validation error", postMsg);
		assertEquals(executionUnit, annuity.getAnnuityHolderId(), results.getAnnuityHolderId(),
			preMsg + "Annuity:"+annuity.getId()+ " Annuity'annuity holder id' validation error", postMsg);
		Double d = annuity.getAmount();
		if (d != null) {						
			d = new Double(decimalFormat.format(d));			
		}
		assertEquals(executionUnit, d, results.getAmount(),
			preMsg + "Annuity:"+annuity.getId()+ " Annuity amount validation error", postMsg);
		d = annuity.getLastPaidAmt();
		if (d != null) {						
			d = new Double(decimalFormat.format(d));			
		}
		assertEquals(executionUnit, d, results.getLastPaidAmt(),
			preMsg + "Annuity:"+annuity.getId()+ " Annuity last paid amount validation error", postMsg);

	}

	@SuppressWarnings("unchecked")
	public static void assertEqual(IExecutionUnit executionUnit, List list, List results, String preMsg, String postMsg) 
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, list, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		if (list.size() != results.size()) {
			throw new ExecutionUnitVerificationException(
				formatMessage(executionUnit.getDescription(), preMsg, postMsg,
					"List size is not equal to results list size."
					+ "list size is: " + list.size() + " results size is: " + results.size()));
		}
	}

	public static void assertEqual(IExecutionUnit executionUnit, IRider rider, IRider results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, rider, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " Rider:"+rider.getId()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, rider.getId(), results.getId(),
			preMsg + " Rider:"+rider.getId()+" id validation error", postMsg);
		assertDateYYYYMMDDEquals(executionUnit, rider.getEffectiveDate(), results.getEffectiveDate(),
			preMsg + " Rider:"+rider.getId()+" effective date validation error", postMsg);
		assertEquals(executionUnit, rider.getRule(), results.getRule(),
			preMsg + " Rider:"+rider.getId()+" rule validation error", postMsg);		
		assertEquals(executionUnit, rider.getType(), results.getType(),
			preMsg + " Rider:"+rider.getId()+" type validation error", postMsg);
	}

	public static void assertEqual(IExecutionUnit executionUnit, IPayout payout, IPayout results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, payout, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " Payout:"+payout.getId()+" lastUpdateDate validation error", "results value is null.");
		assertCalendarYYYYMMDDEquals(executionUnit, payout.getEndDate(), results.getEndDate(), 
			preMsg + " Payout:"+payout.getId()+" end date validation error", postMsg);
		assertEquals(executionUnit, payout.getId(), results.getId(),
			preMsg + " Payout:"+payout.getId()+" id validation error", postMsg);
		assertCalendarYYYYMMDDEquals(executionUnit, payout.getStartDate(), results.getStartDate(),
			preMsg + " Payout:"+payout.getId()+" start date validation error", postMsg);
		BigDecimal d = payout.getTaxableAmount();
		if (d != null) {						
			d = new BigDecimal(decimalFormat.format(d));			
		}
		assertEquals(executionUnit, d, results.getTaxableAmount(),
			preMsg + " Payout:"+payout.getId()+" taxable amount validation error", postMsg);
	}

    public static void assertEqual(IExecutionUnit executionUnit, IConfigData configData, IConfigData results, String preMsg, String postMsg)
    throws ExecutionUnitVerificationException {
        assertNullabilityAndClass(executionUnit, configData, results, preMsg, postMsg);
        if (results == null) return; // then they are both null
        assertNotNull(executionUnit, results.getLastUpdateDate(),
                preMsg + " ConfigData:"+configData.getId()+" lastUpdateDate validation error", "results value is null.");
        assertEquals(executionUnit, configData.getId(), results.getId(),
                preMsg + " ConfigData:"+configData.getId()+"  Id validation error", postMsg);

        assertEquals(executionUnit, configData.getConfigMap().toString(), results.getConfigMap().toString(),
                preMsg + " ConfigData:"+configData.getId()+"  ConfigValue validation error", postMsg);
    }
	
	
	public static void assertEqual(IExecutionUnit executionUnit,
			IPolicy policy, IPolicy results, String preMsg, String postMsg) throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, policy, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
			preMsg + " Policy:"+ policy.getId()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, policy.getId(), results.getId(),
				preMsg + " Policy:"+ policy.getId()+" id validation error", postMsg);
		assertEquals(executionUnit, policy.getAnnuityHolderId(), results.getAnnuityHolderId(),
				preMsg + " Policy:"+ policy.getId()+" Holder id validation error", postMsg);
		for(int i=1; i <= policy.getFunds().size(); i++) {			
			assertEqual(executionUnit, policy.getFunds().get(new Integer(i)), results.getFunds().get(new Integer(i)),preMsg, postMsg);
		}		
	}
		
	public static void assertEqual(IExecutionUnit executionUnit, IFund fund, IFund results, String preMsg, String postMsg) throws ExecutionUnitVerificationException {
		assertNullabilityAndClass(executionUnit, fund, results, preMsg, postMsg);
		if (results == null) return; // then they are both null
		assertEquals(executionUnit, fund.getFundName(), results.getFundName(),
			preMsg + " Fund name validation error", postMsg);
		assertEquals(executionUnit, fund.getIndexRate(), results.getIndexRate(),
			preMsg + " Fund Index rate validation error", postMsg);	
		assertEqual(executionUnit, fund.getAddress(), results.getAddress(),
				preMsg + " Fund Address validation error", postMsg);	
	}
	
	public static void assertEqual(IExecutionUnit executionUnit, IBeneficiary beneficiary, IBeneficiary results, String preMsg, String postMsg) throws ExecutionUnitVerificationException {
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, beneficiary.getId(), results.getId(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" id validation error", postMsg);
		assertEquals(executionUnit, beneficiary.getFirstName(), results.getFirstName(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" first name validation error", postMsg);
		assertEquals(executionUnit, beneficiary.getLastName(), results.getLastName(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" last name validation error", postMsg);
		assertEquals(executionUnit, beneficiary.getRelationship(), results.getRelationship(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" relationship validation error", postMsg);
		assertEquals(executionUnit, beneficiary.getAnnuityHolderId(), results.getAnnuityHolderId(),
				preMsg + " Beneficiary:" + beneficiary.getId()+" annuity holder id validation error", postMsg);
	}
	
	public static void assertEqual(IExecutionUnit executionUnit, IBeneContact beneContact, IBeneContact results, String preMsg, String postMsg) throws ExecutionUnitVerificationException {
		if (results == null) return; // then they are both null
		assertNotNull(executionUnit, results.getLastUpdateDate(),
				preMsg + " BeneContact:" + beneContact.getId().toString()+" lastUpdateDate validation error", "results value is null.");
		assertEquals(executionUnit, beneContact.getId().getContactType(), results.getId().getContactType(),
				preMsg + " BeneContact:" + beneContact.getId().toString()+" id-type validation error", postMsg);
		assertEquals(executionUnit, beneContact.getId().getBeneficiaryPK(), results.getId().getBeneficiaryPK(),
				preMsg + " BeneContact:" + beneContact.getId().toString()+" id-key validation error", postMsg);
		assertEquals(executionUnit, beneContact.getEmail(), results.getEmail(),
				preMsg + " BeneContact:" + beneContact.getId().toString()+" email validation error", postMsg);
		assertEquals(executionUnit, beneContact.getPhone(), results.getPhone(),
				preMsg + " BeneContact:" + beneContact.getId().toString()+" email validation error", postMsg);
	}

	private static void assertNullabilityAndClass(IExecutionUnit executionUnit, Object obj, Object results, String preMsg, String postMsg)
	throws ExecutionUnitVerificationException {
		if (obj == null && results == null) {
			return;
		}
		if (obj == null) {
			throw new ExecutionUnitVerificationException(formatMessage(executionUnit.getDescription(), preMsg, postMsg,
				"object is null which is not equal to results: " + results));
		}
		if (results == null) {
			throw new ExecutionUnitVerificationException(formatMessage(executionUnit.getDescription(), preMsg, postMsg, 
				"results is null is not equal to object:" + obj));
		}
// Commented out due to defect # 468039		
//		if (!(obj.getClass().equals(results.getClass()))) {
//			throw new ExecutionUnitVerificationException(formatMessage(scenario.getDescription(), preMsg, postMsg, obj.getClass().toString()
//				+ " is not equal to results " + results.getClass().toString() + ". They are different classes"));
//		}
		
	}

	private static String formatMessage(String description, String preMsg,
			String postMsg, String localMsg) {
		StringBuffer sb = new StringBuffer();
		if (preMsg != null) {
			sb.append(preMsg);
			sb.append(".  ");
		}
		sb.append("Execution Unit Description: ");
		sb.append(description);
		sb.append(".  ");
		if (postMsg != null) {
			sb.append(postMsg);
			sb.append(".  ");
		}
		if (localMsg != null) {
			sb.append(localMsg);
			sb.append(".");
		}
		return sb.toString();
	}

	

}
