package com.ibm.wssvt.acme.annuity.common.servicelookup.ejb31;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.business.ejb31.AnnuityMgmtAsynchSvcEJB31Local;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.InvalidConfigurationException;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import com.ibm.wssvt.acme.common.util.StringUtils;

public class AnnuityEJB31AsynchLocalServiceProxy implements IAnnuityService {

    private AnnuityMgmtAsynchSvcEJB31Local local;
    private AcmeLogger logger;

    private final long DEFAULT_FIRE_FORGET_WAIT = 2000;
    private final long DEFAULT_ASYNCH_METHOD_WAIT_COUNT = 100;
    private final long DEFAULT_ASYNCH_WAIT_VALUE = 1;
    private static final String FIRE_FORGET_VERIFY_ENABLED_KEY = "fireAndForgetVerify";
    private static final String FIRE_FORGET_VERIFY_WAIT_KEY = "fireAndForgetWait";
    private static final String ASYNCH_METHOD_WAIT = "asynchMethodWait";
    private static final String ASYNCH_METHOD_WAIT_COUNT = "asynchMethodWaitCount";
    private static final String ASYNCH_TIMEOUT_VALUE = "asynchTimeoutValue";
    int remoteAsynchTimeout = 10;

    public AnnuityEJB31AsynchLocalServiceProxy(
            AnnuityMgmtAsynchSvcEJB31Local local) {
        super();
        this.local = local;
    }

