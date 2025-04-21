package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//import org.codehaus.jackson.map.annotate.JsonDeserialize;
/**
 * $Rev: 491 $
 * $Date: 2007-07-09 18:24:12 -0500 (Mon, 09 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnnuityJAXRSReturn", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")
public class AnnuityJAXRSReturn implements Serializable{
	private static final long serialVersionUID = -2212569633245755773L;

	@XmlElement
	private String errorClass;
	public String getErrorClass() {
		return errorClass;
	}
	@XmlElement
	private String errorMsg;
	@XmlElement
	private String returnObjectClass;
	@XmlElement
	//@JsonDeserialize(as=Contact.class)
	//@JsonDeserialize(as = CustomJSONDeserializer.class)
	private Object returnObject;

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getReturnObjectClass() {
		return returnObjectClass;
	}
	public void setReturnObjectClass(String returnObjectClass) {
		this.returnObjectClass = returnObjectClass;
	}
	public Object getReturnObject() {
		return returnObject;
	}
	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

}
