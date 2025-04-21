package com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service;

import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;

public class AnnuityMgmtSvcPojoJAXWSImplPortProxy{

    protected Descriptor _descriptor;

    public class Descriptor {
        private com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl _service = null;
        private com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWS _proxy = null;
        private Dispatch<Source> _dispatch = null;

        public Descriptor() {
            _service = new com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl();
            initCommon();
        }

        public Descriptor(URL wsdlLocation, QName serviceName) {
            _service = new com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWSImpl(wsdlLocation, serviceName);
            initCommon();
        }

        private void initCommon() {
            _proxy = _service.getAnnuityMgmtSvcPojoJAXWSImplPort();
        }

        public com.ibm.wssvt.acme.annuity.common.client.jaxws.pojoimpl.AnnuityMgmtSvcPojoJAXWS getProxy() {
            return _proxy;
        }

        public Dispatch<Source> getDispatch() {
            if(_dispatch == null ) {
                QName portQName = new QName("http://jaxws.client.common.annuity.acme.wssvt.ibm.com/pojoimpl/", "AnnuityMgmtSvcPojoJAXWSImplPort");
                _dispatch = _service.createDispatch(portQName, Source.class, Service.Mode.MESSAGE);

                String proxyEndpointUrl = getEndpoint();
                BindingProvider bp = (BindingProvider) _dispatch;
                String dispatchEndpointUrl = (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
                if(!dispatchEndpointUrl.equals(proxyEndpointUrl))
                    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, proxyEndpointUrl);
            }
            return _dispatch;
        }

        public String getEndpoint() {
            BindingProvider bp = (BindingProvider) _proxy;
            return (String) bp.getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
        }

        public void setEndpoint(String endpointUrl) {
            BindingProvider bp = (BindingProvider) _proxy;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);

