package com.ibm.wssvt.acme.annuity.common.client.adapter;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import com.ibm.wssvt.acme.annuity.common.bean.IConfigData;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuitySingletonService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.adapter.AbstractServerAdapter;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.log.AcmeLoggerConfig;


public abstract class AbstractSingletonServerAdapter  extends AbstractServerAdapter implements ISingletonServerAdapter {

    private static final long serialVersionUID = 6826107504077969435L;
    protected AcmeLogger getLogger(String loggerName){  
        Logger root = Logger.getLogger(getClientContext().getPrefixedRootLoggerName());     
        AcmeLoggerConfig.setClientFileNamePattern("",
                getClientContext().getClientId(), getClientContext().getThreadId(), this);
        Logger theLogger = AnnuityLoggerFactory.getClientLogger(root, this, getClientContext().getLoggerPrefix()+ loggerName);      
        return new AcmeLogger(theLogger);
        /*
        Logger logger = Logger.getLogger(getClientContext().getLoggerPrefix()+ loggerName);
        logger.setParent(super.getClientContext().getRootLogger());     
        logger.setLevel(super.getClientContext().getRootLogger().getLevel());       
        return logger;
         */     
    }


    public void updateConfigDataInDB(IConfigData configData) throws InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException{
        IAnnuitySingletonService service = getAnnuityService();   
        getServerAdapterEvent().incrumentRequest();
        service.updateConfigDataInDB(configData);
        incrumentRunServerInfo(configData);
        getServerAdapterEvent().incrumentUpdate();
        incrumentRunServerInfo(configData);
    }

    public void refreshConfigDataFromDB() throws EntityNotFoundException, 
    InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException {
        IAnnuitySingletonService service = getAnnuityService();   
        getServerAdapterEvent().incrumentRequest();
        service.refreshConfigDataFromDB();
        getServerAdapterEvent().incrumentRead();
    }


    public IConfigData getServerConfigData(Configrable<String, String> configuration) throws ServerAdapterCommunicationException {
        IAnnuitySingletonService service = getAnnuityService(); 
        getServerAdapterEvent().incrumentRequest();
        IConfigData result = service.getServerConfigData(configuration);            
        getServerAdapterEvent().incrumentRead();
        incrumentRunServerInfo(result);
        return result;
    }

    public void testReadConcurrency(IConfigData configData) throws ServerAdapterCommunicationException{
        IAnnuitySingletonService service = getAnnuityService(); 
        getServerAdapterEvent().incrumentRequest();
        service.testReadConcurrency( configData);         
        getServerAdapterEvent().incrumentRead();
        incrumentRunServerInfo(configData);
    }

    public void testWriteConcurrency(IConfigData configData) throws ServerAdapterCommunicationException{
        IAnnuitySingletonService service = getAnnuityService(); 
        getServerAdapterEvent().incrumentRequest();
        service.testWriteConcurrency( configData);         
        getServerAdapterEvent().incrumentUpdate();
        incrumentRunServerInfo(configData);
    }

    public void setServerConfigData(IConfigData configData) throws ServerAdapterCommunicationException {
        IAnnuitySingletonService service = getAnnuityService(); 
        getServerAdapterEvent().incrumentRequest();
        service.setServerConfigData(configData);         
        getServerAdapterEvent().incrumentUpdate();
        incrumentRunServerInfo(configData);
    }

    public IConfigData createConfigData(IConfigData configData) throws ServerAdapterCommunicationException, ServerInternalErrorException, EntityAlreadyExistsException, InvalidArgumentException, RemoteException {
        IAnnuitySingletonService service = getAnnuityService(); 
        getServerAdapterEvent().incrumentRequest();
        IConfigData result = service.createConfigData(configData);          
        getServerAdapterEvent().incrumentCreate();
        incrumentRunServerInfo(result);
        return result;
    }

    public IConfigData updateConfigData(IConfigData configData) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
        IAnnuitySingletonService service = getAnnuityService();
        getServerAdapterEvent().incrumentRequest();
        IConfigData result = service.updateConfigData(configData);
        getServerAdapterEvent().incrumentUpdate();
        incrumentRunServerInfo(result);
        return result;
    }

    public void deleteConfigData(IConfigData configData) throws ServerAdapterCommunicationException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
        IAnnuitySingletonService service = getAnnuityService();
        getServerAdapterEvent().incrumentRequest();
        service.deleteConfigData(configData);           
        getServerAdapterEvent().incrumentDelete();      
    }

    public IConfigData findConfigDataById(IConfigData configData) throws ServerAdapterCommunicationException, EntityNotFoundException, ServerInternalErrorException, InvalidArgumentException , RemoteException {
        IAnnuitySingletonService service = getAnnuityService();
        getServerAdapterEvent().incrumentRequest();
        IConfigData result = service.findConfigDataById(configData);
        getServerAdapterEvent().incrumentRead();
        incrumentRunServerInfo(result);
        return result;
    }

    private void incrumentRunServerInfo(Configrable<String, String> configrable) {  
        if (configrable != null && configrable.getConfiguration() != null) {
            if(configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo") != null) {
                String[] servers = configrable.getConfiguration().getParameterValue("internal.runningOnServerInfo").split(";;");
                getServerAdapterEvent().incrumentRunServerCount(servers[0]);
                for(int i=0; i<servers.length; i++) {
                    getServerAdapterEvent().incrumentOverallRunServerCount(servers[i]);
                }
            }
            synchronized (configrable) {
                configrable.getConfiguration().addParameter("internal.runningOnServerInfo", "");    
            }
        }
    }

    protected abstract IAnnuitySingletonService getAnnuityService() throws ServerAdapterCommunicationException;


}
