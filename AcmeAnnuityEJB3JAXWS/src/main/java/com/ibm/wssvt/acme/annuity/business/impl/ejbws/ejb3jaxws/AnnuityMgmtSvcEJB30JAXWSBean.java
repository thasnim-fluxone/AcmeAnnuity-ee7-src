package com.ibm.wssvt.acme.annuity.business.impl.ejbws.ejb3jaxws;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWS;
import com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSLocal;
import com.ibm.wssvt.acme.annuity.common.business.jaxws.AnnuityMgmtSvcJAXWS;
import com.ibm.wssvt.acme.annuity.common.business.jaxws.JAXWSBusinessBaseProxy;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerPersistenceModuleException;
import com.ibm.wssvt.acme.annuity.common.util.EJBInputParamExaminerInterceptor;
import com.ibm.wssvt.acme.annuity.common.util.ServerInjectedObjects;
import com.ibm.wssvt.acme.common.bean.Configrable;

@Stateless
@RolesAllowed("users")
@Remote(AnnuityMgmtSvcEJB30JAXWS.class)
@Local(AnnuityMgmtSvcEJB30JAXWSLocal.class)
@Interceptors(EJBInputParamExaminerInterceptor.class)

/*Commenting the wsdlLocation section. If this is not commmeted out - when doing CRUD Contact using the EJB_WS Adapter(8), 
 * We get an unmarshalled exception like this
  org.apache.cxf.interceptor.Fault: Unmarshalling Error: unexpected element (uri:"http://jaxws.client.common.annuity.acme.wssvt.ibm.com/ejb3impl/", local:"message").
  Expected elements are <{}message> 
  at org.apache.cxf.jaxb.JAXBEncoderDecoder.unmarshall(JAXBEncoderDecoder.java:838) 
 */
//@WebService (endpointInterface="com.ibm.wssvt.acme.annuity.business.impl.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSSEI", 
//		targetNamespace="http://jaxws.client.common.annuity.acme.wssvt.ibm.com/ejb3impl/", 
//		serviceName="AnnuityMgmtSvcEJB30JAXWSImpl", 
//		portName="AnnuityMgmtSvcEJB3JAXWSImplPort",
//		wsdlLocation="META-INF/wsdl/AnnuityMgmtSvcEJB30JAXWSImpl.wsdl")

@WebService (endpointInterface="com.ibm.wssvt.acme.annuity.business.impl.ejbws.ejb3jaxws.AnnuityMgmtSvcEJB30JAXWSSEI", 
targetNamespace="http://jaxws.client.common.annuity.acme.wssvt.ibm.com/ejb3impl/", 
serviceName="AnnuityMgmtSvcEJB30JAXWSImpl", 
portName="AnnuityMgmtSvcEJB3JAXWSImplPort")

public class AnnuityMgmtSvcEJB30JAXWSBean extends JAXWSBusinessBaseProxy implements AnnuityMgmtSvcJAXWS{

	//create multiple @WebServiceRef to test policyset binding attachments functions
	@WebServiceRef(wsdlLocation="META-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl")	
	private AnnuityMgmtSvcPojoJAXWSImpl pojoJaxws;
	@WebServiceRef(wsdlLocation = "META-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl")
	private AnnuityMgmtSvcPojoJAXWSImpl pojoJaxws2;
	@WebServiceRef(wsdlLocation = "META-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl")
	private AnnuityMgmtSvcPojoJAXWSImpl pojoJaxws3;
	
	String CLASSNAME = AnnuityMgmtSvcEJB30JAXWSBean.class.getName();
    Logger logger = Logger.getLogger(CLASSNAME);
    
