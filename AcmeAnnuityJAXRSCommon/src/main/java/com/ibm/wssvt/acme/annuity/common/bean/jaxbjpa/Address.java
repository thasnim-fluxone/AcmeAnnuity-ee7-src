package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Embeddable
@Access(AccessType.FIELD)
public class Address implements IAddress {
    private static final long serialVersionUID = -2017682230659955349L;
    @XmlElement
    private String line1;
    @XmlElement
    private String line2;
    @XmlElement
    private String city;
    @XmlElement
    private String state;
    @XmlElement
    @Transient
    private String zipCode;
    @XmlElement
    private String country;

    
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getLine1() {
        return line1;
    }   
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    @Column(name="ZIP_CODE")
    @Access(AccessType.PROPERTY)
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    public String toString() {
        return( "com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Address: " + 
                " line1: " + getLine1() + 
                " line2: " + getLine2() + 
                " city: " + getCity() + 
                " state: " + getState() + 
                " zipCode: " + getZipCode() + 
                " country: " + getCountry() );
    }
}
