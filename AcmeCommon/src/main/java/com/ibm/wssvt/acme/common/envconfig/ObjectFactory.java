
package com.ibm.wssvt.acme.common.envconfig;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    private final static QName _AcmeEnvConfigBean_QNAME = new QName("http://common.acme.wssvt.ibm.com/envconfig/", "AcmeEnvConfigBean");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa
     * 
     */
    public ObjectFactory() {
    }



    /**
     * Create an instance of {@link AcmeEnvConfigBean }
     * 
     */
    public AcmeEnvConfigBean createAcmeEnvConfigBean() {
        return new AcmeEnvConfigBean();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AcmeEnvConfigBean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://common.acme.wssvt.ibm.com/envconfig/", name = "AcmeEnvConfigBean")
    public JAXBElement<AcmeEnvConfigBean> createAcmeEnvConfigBean(AcmeEnvConfigBean value) {
        return new JAXBElement<AcmeEnvConfigBean>(_AcmeEnvConfigBean_QNAME, AcmeEnvConfigBean.class, null, value);
    }

    
}
