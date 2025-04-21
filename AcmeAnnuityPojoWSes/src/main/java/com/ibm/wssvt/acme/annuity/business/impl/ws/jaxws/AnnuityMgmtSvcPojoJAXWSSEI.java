package com.ibm.wssvt.acme.annuity.business.impl.ws.jaxws;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.ibm.wssvt.acme.annuity.common.business.jaxws.AnnuityMgmtSvcJAXWS;

@WebService(name = "AnnuityMgmtSvcPojoJAXWS", 
		targetNamespace = "http://jaxws.client.common.annuity.acme.wssvt.ibm.com/pojoimpl/")
@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,
		use=SOAPBinding.Use.LITERAL, 
		parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public interface AnnuityMgmtSvcPojoJAXWSSEI extends AnnuityMgmtSvcJAXWS {		
}
