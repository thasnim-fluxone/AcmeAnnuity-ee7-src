package com.ibm.wssvt.acme.common.util;

/**
 * $Rev: 492 $
 * $Date: 2007-07-09 19:15:08 -0500 (Mon, 09 Jul 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public class StringUtils {
	
	public static int toInt(String s) throws NumberFormatException {		
		if (s == null || s.trim().length()<1) {
			throw new NumberFormatException("The parameter: " + s + " is null or empty");
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("The String: " + s + " is not an integer");

		}
	}
	

	public static <T extends Enum<T>> T  toEnum(Class<T> enumType, String value) 
		throws IllegalArgumentException {						
		try{			
			value  = value.toUpperCase();
			return Enum.valueOf(enumType, value);			 
		}catch (IllegalArgumentException e) {			
			throw new IllegalArgumentException("The value: " 
					+ value + " is not a valid value for the enum: " + enumType);															
		}catch (NullPointerException e){
			throw new IllegalArgumentException("The enum value passed is null.  " +
					"It is not a valid value for the enum: " + enumType);
		}
		
	}
}
