
package com.ibm.wssvt.acme.annuity.business.logic;

import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

/**
 * $Rev: 441 $
 * $Date: 2007-06-26 18:09:01 -0500 (Tue, 26 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface AnnuitySearch {
	
	public IAnnuity findAnnuityById(IAnnuity annuity) 
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;		
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IContact findContactById(IContact contact)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
		throws ServerInternalErrorException, InvalidArgumentException;
	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) 	
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public List<IAnnuity> findPayorAnnuities(IPayor payor)
		throws ServerInternalErrorException, InvalidArgumentException;
	
	public IPayor findPayorById(IPayor payor)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IPayout findPayoutById(IPayout payout)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
	public IRider findRiderById(IRider rider)
		throws EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException;
		
}
