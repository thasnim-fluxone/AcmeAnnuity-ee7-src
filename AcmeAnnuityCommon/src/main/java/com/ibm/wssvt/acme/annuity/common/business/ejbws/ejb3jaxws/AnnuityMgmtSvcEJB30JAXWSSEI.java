package com.ibm.wssvt.acme.annuity.common.business.ejbws.ejb3jaxws;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.ibm.wssvt.acme.annuity.common.business.jaxws.AnnuityMgmtSvcJAXWS;

@WebService(name = "AnnuityMgmtSvcEJB30JAXWS", 
		targetNamespace = "http://jaxws.client.common.annuity.acme.wssvt.ibm.com/ejb3impl/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,
		use=SOAPBinding.Use.LITERAL, 
		parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
		
public interface AnnuityMgmtSvcEJB30JAXWSSEI extends AnnuityMgmtSvcJAXWS{

}
