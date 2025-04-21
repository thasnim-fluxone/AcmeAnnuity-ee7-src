package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.rmi.RemoteException;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.adapter.IServerAdapter;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;

public interface IAnnuityServerAdapter extends IServerAdapter{


	// business APIs
	public IAnnuity createAnnuity(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;
	public IAnnuity updateAnnuity(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException;
	public IAnnuity findAnnuityById(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException;
	public void deleteAnnuityById(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityNotFoundException, InvalidArgumentException, RemoteException;	

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity) 
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;	
	public IContact findContactById(IContact contact) 
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder) 
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;
	public IContact createContact(IContact contact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;
	public IPayor createPayor(IPayor payor) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;
	public void deleteAnnuityHolder(IAnnuityHolder annHolder) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public void deleteContact(IContact contact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public void deletePayor(IPayor payor) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException;
	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException;
	public IContact updateContact(IContact contact) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException;
	public IPayor updatePayor(IPayor payor) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	
	public IPayor findPayorById(IPayor payor)
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public IPayout findPayoutById(IPayout payout)
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public IRider findRiderById(IRider rider)
		throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public List<IAnnuity> findPayorAnnuities(IPayor payor) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;

	public IPayout createPayout(IPayout payout) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException; 
	public IPayout updatePayout(IPayout payout) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException;	

	public void deletePayout(IPayout payout) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;
	public void deleteRider(IRider rider) 
		throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException, RemoteException;

}
