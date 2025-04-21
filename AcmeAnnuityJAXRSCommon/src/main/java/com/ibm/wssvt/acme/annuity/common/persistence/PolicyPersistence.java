package com.ibm.wssvt.acme.annuity.common.persistence;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IBeneficiary;
import com.ibm.wssvt.acme.annuity.common.bean.IFund;
import com.ibm.wssvt.acme.annuity.common.bean.IPolicy;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;


public interface PolicyPersistence<O, K, V> extends BasicPersistence<O, K, V>{

	public List<IPolicy> getHolderPolicies(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException;
	public List<IBeneficiary> getHolderBeneficiaries(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException;
	public List<String> getHolderBeneficiaryContactEmails(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException;
	public IFund getFundIndexRateFromFundName(IAnnuityHolder holder, String fundName) throws InvalidArgumentException, ServerPersistenceModuleException;
	public List<IFund> getFundsInState(IAnnuityHolder holder, String state) throws InvalidArgumentException, ServerPersistenceModuleException;
	public List<O> customQuery(IAnnuityHolder holder) throws InvalidArgumentException, ServerPersistenceModuleException;
}