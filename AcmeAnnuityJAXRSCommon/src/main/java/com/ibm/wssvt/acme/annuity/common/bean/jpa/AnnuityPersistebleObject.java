package com.ibm.wssvt.acme.annuity.common.bean.jpa;

import javax.persistence.MappedSuperclass;
/**
 * $Rev: 404 $
 * $Date: 2007-06-19 18:10:34 -0500 (Tue, 19 Jun 2007) $
 * $Author: mohammad $
 * $LastChangedBy: mohammad $
 */
@MappedSuperclass
public class AnnuityPersistebleObject extends AbstractPersistebleObject implements JPAPersisteble {
	private static final long serialVersionUID = -1752164352355128830L;
	private String id;	

	@javax.persistence.Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
