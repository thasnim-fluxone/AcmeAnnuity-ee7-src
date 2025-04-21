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
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;

public class EJB3JAXWSEJBLocalServiceProxy implements IAnnuityService {

	private AnnuityMgmtSvcEJB30JAXWSLocal ejb3jaxwsLocal;

	public EJB3JAXWSEJBLocalServiceProxy(
			AnnuityMgmtSvcEJB30JAXWSLocal ejbLocalInterface) {
		this.ejb3jaxwsLocal = ejbLocalInterface;
	}

	public IAnnuity createAnnuity(IAnnuity ann)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		AnnuityValueObject result = null;

		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(ann);
		result = ejb3jaxwsLocal.createAnnuity(annuityValueObject);
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
		result = ejb3jaxwsLocal.findAnnuityById(annuityValueObject);
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
		result = ejb3jaxwsLocal.findAnnuityHolder(annuityValueObject);
		// rebuild remote relationship
		annuityValueObject.getAnnuity();
		return result;
	}

	public IContact findContactById(IContact contact)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsLocal.findContactById((Contact) contact);
		return result;
	}

	public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = new ArrayList<IAnnuity>();
		List<AnnuityValueObject> tempList = new ArrayList<AnnuityValueObject>();
		tempList = ejb3jaxwsLocal
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
		result = ejb3jaxwsLocal.findHolderById((AnnuityHolder) annuityHolder);
		return result;
	}

	public List<IAnnuity> findPayorAnnuities(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		List<IAnnuity> result = null;
		List<AnnuityValueObject> tempList = ejb3jaxwsLocal
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
		result = ejb3jaxwsLocal.findPayorById((Payor) payor);
		return result;
	}

	public IPayout findPayoutById(IPayout payout)
			throws EntityNotFoundException, ServerInternalErrorException,
			InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsLocal.findPayoutById(in);
		// rebuild remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

	public IRider findRiderById(IRider rider) throws EntityNotFoundException,
			ServerInternalErrorException, InvalidArgumentException {
		IRider result = null;
		result = ejb3jaxwsLocal.findRiderById((Rider) rider);
		return result;
	}

	public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IAnnuityHolder result = null;
		result = ejb3jaxwsLocal.createAnnuityHolder((AnnuityHolder) annHolder);
		return result;
	}

	public IContact createContact(IContact contact)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsLocal.createContact((Contact) contact);
		return result;
	}

	public IPayor createPayor(IPayor payor)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		IPayor result = null;
		result = ejb3jaxwsLocal.createPayor((Payor) payor);
		return result;
	}

	public IPayout createPayout(IPayout payout)
			throws ServerInternalErrorException, EntityAlreadyExistsException,
			InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsLocal.createPayout(in);
		// rebuild the remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

	public void deleteAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		ejb3jaxwsLocal.deleteAnnuity(annuityValueObject);
		// rebuild the remote relationship
		annuityValueObject.getAnnuity();
	}

	public void deleteAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		ejb3jaxwsLocal.deleteAnnuityHolder((AnnuityHolder) annHolder);
	}

	public void deleteContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		ejb3jaxwsLocal.deleteContact((Contact) contact);
	}

	public void deletePayor(IPayor payor) throws ServerInternalErrorException,
			InvalidArgumentException {
		ejb3jaxwsLocal.deletePayor((Payor) payor);
	}

	public void deletePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		ejb3jaxwsLocal.deletePayout(in);
		// rebuild remote relationship
		in.getPayout();

	}

	public void deleteRider(IRider rider) throws ServerInternalErrorException,
			InvalidArgumentException {
		ejb3jaxwsLocal.deleteRider((Rider) rider);

	}

	public IAnnuity updateAnnuity(IAnnuity annuity)
			throws ServerInternalErrorException, InvalidArgumentException {
		AnnuityValueObject result = null;
		AnnuityValueObject annuityValueObject = new AnnuityValueObject();
		annuityValueObject.setAnnuity(annuity);
		result = ejb3jaxwsLocal.updateAnnuity(annuityValueObject);
		// rebuild the remote relationship
		annuityValueObject.getAnnuity();
		return (result == null) ? null : result.getAnnuity();
	}

	public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
			throws ServerInternalErrorException, InvalidArgumentException {
		IAnnuityHolder result = null;
		result = ejb3jaxwsLocal.updateAnnuityHolder((AnnuityHolder) annHolder);
		return result;
	}

	public IContact updateContact(IContact contact)
			throws ServerInternalErrorException, InvalidArgumentException {
		IContact result = null;
		result = ejb3jaxwsLocal.updateContact((Contact) contact);
		return result;
	}

	public IPayor updatePayor(IPayor payor)
			throws ServerInternalErrorException, InvalidArgumentException {
		IPayor result = null;
		result = ejb3jaxwsLocal.updatePayor((Payor) payor);
		return result;
	}

	public IPayout updatePayout(IPayout payout)
			throws ServerInternalErrorException, InvalidArgumentException {
		PayoutValueObject result = null;
		PayoutValueObject in = new PayoutValueObject();
		in.setPayout(payout);
		result = ejb3jaxwsLocal.updatePayout(in);
		// rebuild remote relationship
		in.getPayout();
		return (result == null) ? null : result.getPayout();
	}

}