	@RolesAllowed ("admins")
	@Override
	public AnnuityValueObject createAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		return super.createAnnuity(annuityVO);
	}

	@RolesAllowed ("admins") 
	@Override
	public AnnuityHolder createAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		return super.createAnnuityHolder(annHolder);
	}

	@RolesAllowed ("admins")
	@Override
	public Contact createContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		return super.createContact(contact);
	}

	@RolesAllowed ("admins")
	@Override
	public Payor createPayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		return super.createPayor(payor);
	}

	@RolesAllowed ("admins")
	@Override
	public PayoutValueObject createPayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, EntityAlreadyExistsException, InvalidArgumentException {
		return super.createPayout(payoutVO);
	}

	@RolesAllowed ("admins")
	@Override
	public void deleteAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deleteAnnuity(annuityVO);
	}

	@RolesAllowed ("admins")
	@Override
	public void deleteAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deleteAnnuityHolder(annHolder);
	}

	@RolesAllowed ("admins") 
	@Override
	public void deleteContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deleteContact(contact);
	}

	@RolesAllowed ("admins") 
	@Override
	public void deletePayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deletePayor(payor);
	}
	
	@RolesAllowed ("admins")
	@Override
	public void deletePayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deletePayout(payoutVO);
	}

	@RolesAllowed ("admins")
	@Override
	public void deleteRider(Rider rider) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		super.deleteRider(rider);
	}

	@Override
	public AnnuityValueObject findAnnuityById(AnnuityValueObject annuityVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findAnnuityById(annuityVO);
	}

	@Override
	public AnnuityHolder findAnnuityHolder(AnnuityValueObject annuityVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findAnnuityHolder(annuityVO);
	}

	@Override
	public Contact findContactById(Contact contact) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findContactById(contact);
	}

	@Override
	public List<AnnuityValueObject> findHolderAnnuities(AnnuityHolder annuityHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findHolderAnnuities(annuityHolder);
	}

	@Override
	public AnnuityHolder findHolderById(AnnuityHolder annuityHolder) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findHolderById(annuityHolder);
	}

	@Override
	public List<AnnuityValueObject> findPayorAnnuities(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findPayorAnnuities(payor);
	}

	@Override
	public Payor findPayorById(Payor payor) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findPayorById(payor);
	}

	@Override
	public PayoutValueObject findPayoutById(PayoutValueObject payoutVO) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findPayoutById(payoutVO);
	}

	@Override
	public Rider findRiderById(Rider rider) throws EntityNotFoundException, ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.findRiderById(rider);
	}


	@RolesAllowed ("admins") 
	@Override
	public AnnuityValueObject updateAnnuity(AnnuityValueObject annuityVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.updateAnnuity(annuityVO);
	}

	@RolesAllowed ("admins")
	@Override
	public AnnuityHolder updateAnnuityHolder(AnnuityHolder annHolder) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.updateAnnuityHolder(annHolder);
	}
	
	@RolesAllowed ("admins")
	@Override
	public Contact updateContact(Contact contact) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.updateContact(contact);
	}
	
	@RolesAllowed ("admins")
	@Override
	public Payor updatePayor(Payor payor) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.updatePayor(payor);
	}
	
	@RolesAllowed ("admins") 
	@Override
	public PayoutValueObject updatePayout(PayoutValueObject payoutVO) throws ServerBusinessModuleException, ServerPersistenceModuleException, InvalidArgumentException {
		return super.updatePayout(payoutVO);
	}
	

	@Override
	protected IAnnuityService getAnnuityService(Configrable<String, String> configrable) throws ServerBusinessModuleException {
		//ServerInjectedObjects.getInstance().addObject("AnnuityMgmtSvcPojoJAXWSImpl", pojoJaxws);
		if ("1".equalsIgnoreCase(
				configrable.getConfiguration().getParameterValue("injectedServiceNumber"))) {
			ServerInjectedObjects.getInstance().addObject(
					"AnnuityMgmtSvcPojoJAXWSImpl1", pojoJaxws);	
			logger.fine("injecting AnnuityMgmtSvcPojoJAXWS service number 1 ...");
		}else if ("2".equalsIgnoreCase(
				configrable.getConfiguration().getParameterValue("injectedServiceNumber"))) {
			ServerInjectedObjects.getInstance().addObject(
					"AnnuityMgmtSvcPojoJAXWSImpl2", pojoJaxws2);	
			logger.fine("injecting AnnuityMgmtSvcPojoJAXWS service number 2 ...");
		}else if ("3".equalsIgnoreCase(
				configrable.getConfiguration().getParameterValue("injectedServiceNumber"))) {
			ServerInjectedObjects.getInstance().addObject(
					"AnnuityMgmtSvcPojoJAXWSImpl3", pojoJaxws3);	
			logger.fine("injecting AnnuityMgmtSvcPojoJAXWS service number 3 ...");
		}		
		else{
			ServerInjectedObjects.getInstance().addObject(
					"AnnuityMgmtSvcPojoJAXWSImpl", pojoJaxws);
		}
		return super.getAnnuityService(configrable);
	}
}

