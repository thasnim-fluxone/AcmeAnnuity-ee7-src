package com.ibm.wssvt.acme.annuity.common.util;

public class ServerExceptionMapper {

	public static ServerExceptionType getExceptionType(Exception e) {
		if (e == null) {
	  	    return null;			
		}
		if (e.getMessage() != null && e.getMessage().contains("An optimistic lock violation was detected when flushing object instance")) {
		       return ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION;
		}
		if (e.getMessage() != null && e.getMessage().contains("Optimistic locking errors were detected when flushing to the data store")) {
		       return ServerExceptionType.OPTIMISTIC_LOCKING_EXCEPTION;
		}
		if (e.getMessage() != null && e.getMessage().contains("deadlock or timeout.")) {
		       return ServerExceptionType.DEADLOCK_EXCEPTION;
		}
		if (e.getMessage() != null && e.getMessage().contains("it would have caused a duplicate key value in a unique or primary key constraint or unique index")) {
		       return ServerExceptionType.ENTITY_ALREADY_EXISTS_EXCEPTION;
		}
		// workaround for defect# 465272
		if (e.getMessage() != null && e.getMessage().contains("SQLCODE: -204, SQLSTATE: 42704")) {
		       return ServerExceptionType.ENTITY_ALREADY_EXISTS_EXCEPTION;
		}
		if (e.getMessage() != null && e.getMessage().contains("SQLCODE: -803, SQLSTATE: 23505")) {
		       return ServerExceptionType.ENTITY_ALREADY_EXISTS_EXCEPTION;
		}
		
		if (e.getMessage() != null && e.getMessage().contains("from having duplicate values")) {
		       return ServerExceptionType.ENTITY_ALREADY_EXISTS_EXCEPTION;
		}
				
		if (e.getMessage() != null && e.getMessage().contains("caused a violation of foreign key constraint")) {
		       return ServerExceptionType.FOREIGN_KEY_VIOLATION_EXCEPTION;
		}		
		return ServerExceptionType.UNKNOWN_ERROR;
	}

}