    public IAnnuity findAnnuityById(IAnnuity annuity)
    throws EntityNotFoundException, ServerInternalErrorException,
    InvalidArgumentException {
        Future<IAnnuity> future = local.findAnnuityById(annuity);
        getAsyncTimout(annuity);
        try {
            return future.get(remoteAsynchTimeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        } catch (TimeoutException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IAnnuityHolder findAnnuityHolder(IAnnuity annuity)
    throws EntityNotFoundException, ServerInternalErrorException,
    InvalidArgumentException {
        Future<IAnnuityHolder> future = local.findAnnuityHolder(annuity);
        getAsyncTimout(annuity);
        try {
            return future.get(remoteAsynchTimeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        } catch (TimeoutException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IContact findContactById(IContact contact)
    throws EntityNotFoundException, ServerInternalErrorException,
    InvalidArgumentException {
        Future<IContact> future = local.findContactById(contact);
        getAsyncTimout(contact);
        try {
            return future.get(remoteAsynchTimeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        } catch (TimeoutException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public List<IAnnuity> findHolderAnnuities(IAnnuityHolder annuityHolder)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<List<IAnnuity>> future = local
        .findHolderAnnuities(annuityHolder);
        getAsyncTimout(annuityHolder);
        try {
            return future.get(remoteAsynchTimeout, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        } catch (TimeoutException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IAnnuityHolder findHolderById(IAnnuityHolder annuityHolder)
    throws EntityNotFoundException, ServerInternalErrorException,
    InvalidArgumentException {
        Future<IAnnuityHolder> future = local.findHolderById(annuityHolder);
        getAsyncTimout(annuityHolder);
        try {
            return future.get(remoteAsynchTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        } catch (TimeoutException e) {
            throw new ServerInternalErrorException(e);

        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        }

    }

    public IPayor findPayorById(IPayor payor) throws EntityNotFoundException,
    ServerInternalErrorException, InvalidArgumentException {
        Future<IPayor> future = local.findPayorById(payor);
        try {
            waitAndCheckForValidResponse(future, payor);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IPayout findPayoutById(IPayout payout)
    throws EntityNotFoundException, ServerInternalErrorException,
    InvalidArgumentException {
        Future<IPayout> future = local.findPayoutById(payout);
        try {
            waitAndCheckForValidResponse(future, payout);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IRider findRiderById(IRider rider) throws EntityNotFoundException,
    ServerInternalErrorException, InvalidArgumentException {
        Future<IRider> future = local.findRiderById(rider);
        try {
            waitAndCheckForValidResponse(future, rider);

            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityNotFoundException)
                throw (EntityNotFoundException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IAnnuity createAnnuity(IAnnuity ann)
    throws ServerInternalErrorException, EntityAlreadyExistsException,
    InvalidArgumentException {
        Future<IAnnuity> future = local.createAnnuity(ann);
        try {
            waitAndCheckForValidResponse(future, ann);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityAlreadyExistsException)
                throw (EntityAlreadyExistsException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }

    }

    public IAnnuityHolder createAnnuityHolder(IAnnuityHolder annHolder)
    throws ServerInternalErrorException, EntityAlreadyExistsException,
    InvalidArgumentException {
        Future<IAnnuityHolder> future = local.createAnnuityHolder(annHolder);
        try {
            waitAndCheckForValidResponse(future, annHolder);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityAlreadyExistsException)
                throw (EntityAlreadyExistsException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IContact createContact(IContact contact)
    throws ServerInternalErrorException, EntityAlreadyExistsException,
    InvalidArgumentException {
        Future<IContact> future = local.createContact(contact);
        try {
            waitAndCheckForValidResponse(future, contact);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityAlreadyExistsException)
                throw (EntityAlreadyExistsException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IPayor createPayor(IPayor payor)
    throws ServerInternalErrorException, EntityAlreadyExistsException,
    InvalidArgumentException {
        Future<IPayor> future = local.createPayor(payor);
        try {
            waitAndCheckForValidResponse(future, payor);
            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityAlreadyExistsException)
                throw (EntityAlreadyExistsException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public void deleteAnnuity(IAnnuity annuity)
    throws ServerInternalErrorException, InvalidArgumentException {
        local.deleteAnnuity(annuity);
        waitForDelete(annuity);
    }

    public void deleteAnnuityHolder(IAnnuityHolder annHolder)
    throws ServerInternalErrorException, InvalidArgumentException {
        local.deleteAnnuityHolder(annHolder);
        waitForDelete(annHolder);
    }

    public void deleteContact(IContact contact)
    throws ServerInternalErrorException, InvalidArgumentException {
        local.deleteContact(contact);
        waitForDelete(contact);
    }

    public void deletePayor(IPayor payor) throws ServerInternalErrorException,
    InvalidArgumentException {
        local.deletePayor(payor);
        waitForDelete(payor);
    }

    public void deletePayout(IPayout payout)
    throws ServerInternalErrorException, InvalidArgumentException {
        local.deletePayout(payout);
        waitForDelete(payout);
    }

    public void deleteRider(IRider rider) throws ServerInternalErrorException,
    InvalidArgumentException {
        local.deleteRider(rider);
        waitForDelete(rider);
    }

    public IAnnuity updateAnnuity(IAnnuity ann)
    throws ServerInternalErrorException, InvalidArgumentException {

        Future<IAnnuity> future = local.updateAnnuity(ann);
        try {
            waitAndCheckForValidResponse(future, ann);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IAnnuityHolder updateAnnuityHolder(IAnnuityHolder annHolder)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<IAnnuityHolder> future = local.updateAnnuityHolder(annHolder);
        try {
            waitAndCheckForValidResponse(future, annHolder);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IContact updateContact(IContact contact)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<IContact> future = local.updateContact(contact);
        try {
            waitAndCheckForValidResponse(future, contact);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IPayor updatePayor(IPayor payor)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<IPayor> future = local.updatePayor(payor);
        try {
            waitAndCheckForValidResponse(future, payor);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public List<IAnnuity> findPayorAnnuities(IPayor payor)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<List<IAnnuity>> future = local.findPayorAnnuities(payor);
        try {
            waitAndCheckForValidResponse(future, payor);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IPayout createPayout(IPayout payout)
    throws ServerInternalErrorException, EntityAlreadyExistsException,
    InvalidArgumentException {
        Future<IPayout> future = local.createPayout(payout);
        try {
            waitAndCheckForValidResponse(future, payout);

            return future.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof EntityAlreadyExistsException)
                throw (EntityAlreadyExistsException) e.getCause();
            else
                throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    public IPayout updatePayout(IPayout payout)
    throws ServerInternalErrorException, InvalidArgumentException {
        Future<IPayout> future = local.updatePayout(payout);
        try {
            waitAndCheckForValidResponse(future, payout);

            return future.get();
        } catch (ExecutionException e) {
            throw new ServerInternalErrorException(e);
        } catch (InterruptedException e) {
            throw new ServerInternalErrorException(e);
        }
    }

    private void waitForDelete(Configrable<String, String> configrable)
    throws ServerInternalErrorException {
        if (isFireForgetVerificationEnabled(configrable)) {
            long waitTime = getFireForgetWaitTime(configrable);
            try {
                Thread.sleep(waitTime);
                logger.fine("Waiting for Fire and Forget delete...");
            } catch (InterruptedException e) {
                throw new ServerInternalErrorException(e);
            }
        }
    }

    private long getFireForgetWaitTime(Configrable<String, String> configrable) {
        long waitTime;
        try {
            if (configrable.getConfiguration().getParameterValue(
                    FIRE_FORGET_VERIFY_WAIT_KEY) == null)
                throw new InvalidArgumentException();
            if (Long.parseLong(configrable.getConfiguration()
                    .getParameterValue(FIRE_FORGET_VERIFY_WAIT_KEY)) >= 0)
                waitTime = Long.parseLong(configrable.getConfiguration()
                        .getParameterValue(FIRE_FORGET_VERIFY_WAIT_KEY));
            else
                throw new InvalidArgumentException();
        } catch (Exception e) {
            logger.info("Invalid Configuration. The parameter "
                    + FIRE_FORGET_VERIFY_WAIT_KEY
                    + " is not a valid value or not specified.");
            logger.info("Will be using the default value for"
                    + FIRE_FORGET_VERIFY_WAIT_KEY + ": "
                    + DEFAULT_FIRE_FORGET_WAIT);
            waitTime = DEFAULT_FIRE_FORGET_WAIT;
        }
        return waitTime;
    }

    private boolean isFireForgetVerificationEnabled(
            Configrable<String, String> configrable) {
        try {
            if (configrable.getConfiguration().getParameterValue(
                    FIRE_FORGET_VERIFY_ENABLED_KEY) == null)
                throw new InvalidArgumentException();
            return Boolean.parseBoolean(configrable.getConfiguration()
                    .getParameterValue(FIRE_FORGET_VERIFY_ENABLED_KEY));
        } catch (Exception ex) {
            logger.info("Invalid Configuration.  The parameter "
                    + FIRE_FORGET_VERIFY_ENABLED_KEY
                    + " is not a valid value or not specified.");
            logger.info("Will be using the default value for"
                    + FIRE_FORGET_VERIFY_ENABLED_KEY + true);
            return true;
        }
    }

    private void waitForResponse(Future<?> future,
            Configrable<String, String> configrable)
    throws ServerInternalErrorException, InterruptedException {
        long cntr = 0;
        Long waitTime = DEFAULT_ASYNCH_WAIT_VALUE;
        Long waitCount = DEFAULT_ASYNCH_METHOD_WAIT_COUNT;
        if(configrable!=null && configrable.getConfiguration()!= null){

            try {

                if(configrable.getConfiguration().getParameterValue(ASYNCH_METHOD_WAIT) != null){
                    waitTime = Long.parseLong(configrable.getConfiguration()
                            .getParameterValue(ASYNCH_METHOD_WAIT));
                    if(waitTime <= 0){
                        waitTime = DEFAULT_ASYNCH_METHOD_WAIT_COUNT;
                        throw new InvalidConfigurationException();
                    }
                }
                if(configrable.getConfiguration().getParameterValue(ASYNCH_METHOD_WAIT_COUNT) != null){
                    waitCount = Long.parseLong(configrable.getConfiguration()
                            .getParameterValue(ASYNCH_METHOD_WAIT_COUNT));
                    if(waitCount <=0){
                        waitCount = DEFAULT_ASYNCH_METHOD_WAIT_COUNT;
                        throw new InvalidConfigurationException();
                    }
                }
            } catch (Exception e) {
                logger.fine("Invalid Configuration.  "
                        + ASYNCH_METHOD_WAIT
                        + " or "
                        + ASYNCH_METHOD_WAIT_COUNT
                        + " are not valid values."
                        + "current values are: "
                        + ASYNCH_METHOD_WAIT
                        + ": "
                        + configrable.getConfiguration().getParameterValue(
                                ASYNCH_METHOD_WAIT)
                                + ASYNCH_METHOD_WAIT_COUNT
                                + ": "
                                + configrable.getConfiguration().getParameterValue(
                                        ASYNCH_METHOD_WAIT_COUNT));

            }
            while (cntr <= waitCount) {
                if (future.isDone()) {
                    return;
                } else {
                    logger.fine("waiting for Asynchronous work ... wait count: "
                            + cntr);
                    cntr++;
                    Thread.sleep(waitTime);
                }
            }
        } else {
            logger.info ("Object: " + configrable + " or it's configration is null!");
        }
    }

    private void waitAndCheckForValidResponse(Future<?> future,
            Configrable<String, String> configrable)
    throws ServerInternalErrorException, InterruptedException,
    ExecutionException {

        waitForResponse(future, configrable);

        if (future.isDone()) {
            if (future.get() == null) {
                throw new ServerInternalErrorException(
                        "The server responded with a null object.  "
                        + "Expected either an error or a non-null object.");
            }
        } else {
            throw new ServerInternalErrorException(
                    "The server did not respond in in the expected time."
                    + "wait values are:"
                    + ASYNCH_METHOD_WAIT
                    + ":"
                    + configrable.getConfiguration().getParameterValue(
                            ASYNCH_METHOD_WAIT)
                            + " and "
                            + ASYNCH_METHOD_WAIT_COUNT
                            + ": "
                            + configrable.getConfiguration().getParameterValue(
                                    ASYNCH_METHOD_WAIT_COUNT)
                                    + " was waiting for Future object: "
                                    + future.getClass().getName());

        }

    }

    private void getAsyncTimout(Configrable<String, String> configrable) {
        String asynchTimeoutStringValue = (String) configrable
        .getConfiguration().getParameterValue(ASYNCH_TIMEOUT_VALUE);
        if (asynchTimeoutStringValue != null
                && asynchTimeoutStringValue.trim().length() > 0) {
            remoteAsynchTimeout = StringUtils.toInt(asynchTimeoutStringValue);
            logger.fine("Setting remoteAsynchTimout = " + remoteAsynchTimeout);
        }
    }


}