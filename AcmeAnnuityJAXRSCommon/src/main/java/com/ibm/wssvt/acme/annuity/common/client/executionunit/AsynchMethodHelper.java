package com.ibm.wssvt.acme.annuity.common.client.executionunit;
/*
import java.rmi.RemoteException;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.client.adapter.EJB31AsynchServerAdapter;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityType;
import com.ibm.wssvt.acme.common.adapter.ServerAdapterCommunicationException;
import com.ibm.wssvt.acme.common.executionunit.ExecutionUnitVerificationException;
import com.ibm.wssvt.acme.common.executionunit.InvalidExecutionUnitParameterException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;

public class AsynchMethodHelper {

    
    private static final int DEFAULT_MAX_LOOPS=100;
    private static final int DEFAULT_ASYNCH_WAIT=10;
    
    public static void verifyContactDelete(IContact contact, AbastractAnnuityExecutionUnit eu, AcmeLogger logger) throws InvalidExecutionUnitParameterException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, InterruptedException, ExecutionUnitVerificationException{

        
        if (isFireForgetVerficationDisabled(eu))
            return;

        IContact results=null;
        IContact result = eu.getAnnuityBeansFactory().createContact();
        result.setId(contact.getId());
        result.setConfiguration(eu.getConfiguration());
        try {
            results = eu.getServerAdapter().findContactById(result);
            // if we are using aysnch methods (Adapter 18) we need to loop and wait till
            // the deletion is done.
            int loop=0;

            int asynchMethodMaxLoops=getAsynchMaxLoops(eu);  
            while (isFireForgetVerficationEnabled(eu) && results!=null && loop <= asynchMethodMaxLoops){
                results = eu.getServerAdapter().findContactById(result);
                logger.info("Waiting for Asynchronous deletion to finish... attempt: " + loop );
                waitForAsynchMethod(eu);
                loop++;
            }

            if (results != null) {
                throw new AsynchMethodVerificationException(contact.getId());
            }
        } catch (EntityNotFoundException e) {

        }
    }

    public static void verifyPayerDelete(IPayor payor, AbastractAnnuityExecutionUnit eu, AcmeLogger logger) throws InvalidExecutionUnitParameterException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, InterruptedException, ExecutionUnitVerificationException{

        if (isFireForgetVerficationDisabled(eu))
            return;

        IPayor results=null;
        IPayor result = eu.getAnnuityBeansFactory().createPayor();
        result.setId(payor.getId());
        result.setConfiguration(eu.getConfiguration());
        try {
            results = eu.getServerAdapter().findPayorById(result);
            // if we are using aysnch methods (Adapter 18) we need to loop and wait till
            // the deletion is done.
            int loop=0;

            int asynchMethodMaxLoops=getAsynchMaxLoops(eu);  
            while (isFireForgetVerficationEnabled(eu) && results!=null && loop <= asynchMethodMaxLoops){
                results = eu.getServerAdapter().findPayorById(result);
                logger.info("Waiting for Asynchronous deletion to finish... attempt: " + loop );
                waitForAsynchMethod(eu);
                loop++;
            }

            if (results != null) {
                throw new AsynchMethodVerificationException(payor.getId());
            }
        } catch (EntityNotFoundException e) {

        }
    }


    public static void verifyHolderDelete(IAnnuityHolder holder, AbastractAnnuityExecutionUnit eu, AcmeLogger logger) throws InvalidExecutionUnitParameterException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, InterruptedException, ExecutionUnitVerificationException{

        if (isFireForgetVerficationDisabled(eu))
            return;

        IAnnuityHolder results=null;
        IAnnuityHolder result = eu.getAnnuityBeansFactory().createAnnuityHolder();
        result.setId(holder.getId());
        result.setConfiguration(eu.getConfiguration());
        try {
            results = eu.getServerAdapter().findHolderById(result);
            // if we are using aysnch methods (Adapter 18) we need to loop and wait till
            // the deletion is done.
            int loop=0;

            int asynchMethodMaxLoops=getAsynchMaxLoops(eu);  
            while (isFireForgetVerficationEnabled(eu) && results!=null && loop <= asynchMethodMaxLoops){
                results = eu.getServerAdapter().findHolderById(result);
                logger.info("Waiting for Asynchronous deletion to finish... attempt: " + loop );
                waitForAsynchMethod(eu);
                loop++;
            }

            if (results != null) {
                throw new AsynchMethodVerificationException(holder.getId());
            }
        } catch (EntityNotFoundException e) {

        }
    }

    public static void verifyAnnuityDelete(IAnnuity annuity, AnnuityType annuityType, AbastractAnnuityExecutionUnit eu, AcmeLogger logger) throws InvalidExecutionUnitParameterException, InvalidArgumentException, ServerInternalErrorException, ServerAdapterCommunicationException, RemoteException, InterruptedException, ExecutionUnitVerificationException{
        if (isFireForgetVerficationDisabled(eu))
            return;

        IAnnuity results=null;
        IAnnuity result = BasicExecutionUnitLibrarry.getAnnuity(
                eu.getAnnuityBeansFactory(), annuityType);
        result.setId(annuity.getId());
        result.setConfiguration(eu.getConfiguration());
        try {
            results = eu.getServerAdapter().findAnnuityById(result);
            // if we are using aysnch methods (Adapter 18) we need to loop and wait till
            // the deletion is done.
            int loop=0;

            int asynchMethodMaxLoops=getAsynchMaxLoops(eu);  
            while (isFireForgetVerficationEnabled(eu) && results!=null && loop <= asynchMethodMaxLoops){
                results = eu.getServerAdapter().findAnnuityById(result);
                logger.info("Waiting for Asynchronous deletion to finish... attempt: " + loop );
                waitForAsynchMethod(eu);
                loop++;
            }

            if (results != null) {
                throw new AsynchMethodVerificationException(annuity.getId());
            }
        } catch (EntityNotFoundException e) {

        }
    }

    private static int getAsynchMaxLoops(AbastractAnnuityExecutionUnit eu){
        try {
            return eu.getParameterValueInt("asynch.method.max.loops");
        } catch (InvalidExecutionUnitParameterException ex1){
            try {
                return Integer.parseInt(eu.getServerAdapter().getConfiguration().getParameterValue("asynch.method.max.loops"));
            }catch (Exception ex2){
                return DEFAULT_MAX_LOOPS;
            }
        }
    }
    
    private static boolean getFireForgetVerification(AbastractAnnuityExecutionUnit eu){
        try {
            return eu.getParameterValueBoolean("fire.and.forget.verify");
        } catch (InvalidExecutionUnitParameterException ex1){
            try {
                return Boolean.parseBoolean(eu.getServerAdapter().getConfiguration().getParameterValue("fire.and.forget.verify"));
            }catch (Exception ex2){
                return true;
            }
        }
    }
    
    private static int getAsynchMethodWait(AbastractAnnuityExecutionUnit eu){
        try {
            return eu.getParameterValueInt("asynch.method.wait");
        } catch (InvalidExecutionUnitParameterException ex1){
            try {
                return Integer.parseInt(eu.getServerAdapter().getConfiguration().getParameterValue("asynch.method.wait"));
            }catch (Exception ex2){
                return DEFAULT_ASYNCH_WAIT;
            }
        }
    }
    
    private static boolean isFireForgetVerficationEnabled(AbastractAnnuityExecutionUnit eu) throws InvalidExecutionUnitParameterException{
        return eu.getServerAdapter().getClass().equals(EJB31AsynchServerAdapter.class) &&
        getFireForgetVerification(eu);
    }

    private static boolean isFireForgetVerficationDisabled(AbastractAnnuityExecutionUnit eu) throws InvalidExecutionUnitParameterException{
        return eu.getServerAdapter().getClass().equals(EJB31AsynchServerAdapter.class) &&
        !getFireForgetVerification(eu);
    }

    private static void waitForAsynchMethod(AbastractAnnuityExecutionUnit eu) throws InterruptedException, InvalidExecutionUnitParameterException {
        if(eu.getServerAdapter().getClass().equals(EJB31AsynchServerAdapter.class)){
            Thread.sleep(getAsynchMethodWait(eu));
        }
    }
    public static class AsynchMethodVerificationException extends ExecutionUnitVerificationException{
        private static final long serialVersionUID = -5980657797922030535L;

        public AsynchMethodVerificationException(String ObjID){
            super("Deletion of Object with ID = "
                    + ObjID
                    + "was not successful! The object still exists in DB.");
        }
    }

    
}

*/