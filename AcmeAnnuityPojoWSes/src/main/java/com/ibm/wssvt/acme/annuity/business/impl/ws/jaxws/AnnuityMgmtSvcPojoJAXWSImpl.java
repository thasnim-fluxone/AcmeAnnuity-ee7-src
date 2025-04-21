package com.ibm.wssvt.acme.annuity.business.impl.ws.jaxws;

import javax.ejb.EJB;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.AnnuityMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.business.ejb30.policy.PolicyMgmtSvcEJB30Local;
import com.ibm.wssvt.acme.annuity.common.business.jaxws.JAXWSBusinessBaseProxy;
import com.ibm.wssvt.acme.annuity.common.client.jaxws.ejb3impl.AnnuityMgmtSvcEJB30JAXWSImpl;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.util.ServerInjectedObjects;
import com.ibm.wssvt.acme.common.bean.Configrable;


//@WebService (endpointInterface="com.ibm.wssvt.acme.annuity.business.impl.ws.jaxws.AnnuityMgmtSvcPojoJAXWSSEI", 
//		targetNamespace="http://jaxws.client.common.annuity.acme.wssvt.ibm.com/pojoimpl/", 
//		serviceName="AnnuityMgmtSvcPojoJAXWSImpl", 
//		portName="AnnuityMgmtSvcPojoJAXWSImplPort",
//		wsdlLocation="WEB-INF/wsdl/AnnuityMgmtSvcPojoJAXWSImpl.wsdl")
@WebService (endpointInterface="com.ibm.wssvt.acme.annuity.business.impl.ws.jaxws.AnnuityMgmtSvcPojoJAXWSSEI", 
targetNamespace="http://jaxws.client.common.annuity.acme.wssvt.ibm.com/pojoimpl/", 
serviceName="AnnuityMgmtSvcPojoJAXWSImpl", 
portName="AnnuityMgmtSvcPojoJAXWSImplPort")

public class AnnuityMgmtSvcPojoJAXWSImpl extends JAXWSBusinessBaseProxy implements AnnuityMgmtSvcPojoJAXWSSEI{

	@EJB AnnuityMgmtSvcEJB30Local ejb3Local;
	@EJB PolicyMgmtSvcEJB30Local ejb3PolicyLocal;
	@WebServiceRef(wsdlLocation="WEB-INF/wsdl/AnnuityMgmtSvcEJB30JAXWSImpl.wsdl")
	private AnnuityMgmtSvcEJB30JAXWSImpl ejb3jaxws;
	
	@Override
	protected IAnnuityService getAnnuityService(
			Configrable<String, String> configrable)
			throws ServerBusinessModuleException {		
		ServerInjectedObjects.getInstance().addObject("AnnuityMgmtSvcEJB30JAXWSImpl", ejb3jaxws);
		return super.getAnnuityService(configrable);
	}	
	
	
}