            if(_dispatch != null ) {
            bp = (BindingProvider) _dispatch;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
            }
        }
    }

    public AnnuityMgmtSvcPojoJAXWSImplPortProxy() {
        _descriptor = new Descriptor();
    }

    public AnnuityMgmtSvcPojoJAXWSImplPortProxy(URL wsdlLocation, QName serviceName) {
        _descriptor = new Descriptor(wsdlLocation, serviceName);
    }

    public Descriptor _getDescriptor() {
        return _descriptor;
    }

    public Response<CreateAnnuityResponse> createAnnuityAsync(AnnuityValueObject arg0) {
        return _getDescriptor().getProxy().createAnnuityAsync(arg0);
    }

    public Future<?> createAnnuityAsync(AnnuityValueObject arg0, AsyncHandler<CreateAnnuityResponse> asyncHandler) {
        return _getDescriptor().getProxy().createAnnuityAsync(arg0,asyncHandler);
    }

    public AnnuityValueObject createAnnuity(AnnuityValueObject arg0) throws EntityAlreadyExistsException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().createAnnuity(arg0);
    }

    public Response<UpdateAnnuityResponse> updateAnnuityAsync(AnnuityValueObject arg0) {
        return _getDescriptor().getProxy().updateAnnuityAsync(arg0);
    }

    public Future<?> updateAnnuityAsync(AnnuityValueObject arg0, AsyncHandler<UpdateAnnuityResponse> asyncHandler) {
        return _getDescriptor().getProxy().updateAnnuityAsync(arg0,asyncHandler);
    }

    public AnnuityValueObject updateAnnuity(AnnuityValueObject arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().updateAnnuity(arg0);
    }

    public Response<DeleteAnnuityResponse> deleteAnnuityAsync(AnnuityValueObject arg0) {
        return _getDescriptor().getProxy().deleteAnnuityAsync(arg0);
    }

    public Future<?> deleteAnnuityAsync(AnnuityValueObject arg0, AsyncHandler<DeleteAnnuityResponse> asyncHandler) {
        return _getDescriptor().getProxy().deleteAnnuityAsync(arg0,asyncHandler);
    }

    public void deleteAnnuity(AnnuityValueObject arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deleteAnnuity(arg0);
    }

    public Response<CreateAnnuityHolderResponse> createAnnuityHolderAsync(AnnuityHolder arg0) {
        return _getDescriptor().getProxy().createAnnuityHolderAsync(arg0);
    }

    public Future<?> createAnnuityHolderAsync(AnnuityHolder arg0, AsyncHandler<CreateAnnuityHolderResponse> asyncHandler) {
        return _getDescriptor().getProxy().createAnnuityHolderAsync(arg0,asyncHandler);
    }

    public AnnuityHolder createAnnuityHolder(AnnuityHolder arg0) throws EntityAlreadyExistsException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().createAnnuityHolder(arg0);
    }

    public Response<UpdateAnnuityHolderResponse> updateAnnuityHolderAsync(AnnuityHolder arg0) {
        return _getDescriptor().getProxy().updateAnnuityHolderAsync(arg0);
    }

    public Future<?> updateAnnuityHolderAsync(AnnuityHolder arg0, AsyncHandler<UpdateAnnuityHolderResponse> asyncHandler) {
        return _getDescriptor().getProxy().updateAnnuityHolderAsync(arg0,asyncHandler);
    }

    public AnnuityHolder updateAnnuityHolder(AnnuityHolder arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().updateAnnuityHolder(arg0);
    }

    public Response<DeleteAnnuityHolderResponse> deleteAnnuityHolderAsync(AnnuityHolder arg0) {
        return _getDescriptor().getProxy().deleteAnnuityHolderAsync(arg0);
    }

    public Future<?> deleteAnnuityHolderAsync(AnnuityHolder arg0, AsyncHandler<DeleteAnnuityHolderResponse> asyncHandler) {
        return _getDescriptor().getProxy().deleteAnnuityHolderAsync(arg0,asyncHandler);
    }

    public void deleteAnnuityHolder(AnnuityHolder arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deleteAnnuityHolder(arg0);
    }

    public Response<CreatePayorResponse> createPayorAsync(Payor arg0) {
        return _getDescriptor().getProxy().createPayorAsync(arg0);
    }

    public Future<?> createPayorAsync(Payor arg0, AsyncHandler<CreatePayorResponse> asyncHandler) {
        return _getDescriptor().getProxy().createPayorAsync(arg0,asyncHandler);
    }

    public Payor createPayor(Payor arg0) throws EntityAlreadyExistsException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().createPayor(arg0);
    }

    public Response<UpdatePayorResponse> updatePayorAsync(Payor arg0) {
        return _getDescriptor().getProxy().updatePayorAsync(arg0);
    }

    public Future<?> updatePayorAsync(Payor arg0, AsyncHandler<UpdatePayorResponse> asyncHandler) {
        return _getDescriptor().getProxy().updatePayorAsync(arg0,asyncHandler);
    }

    public Payor updatePayor(Payor arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().updatePayor(arg0);
    }

    public Response<DeletePayorResponse> deletePayorAsync(Payor arg0) {
        return _getDescriptor().getProxy().deletePayorAsync(arg0);
    }

    public Future<?> deletePayorAsync(Payor arg0, AsyncHandler<DeletePayorResponse> asyncHandler) {
        return _getDescriptor().getProxy().deletePayorAsync(arg0,asyncHandler);
    }

    public void deletePayor(Payor arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deletePayor(arg0);
    }

    public Response<CreateContactResponse> createContactAsync(Contact arg0) {
        return _getDescriptor().getProxy().createContactAsync(arg0);
    }

    public Future<?> createContactAsync(Contact arg0, AsyncHandler<CreateContactResponse> asyncHandler) {
        return _getDescriptor().getProxy().createContactAsync(arg0,asyncHandler);
    }

    public Contact createContact(Contact arg0) throws EntityAlreadyExistsException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().createContact(arg0);
    }

    public Response<UpdateContactResponse> updateContactAsync(Contact arg0) {
        return _getDescriptor().getProxy().updateContactAsync(arg0);
    }

    public Future<?> updateContactAsync(Contact arg0, AsyncHandler<UpdateContactResponse> asyncHandler) {
        return _getDescriptor().getProxy().updateContactAsync(arg0,asyncHandler);
    }

    public Contact updateContact(Contact arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().updateContact(arg0);
    }

    public Response<DeleteContactResponse> deleteContactAsync(Contact arg0) {
        return _getDescriptor().getProxy().deleteContactAsync(arg0);
    }

    public Future<?> deleteContactAsync(Contact arg0, AsyncHandler<DeleteContactResponse> asyncHandler) {
        return _getDescriptor().getProxy().deleteContactAsync(arg0,asyncHandler);
    }

    public void deleteContact(Contact arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deleteContact(arg0);
    }

    public Response<CreatePayoutResponse> createPayoutAsync(PayoutValueObject arg0) {
        return _getDescriptor().getProxy().createPayoutAsync(arg0);
    }

    public Future<?> createPayoutAsync(PayoutValueObject arg0, AsyncHandler<CreatePayoutResponse> asyncHandler) {
        return _getDescriptor().getProxy().createPayoutAsync(arg0,asyncHandler);
    }

    public PayoutValueObject createPayout(PayoutValueObject arg0) throws EntityAlreadyExistsException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().createPayout(arg0);
    }

    public Response<UpdatePayoutResponse> updatePayoutAsync(PayoutValueObject arg0) {
        return _getDescriptor().getProxy().updatePayoutAsync(arg0);
    }

    public Future<?> updatePayoutAsync(PayoutValueObject arg0, AsyncHandler<UpdatePayoutResponse> asyncHandler) {
        return _getDescriptor().getProxy().updatePayoutAsync(arg0,asyncHandler);
    }

    public PayoutValueObject updatePayout(PayoutValueObject arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().updatePayout(arg0);
    }

    public Response<DeletePayoutResponse> deletePayoutAsync(PayoutValueObject arg0) {
        return _getDescriptor().getProxy().deletePayoutAsync(arg0);
    }

    public Future<?> deletePayoutAsync(PayoutValueObject arg0, AsyncHandler<DeletePayoutResponse> asyncHandler) {
        return _getDescriptor().getProxy().deletePayoutAsync(arg0,asyncHandler);
    }

    public void deletePayout(PayoutValueObject arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deletePayout(arg0);
    }

    public Response<DeleteRiderResponse> deleteRiderAsync(Rider arg0) {
        return _getDescriptor().getProxy().deleteRiderAsync(arg0);
    }

    public Future<?> deleteRiderAsync(Rider arg0, AsyncHandler<DeleteRiderResponse> asyncHandler) {
        return _getDescriptor().getProxy().deleteRiderAsync(arg0,asyncHandler);
    }

    public void deleteRider(Rider arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        _getDescriptor().getProxy().deleteRider(arg0);
    }

    public Response<FindAnnuityByIdResponse> findAnnuityByIdAsync(AnnuityValueObject arg0) {
        return _getDescriptor().getProxy().findAnnuityByIdAsync(arg0);
    }

    public Future<?> findAnnuityByIdAsync(AnnuityValueObject arg0, AsyncHandler<FindAnnuityByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findAnnuityByIdAsync(arg0,asyncHandler);
    }

    public AnnuityValueObject findAnnuityById(AnnuityValueObject arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findAnnuityById(arg0);
    }

    public Response<FindHolderByIdResponse> findHolderByIdAsync(AnnuityHolder arg0) {
        return _getDescriptor().getProxy().findHolderByIdAsync(arg0);
    }

    public Future<?> findHolderByIdAsync(AnnuityHolder arg0, AsyncHandler<FindHolderByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findHolderByIdAsync(arg0,asyncHandler);
    }

    public AnnuityHolder findHolderById(AnnuityHolder arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findHolderById(arg0);
    }

    public Response<FindContactByIdResponse> findContactByIdAsync(Contact arg0) {
        return _getDescriptor().getProxy().findContactByIdAsync(arg0);
    }

    public Future<?> findContactByIdAsync(Contact arg0, AsyncHandler<FindContactByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findContactByIdAsync(arg0,asyncHandler);
    }

    public Contact findContactById(Contact arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findContactById(arg0);
    }

    public Response<FindHolderAnnuitiesResponse> findHolderAnnuitiesAsync(AnnuityHolder arg0) {
        return _getDescriptor().getProxy().findHolderAnnuitiesAsync(arg0);
    }

    public Future<?> findHolderAnnuitiesAsync(AnnuityHolder arg0, AsyncHandler<FindHolderAnnuitiesResponse> asyncHandler) {
        return _getDescriptor().getProxy().findHolderAnnuitiesAsync(arg0,asyncHandler);
    }

    public List<AnnuityValueObject> findHolderAnnuities(AnnuityHolder arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findHolderAnnuities(arg0);
    }

    public Response<FindAnnuityHolderResponse> findAnnuityHolderAsync(AnnuityValueObject arg0) {
        return _getDescriptor().getProxy().findAnnuityHolderAsync(arg0);
    }

    public Future<?> findAnnuityHolderAsync(AnnuityValueObject arg0, AsyncHandler<FindAnnuityHolderResponse> asyncHandler) {
        return _getDescriptor().getProxy().findAnnuityHolderAsync(arg0,asyncHandler);
    }

    public AnnuityHolder findAnnuityHolder(AnnuityValueObject arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findAnnuityHolder(arg0);
    }

    public Response<FindPayorAnnuitiesResponse> findPayorAnnuitiesAsync(Payor arg0) {
        return _getDescriptor().getProxy().findPayorAnnuitiesAsync(arg0);
    }

    public Future<?> findPayorAnnuitiesAsync(Payor arg0, AsyncHandler<FindPayorAnnuitiesResponse> asyncHandler) {
        return _getDescriptor().getProxy().findPayorAnnuitiesAsync(arg0,asyncHandler);
    }

    public List<AnnuityValueObject> findPayorAnnuities(Payor arg0) throws InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findPayorAnnuities(arg0);
    }

    public Response<FindPayorByIdResponse> findPayorByIdAsync(Payor arg0) {
        return _getDescriptor().getProxy().findPayorByIdAsync(arg0);
    }

    public Future<?> findPayorByIdAsync(Payor arg0, AsyncHandler<FindPayorByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findPayorByIdAsync(arg0,asyncHandler);
    }

    public Payor findPayorById(Payor arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findPayorById(arg0);
    }

    public Response<FindPayoutByIdResponse> findPayoutByIdAsync(PayoutValueObject arg0) {
        return _getDescriptor().getProxy().findPayoutByIdAsync(arg0);
    }

    public Future<?> findPayoutByIdAsync(PayoutValueObject arg0, AsyncHandler<FindPayoutByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findPayoutByIdAsync(arg0,asyncHandler);
    }

    public PayoutValueObject findPayoutById(PayoutValueObject arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findPayoutById(arg0);
    }

    public Response<FindRiderByIdResponse> findRiderByIdAsync(Rider arg0) {
        return _getDescriptor().getProxy().findRiderByIdAsync(arg0);
    }

    public Future<?> findRiderByIdAsync(Rider arg0, AsyncHandler<FindRiderByIdResponse> asyncHandler) {
        return _getDescriptor().getProxy().findRiderByIdAsync(arg0,asyncHandler);
    }

    public Rider findRiderById(Rider arg0) throws EntityNotFoundException_Exception, InvalidArgumentException_Exception, ServerBusinessModuleException_Exception, ServerPersistenceModuleException_Exception {
        return _getDescriptor().getProxy().findRiderById(arg0);
    }

}