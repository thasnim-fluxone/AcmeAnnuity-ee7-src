package com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;

import org.codehaus.jackson.annotate.JsonIgnore;
/**
 * $Rev: 491 $
 * $Date: 2007-07-09 18:24:12 -0500 (Mon, 09 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType (name = "Contact", namespace = "http://bean.common.annuity.acme.wssvt.ibm.com/jaxbjpa/")

@Entity
@Table(name="CONTACT")
@AttributeOverride(name="lastUpdateDate", column=@Column(name="LAST_UPDATE_TS"))
@Access(AccessType.FIELD)
public class Contact extends AnnuityPersistebleObject implements IContact {
    private static final long serialVersionUID = 4015672780551057807L;
    @XmlElement
    @Embedded
    private Address theAddress;
    @XmlElement
    @Column(name="EMAIL")
    private String email;
    @XmlElement
    @Column(name="PHONE")
    private String phone;
    @XmlElement
    @Transient
    private ContactType contactType;
    @JsonIgnore		//for defect 160064
    public IAddress getAddress() {
        return (IAddress) this.getTheAddress();
    }
    @JsonIgnore		//for defect 160064
    public void setAddress(IAddress address) {
        if (address instanceof Address){
            this.setTheAddress((Address)address);
        }else if(address == null) {
            this.setTheAddress(null);
        }
        else{
            throw new ClassCastException("Invalid Implementaion of IAddress.  " +
                    "Class must be instance of com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Address");
        }
    }
    
    private  Address getTheAddress() {
        return theAddress;
    }
    private  void setTheAddress(Address address) {
        this.theAddress = address;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Column(name="TYPE")
    @Enumerated(EnumType.STRING)    
    @Access(AccessType.PROPERTY)
    public ContactType getContactType() {
        return contactType;
    }
    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }
    
}
