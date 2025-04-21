package com.ibm.wssvt.acme.common.client.ui.gui.model;

import java.util.Vector;

public interface IAdapterBeansFactoryMap {

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getShortName(String name);

	public abstract Vector<IAdapterBeansFactoryMap> getSupportedBeansFactories(
			AdapterConfigurationDecorator adapter);

}