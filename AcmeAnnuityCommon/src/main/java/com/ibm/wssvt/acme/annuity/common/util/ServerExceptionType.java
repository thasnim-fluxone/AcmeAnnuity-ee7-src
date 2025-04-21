package com.ibm.wssvt.acme.annuity.common.util;

/**
 * @author trinn / Kavi
 *
 */
public enum ServerExceptionType {
	OPTIMISTIC_LOCKING_EXCEPTION,
	ENTITY_ALREADY_EXISTS_EXCEPTION,
	FOREIGN_KEY_VIOLATION_EXCEPTION,
	DEADLOCK_EXCEPTION,
	UNKNOWN_ERROR;
}
