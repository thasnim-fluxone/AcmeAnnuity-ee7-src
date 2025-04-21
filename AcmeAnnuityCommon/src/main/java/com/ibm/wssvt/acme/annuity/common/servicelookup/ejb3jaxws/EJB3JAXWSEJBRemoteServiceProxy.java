package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb3jaxws;

import java.util.ArrayList;
import java.util.List;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

public class EJB3JAXWSEJBRemoteServiceProxy implements IAnnuityService {

	private AnnuityMgmtSvcEJB30JAXWS ejb3jaxwsRemote;

	public EJB3JAXWSEJBRemoteServiceProxy(
			AnnuityMgmtSvcEJB30JAXWS webServiceInterface) {
		this.ejb3jaxwsRemote = webServiceInterface;
	}

	public IAnnuity createAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		AnnuityValueObject result = null;

		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(ann);
		result = ejb3jaxwsRemote.createAnnuity(annuityValueObject);
		// rebuild remote relationship
		annuityValueObject.getAnnuity();
		return (result == null) ? null : result.getAnnuity();

	}

	public IAnnuity findAnnuityById(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		AnnuityValueObject result = null;
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		result = ejb3jaxwsRemote.findAnnuityById(annuityValueObject);
		// rebuild remote relationship
		annuityValueObject.getAnnuity();
		return (result == null) ? null : result.getAnnuity();
	}

	public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		IAnnuityHolder result = null;
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		result = ejb3jaxwsRemote.findAnnuityHolder(annuityValueObject);
		// rebuild remote relationship
		annuityValueObject.getAnnuity();
		return result;
	}

	public IContact findContactById(IContact contact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsRemote.findContactById((Contact) contact);
		return result;
	}

	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = new ArrayList<IAnnuity>();
		List<AnnuityValueObject> tempList = new ArrayList<AnnuityValueObject>();
		tempList = ejb3jaxwsRemote
				.findHolderAnnuities((AnnuityHolder) annuityHolder);
		for (AnnuityValueObject object : tempList) {
			result.add(object.getAnnuity());
		}
		return result;
	}

	public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		IAnnuityHolder result = null;
		result = ejb3jaxwsRemote.findHolderById((AnnuityHolder) annuityHolder);
		return result;
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		List<AnnuityValueObject> tempList = ejb3jaxwsRemote
				.findPayorAnnuities((Payor) payor);
		if (tempList != null) {
			result = new ArrayList<IAnnuity>();
			for (AnnuityValueObject object : tempList) {
				result.add(object.getAnnuity());
			}
		}
		return result;
	}

	public IPayor findPayorById(IPayor payor) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		result = ejb3jaxwsRemote.findPayorById((Payor) payor);
		return result;
	}

	public IPayout findPayoutById(IPayout payout)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsRemote.findPayoutById(in);
		// rebuild remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

	public IRider findRiderById(IRider rider) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {
		IRider result = null;
		result = ejb3jaxwsRemote.findRiderById((Rider) rider);
		return result;
	}

	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IAnnuityHolder result = null;
		result = ejb3jaxwsRemote.createAnnuityHolder((AnnuityHolder) annHolder);
		return result;
	}

	public IContact createContact(IContact contact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsRemote.createContact((Contact) contact);
		return result;
	}

	public IPayor createPayor(IPayor payor)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IPayor result = null;
		result = ejb3jaxwsRemote.createPayor((Payor) payor);
		return result;
	}

	public IPayout createPayout(IPayout payout)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsRemote.createPayout(in);
		// rebuild the remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

	public void deleteAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		ejb3jaxwsRemote.deleteAnnuity(annuityValueObject);
		// rebuild the remote relationship
		annuityValueObject.getAnnuity();
	}

	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		ejb3jaxwsRemote.deleteAnnuityHolder((AnnuityHolder) annHolder);
	}

	public void deleteContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		ejb3jaxwsRemote.deleteContact((Contact) contact);
	}

	public void deletePayor(IPayor payor) throws ServerInternalErrorException,
			InvalidArgumentException {
		ejb3jaxwsRemote.deletePayor((Payor) payor);
	}

	public void deletePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		ejb3jaxwsRemote.deletePayout(in);
		// rebuild remote relationship
		in.getPayout();

	}

	public void deleteRider(IRider rider) throws ServerInternalErrorException,
			InvalidArgumentException {
		ejb3jaxwsRemote.deleteRider((Rider) rider);

	}

	public IAnnuity updateAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject result = null;
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		result = ejb3jaxwsRemote.updateAnnuity(annuityValueObject);
		// rebuild the remote relationship
		annuityValueObject.getAnnuity();
		return (result == null) ? null : result.getAnnuity();
	}

	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		result = ejb3jaxwsRemote.updateAnnuityHolder((AnnuityHolder) annHolder);
		return result;
	}

	public IContact updateContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsRemote.updateContact((Contact) contact);
		return result;
	}

	public IPayor updatePayor(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		result = ejb3jaxwsRemote.updatePayor((Payor) payor);
		return result;
	}

	public IPayout updatePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsRemote.updatePayout(in);
		// rebuild remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

}
