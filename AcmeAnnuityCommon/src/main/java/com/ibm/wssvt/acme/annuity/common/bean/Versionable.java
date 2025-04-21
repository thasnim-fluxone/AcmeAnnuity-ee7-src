package com.ibm.wssvt.acme.annuity.common.bean;

import java.io.Serializable;

public interface Versionable extends Serializable{
	public void setVersion(int version);
	public int getVersion();
}
