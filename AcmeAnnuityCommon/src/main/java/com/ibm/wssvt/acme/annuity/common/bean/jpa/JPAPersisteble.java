package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import com.ibm.wssvt.acme.annuity.common.bean.IPersisteble;
import com.ibm.wssvt.acme.annuity.common.bean.Identifiable;
import com.ibm.wssvt.acme.common.bean.Configrable;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
public interface JPAPersisteble extends IPersisteble<String>, Identifiable<String>, Configrable<String, String>{

}
