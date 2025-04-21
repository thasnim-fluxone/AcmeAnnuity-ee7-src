package com.ibm.wssvt.acme.annuity.common.bean;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ContactTypeConverter implements AttributeConverter<ContactType, String>{

	    @Override
	    public String convertToDatabaseColumn(ContactType attribute) {
	        switch (attribute) {
	            case HOME:  
	                return "HOME";
	            case BUSINESS:
	                return "BUSINESS";
	            case OTHER:
	                return "OTHER";
	            default:
	                throw new IllegalArgumentException("Unknown" + attribute);
	        }
	    }

	    @Override
	    public ContactType convertToEntityAttribute(String dbData) {
	        switch (dbData) {
	            case "HOME":
	                return ContactType.HOME;
	            case "BUSINESS":
	                return ContactType.BUSINESS;
	            case "OTHER":
	                return ContactType.OTHER;
	            default:
	                throw new IllegalArgumentException("Unknown" + dbData);
	        }
	    }

		
	}


