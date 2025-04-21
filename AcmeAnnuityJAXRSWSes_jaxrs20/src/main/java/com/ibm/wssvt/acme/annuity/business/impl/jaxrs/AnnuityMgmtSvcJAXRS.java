package com.ibm.wssvt.acme.annuity.business.impl.jaxrs;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.model.atom.AtomContent;
import org.apache.wink.common.model.atom.AtomEntry;
import org.apache.wink.common.model.wadl.WADLGenerator;

import com.ibm.wssvt.acme.annuity.common.bean.IAnnuity;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.bean.IPayor;
import com.ibm.wssvt.acme.annuity.common.bean.IPayout;
import com.ibm.wssvt.acme.annuity.common.bean.IRider;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Annuity;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityJAXRSReturn;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.ListOfObjects;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payout;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Rider;
import com.ibm.wssvt.acme.annuity.common.business.AnnuityBusinessServiceLookup;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.EntityAlreadyExistsException;
import com.ibm.wssvt.acme.annuity.common.exception.EntityNotFoundException;
import com.ibm.wssvt.acme.annuity.common.exception.InvalidArgumentException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.annuity.common.exception.ServerInternalErrorException;
import com.ibm.wssvt.acme.annuity.common.util.AnnuityLoggerFactory;
import com.ibm.wssvt.acme.common.bean.Configrable;
import com.ibm.wssvt.acme.common.exception.ExceptionFormatter;
import com.ibm.wssvt.acme.common.log.AcmeLogger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@Path(value = "/jaxrs20")
@RolesAllowed({"users","admins"})
//@RolesAllowed("users, admins")

public class AnnuityMgmtSvcJAXRS {
	
	@Context
	private UriInfo uriInfo;	
	
	@Path(value ="/annuity")
	@POST
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response createAnnuity(AnnuityValueObject annuityVO){
		AnnuityJAXRSReturn retData= null;
		try {
			IAnnuity annuity = annuityVO.getAnnuity();
			AcmeLogger logger = getLogger(annuity);
			logger.fine("create annuity.  AnnuityVO: " + annuityVO + " annuity: " + annuity);
			IAnnuity result = getAnnuityService(annuity).createAnnuity(annuity);
			AnnuityValueObject avo = new AnnuityValueObject();
			avo.setAnnuity(result);
			logger.fine("create annuity - ready to return. result annuityVO: " + avo );
			retData = objectToReturn(avo);
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@Path(value="/annuity")
	@POST
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response createAnnuity(AtomEntry atomEntry) {
		// unwrap AtomEntry and call business logic
		Response response = createAnnuity(atomEntry.getContent().getValue(AnnuityValueObject.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}

	@Path(value ="/annuity")
	@PUT 
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})	
	public Response updateAnnuity(AnnuityValueObject annuityVO){
		AnnuityJAXRSReturn retData= null;
		try {
			IAnnuity annuity = annuityVO.getAnnuity();
			AcmeLogger logger = getLogger(annuity);
			logger.fine("update Annuity call");
			IAnnuity result = getAnnuityService(annuity).updateAnnuity(annuity);			
			AnnuityValueObject avo = new AnnuityValueObject();
			avo.setAnnuity(result);
			logger.fine("update Annuity call ready to return: " + avo);
			retData = objectToReturn(avo);		
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();		
	}
	
	@Path(value="/annuity")
	@PUT
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	public Response updateAnnuity(AtomEntry atomEntry) {
		// unwrap AtomEntry and call business logic
		Response response = updateAnnuity(atomEntry.getContent().getValue(AnnuityValueObject.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@Path(value ="/annuity/{annuityId}")
	@GET
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findAnnuityById(@PathParam(value="annuityId") String annuityId){
		AnnuityJAXRSReturn retData= null;
		try {			
			IAnnuity annuity = new Annuity();
			annuity.setId(annuityId);
			addParams(annuity, uriInfo.getQueryParameters());
			AcmeLogger logger = getLogger(annuity);
			logger.fine("find Annuity by id call. id: " + annuityId);
			IAnnuity result = getAnnuityService(annuity).findAnnuityById(annuity);
			AnnuityValueObject avo = new AnnuityValueObject();
			avo.setAnnuity(result);
			logger.fine("ready to return find annuity by id");
			retData = objectToReturn(avo);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();		
	}
	
	@Path(value ="/annuity/{annuityId}")
	@GET
	@Produces("application/atom+xml")
	@PermitAll
	public Response findAnnuityByIdAtom(@PathParam(value="annuityId") String annuityId){
		// call business logic
		Response response = findAnnuityById(annuityId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@Path(value ="/annuity/{annuityId}")
	@DELETE
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response deleteAnnuity(@PathParam(value="annuityId") String annuityId){
		AnnuityJAXRSReturn retData= new AnnuityJAXRSReturn();
		try {			
			IAnnuity annuity = new Annuity();
			annuity.setId(annuityId);
			addParams(annuity, uriInfo.getQueryParameters());
			AcmeLogger logger = getLogger(annuity);
			logger.fine("delete Annuity call. id: " + annuityId);
			getAnnuityService(annuity).deleteAnnuity(annuity);				
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}
		return Response.status(Status.OK).entity(retData).build();		
	} 
	
	@Path(value ="/annuity/{annuityId}")
	@DELETE
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deleteAnnuityAtom(@PathParam(value="annuityId") String annuityId){
		// call business logic
		Response response = deleteAnnuity(annuityId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}

	@Path(value ="/annuity/holder/{holderId}")
	@GET
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findHolderAnnuities(@PathParam(value="holderId") String holderId){
		AnnuityJAXRSReturn retData= null;
		try {			
			IAnnuityHolder holder = new AnnuityHolder();
			holder.setId(holderId);
			addParams(holder, uriInfo.getQueryParameters());
			AcmeLogger logger = getLogger(holder);
			logger.fine("find holder annuities. id: " + holderId);
			List<IAnnuity> annuities = getAnnuityService(holder).findHolderAnnuities(holder);		
			ListOfObjects jaxbList = new ListOfObjects();
			if (annuities != null) {
				for (IAnnuity ann : annuities){
					AnnuityValueObject avo = new AnnuityValueObject();
					avo.setAnnuity(ann);
					jaxbList.getObjects().add(avo);
				}
			}
			retData = objectToReturn(jaxbList);		
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}
		return Response.status(Status.OK).entity(retData).build();		
	}
	
	@Path(value ="/annuity/holder/{holderId}")
	@GET
	@Produces("application/atom+xml")
	@PermitAll
	public Response findHolderAnnuitiesAtom(@PathParam(value="holderId") String holderId){
		// call business logic
		Response response = findHolderAnnuities(holderId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}

	@Path(value ="/annuity/payor/{payorId}")
	@GET
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findPayorAnnuities(@PathParam(value="payorId") String payorId){
		AnnuityJAXRSReturn retData= null;
		try {			
			IPayor payor= new Payor();
			payor.setId(payorId);
			addParams(payor, uriInfo.getQueryParameters());
			AcmeLogger logger = getLogger(payor);
			logger.fine("find payor annuities. id: " + payorId);
			List<IAnnuity> annuities = getAnnuityService(payor).findPayorAnnuities(payor);
			ListOfObjects jaxbList = new ListOfObjects();
			if (annuities != null) {
				for (IAnnuity ann : annuities){
					AnnuityValueObject avo = new AnnuityValueObject();
					avo.setAnnuity(ann);
					jaxbList.getObjects().add(avo);
				}
			}
			retData = objectToReturn(jaxbList);		
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}
		return Response.status(Status.OK).entity(retData).build();		
	}
	
	@Path(value ="/annuity/payor/{payorId}")
	@GET
	@Produces("application/atom+xml")
	@PermitAll
	public Response findPayorAnnuitiesAtom(@PathParam(value="payorId") String payorId){
		// call business logic
		Response response = findPayorAnnuities(payorId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@POST 
	@Path(value = "/annuityholder")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response createAnnuityHolder(AnnuityHolder annuityHolder){
		AnnuityJAXRSReturn retData= null;
		try {
			AcmeLogger logger = getLogger(annuityHolder);
			logger.fine("create annuity holder call. id: " + annuityHolder.getId());
			IAnnuityHolder result = getAnnuityService(annuityHolder).createAnnuityHolder(annuityHolder);
			retData = objectToReturn(result);
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@POST 
	@Path(value = "/annuityholder")
	@Consumes(value ={"application/atom+xml"})
	@Produces(value ={"application/atom+xml"})
	@RolesAllowed("admins")
	public Response createAnnuityHolder(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = createAnnuityHolder(atomEntry.getContent().getValue(AnnuityHolder.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@PUT
	@Path(value = "/annuityholder")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public Response updateAnnuityHolder(AnnuityHolder annuityHolder){
		AnnuityJAXRSReturn retData= null;
		try {
			AcmeLogger logger = getLogger(annuityHolder);
			logger.fine("update annuity holder call. id: " + annuityHolder.getId());
			IAnnuityHolder result = getAnnuityService(annuityHolder).updateAnnuityHolder(annuityHolder);
			retData = objectToReturn(result);	
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@PUT
	@Path(value = "/annuityholder")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	public Response updateAnnuityHolder(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = updateAnnuityHolder(atomEntry.getContent().getValue(AnnuityHolder.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/annuityholder/{holderId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findHolderById(@PathParam(value="holderId") String holderId){
		AnnuityJAXRSReturn retData = null;		
		AnnuityHolder annuityHolder = new AnnuityHolder();
		annuityHolder.setId(holderId);
		addParams(annuityHolder, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(annuityHolder);
			logger.fine("find annuity holder call. id: " + annuityHolder.getId());
			IAnnuityHolder result = getAnnuityService(annuityHolder).findHolderById(annuityHolder);
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/annuityholder/{holderId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findHolderByIdAtom(@PathParam(value="holderId") String holderId){
		// call business logic
		Response response = findHolderById(holderId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/annuityholder/annuity/{annuityId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findAnnuityHolder(@PathParam(value="annuityId") String annuityId){
		AnnuityJAXRSReturn retData = null;		
		Annuity annuity = new Annuity();
		annuity.setId(annuityId);
		addParams(annuity, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(annuity);
			logger.fine("find holder from annuity id call. id: " + annuity.getId());
			IAnnuityHolder result = getAnnuityService(annuity).findAnnuityHolder(annuity);
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/annuityholder/annuity/{annuityId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findAnnuityHolderAtom(@PathParam(value="annuityId") String annuityId){
		// call business logic
		Response response = findAnnuityHolder(annuityId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@DELETE 
	@Path(value ="/annuityholder/{holderId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response deleteHolder(@PathParam(value="holderId") String holderId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();		
		AnnuityHolder holder = new AnnuityHolder();
		holder.setId(holderId);
		addParams(holder, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(holder);
			logger.fine("delete holder call. id: " + holder.getId());
			getAnnuityService(holder).deleteAnnuityHolder(holder);					
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@DELETE 
	@Path(value ="/annuityholder/{holderId}")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deleteHolderAtom(@PathParam(value="holderId") String holderId){
		// call business logic
		Response response = deleteHolder(holderId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@POST 
	@Path(value = "/payor")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response createPayor(Payor payor){
		AnnuityJAXRSReturn retData= null;
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("create payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).createPayor(payor);
			retData = objectToReturn(result);
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@POST 
	@Path(value = "/payor")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response createPayor(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = createPayor(atomEntry.getContent().getValue(Payor.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@PUT
	@Path(value = "/payor")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public Response updatePayor(Payor payor){
		AnnuityJAXRSReturn retData= null;
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("update payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).updatePayor(payor);
			retData = objectToReturn(result);	
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@PUT
	@Path(value = "/payor")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	public Response updatePayor(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = updatePayor(atomEntry.getContent().getValue(Payor.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/payor/{payorId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findPayorById(@PathParam(value="payorId") String payorId){
		AnnuityJAXRSReturn retData = null;		
		Payor payor= new Payor();
		payor.setId(payorId);
		addParams(payor, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("find payor call. id: " + payor.getId());
			IPayor result = getAnnuityService(payor).findPayorById(payor);
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/payor/{payorId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findPayorByIdAtom(@PathParam(value="payorId") String payorId){
		// call business logic
		Response response = findPayorById(payorId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@DELETE 
	@Path(value ="/payor/{payorId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response deletePayor(@PathParam(value="payorId") String payorId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();		
		Payor payor= new Payor();
		payor.setId(payorId);
		addParams(payor, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payor);
			logger.fine("delete payor call. id: " + payor.getId());
			getAnnuityService(payor).deletePayor(payor);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@DELETE 
	@Path(value ="/payor/{payorId}")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deletePayorAtom(@PathParam(value="payorId") String payorId){
		// call business logic
		Response response = deletePayor(payorId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@POST 
	@Path(value = "/payout")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response createPayout(PayoutValueObject payoutVO){
		AnnuityJAXRSReturn retData= null;
		try {
			IPayout payout = payoutVO.getPayout();
			AcmeLogger logger = getLogger(payout);
			logger.fine("create payout call. id: " + payout.getId());
			IPayout result = getAnnuityService(payout).createPayout(payout);
			PayoutValueObject pvo = new PayoutValueObject();
			pvo.setPayout(result);
			retData = objectToReturn(pvo);
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@POST 
	@Path(value = "/payout")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response createPayout(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = createPayout(atomEntry.getContent().getValue(PayoutValueObject.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@PUT
	@Path(value = "/payout")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public Response updatePayout(PayoutValueObject payoutVO){
		AnnuityJAXRSReturn retData= null;
		try {
			IPayout payout = payoutVO.getPayout();			
			AcmeLogger logger = getLogger(payout);
			logger.fine("update payout call. id: " + payout.getId());
			IPayout result = getAnnuityService(payout).updatePayout(payout);
			PayoutValueObject  pvo = new PayoutValueObject();
			pvo.setPayout(result);
			retData = objectToReturn(pvo);	
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@PUT
	@Path(value = "/payout")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	public Response updatePayout(AtomEntry atomEntry){				
		// unwrap AtomEntry and call business logic
		Response response = updatePayout(atomEntry.getContent().getValue(PayoutValueObject.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/payout/{payoutId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findPayoutById(@PathParam(value="payoutId") String payoutId){
		AnnuityJAXRSReturn retData = null;		
		Payout payout= new Payout();
		payout.setId(payoutId);
		addParams(payout, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payout);
			logger.fine("find payout call. id: " + payout.getId());
			IPayout result = getAnnuityService(payout).findPayoutById(payout);
			PayoutValueObject pvo = new PayoutValueObject();
			pvo.setPayout(result);
			retData = objectToReturn(pvo);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/payout/{payoutId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findPayoutByIdAtom(@PathParam(value="payoutId") String payoutId){
		// call business logic
		Response response = findPayoutById(payoutId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@DELETE 
	@Path(value ="/payout/{payoutId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response deletePayout(@PathParam(value="payoutId") String payoutId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();		
		Payout payout= new Payout();
		payout.setId(payoutId);
		addParams(payout, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(payout);
			logger.fine("delete payout call. id: " + payout.getId());
			getAnnuityService(payout).deletePayout(payout);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@DELETE 
	@Path(value ="/payout/{payoutId}")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deletePayoutAtom(@PathParam(value="payoutId") String payoutId){
		// call business logic
		Response response = deletePayout(payoutId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/rider/{riderId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findRiderById(@PathParam(value="riderId") String riderId){
		AnnuityJAXRSReturn retData = null;		
		Rider rider = new Rider();
		rider.setId(riderId);
		addParams(rider, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(rider);
			logger.fine("find rider call. id: " + rider.getId());
			IRider result = getAnnuityService(rider).findRiderById(rider);
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/rider/{riderId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findRiderByIdAtom(@PathParam(value="riderId") String riderId){
		// call business logic
		Response response = findRiderById(riderId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@DELETE 
	@Path(value ="/rider/{riderId}")
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response deleteRider(@PathParam(value="riderId") String riderId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();		
		Rider rider= new Rider();
		rider.setId(riderId);
		addParams(rider, uriInfo.getQueryParameters());		
		try {
			AcmeLogger logger = getLogger(rider);
			logger.fine("delete rider call. id: " + rider.getId());
			getAnnuityService(rider).deleteRider(rider);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@DELETE 
	@Path(value ="/rider/{riderId}")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deleteRiderAtom(@PathParam(value="riderId") String riderId){
		// call business logic
		Response response = deleteRider(riderId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@POST 
	@Path(value = "/contact")
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	@RolesAllowed("admins")
	public Response createContact(Contact contact){
		
		AnnuityJAXRSReturn retData= null;
		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("create contact call. id: " + contact.getId());
	
			IContact result = getAnnuityService(contact).createContact(contact);
			logger.fine("contact id " + contact.getId() + " created");
			retData = objectToReturn(result);
			logger.fine("Tam debug - return data ....");
			
		} catch (EntityAlreadyExistsException e) {
			retData = exceptionToReturn(e);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}
		
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@POST 
	@Path(value = "/contact")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response createContact(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = createContact(atomEntry.getContent().getValue(Contact.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}

	@PUT
	@Path(value = "/contact")	
	@Consumes(value ={"application/xml", "application/json"})
	@Produces(value ={"application/xml", "application/json"})
	public Response updateContact(Contact contact){
		AnnuityJAXRSReturn retData= null;

		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("update contact call. id: " + contact.getId());
			IContact result = getAnnuityService(contact).updateContact(contact);
			retData = objectToReturn(result);		
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}		
		
		return Response.status(Status.OK).entity(retData).build();
		
	}
	
	@PUT 
	@Path(value = "/contact")
	@Consumes("application/atom+xml")
	@Produces("application/atom+xml")
	public Response updateContact(AtomEntry atomEntry){
		// unwrap AtomEntry and call business logic
		Response response = updateContact(atomEntry.getContent().getValue(Contact.class));
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@DELETE 
	@Path(value ="/contact/{contactId}")
	@Produces(value ={"application/xml", "application/json"})	
	@RolesAllowed("admins")
	public Response deleteContact(@PathParam(value="contactId") String contactId){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();

		MultivaluedMap<String, String> qparams = uriInfo.getQueryParameters();
		
		Contact contact = new Contact();
		contact.setId(contactId);
		addParams(contact, qparams);		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("delete contact call. id: " + contact.getId());
			getAnnuityService(contact).deleteContact(contact);	
			logger.fine("contact deleted!");
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		}	
		return Response.status(Status.OK).entity(retData).build();				
	}
	
	@DELETE 
	@Path(value ="/contact/{contactId}")
	@Produces("application/atom+xml")
	@RolesAllowed("admins")
	public Response deleteContactAtom(@PathParam(value="contactId") String contactId){

		// call business logic
		Response response = deleteContact(contactId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
	
	@GET 
	@Path(value ="/contact/{contactId}")
	@Produces(value ={"application/xml", "application/json"})
	@PermitAll
	public Response findContactById(@PathParam(value="contactId") String contactId){
		AnnuityJAXRSReturn retData = null;
		MultivaluedMap<String, String> qparams = uriInfo.getQueryParameters();
		
		Contact contact = new Contact();
		contact.setId(contactId);
		addParams(contact, qparams);		
		try {
			AcmeLogger logger = getLogger(contact);
			logger.fine("find contact call. id: " + contact.getId());	
			IContact result = getAnnuityService(contact).findContactById(contact);
			logger.fine("Found contact " +result.getId());
			retData = objectToReturn(result);
		} catch (InvalidArgumentException e) {
			retData = exceptionToReturn(e);
		} catch (ServerBusinessModuleException e) {
			retData = exceptionToReturn(e);
		} catch (ServerInternalErrorException e) {
			retData = exceptionToReturn(e);
		} catch (EntityNotFoundException e) {
			retData = exceptionToReturn(e);			
		}					
		return Response.status(Status.OK).entity(retData).build();
	}
	
	@GET 
	@Path(value ="/contact/{contactId}")
	@Produces("application/atom+xml")
	@PermitAll
	public Response findContactByIdAtom(@PathParam(value="contactId") String contactId){
		// call business logic
		Response response = findContactById(contactId);
		AnnuityJAXRSReturn annuityJAXRSReturn = (AnnuityJAXRSReturn)response.getEntity();
		// wrap in AtomEntry
		return Response.status(Status.OK).entity(wrapInAtom(annuityJAXRSReturn)).build();
	}
		
	
	@GET
	@Path(value = "/test")
	@Produces(MediaType.TEXT_PLAIN)
	public Response testService(){
		String msg = "Annnuity JAXRS Service.  Service is ready.";
		System.out.println(msg);
		return Response.status(Status.OK).entity(msg).build();
	}

	private void addParams(Configrable<String, String> configrable, MultivaluedMap<String, String> qparams){		
		if (qparams != null){
			for (String key: qparams.keySet()){
				String value = qparams.getFirst(key);
				configrable.getConfiguration().addParameter(key, value);				
			}
		}
		AcmeLogger logger = getLogger(configrable);
		logger.fine("params are: " + configrable.getConfiguration().getParameters());
	}
	
	private AnnuityJAXRSReturn objectToReturn(Object o){		
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();
		retData.setReturnObjectClass(o.getClass().getName());
		retData.setReturnObject(o);
		return retData;
	}
	private AnnuityJAXRSReturn exceptionToReturn(Exception e){
		AnnuityJAXRSReturn retData = new AnnuityJAXRSReturn();
		retData.setErrorClass(e.getClass().getName());
		retData.setErrorMsg(ExceptionFormatter.deepFormatToString(e));		
		return retData;
	}
	
	protected IAnnuityService getAnnuityService(Configrable<String, String> configrable) throws ServerBusinessModuleException {		
		try {
			
			return new AnnuityBusinessServiceLookup().getAnnuityAnnuityService(configrable);
			
		} catch (Exception e) {
			AcmeLogger logger = getLogger(configrable);
			logger.info("Annuity Service lookup returned the error: " +e.getMessage());			
			throw new ServerBusinessModuleException(e.getMessage(), e);
		} 
		
	}
	
	private AcmeLogger getLogger(Configrable<String, String> configrable) {
		return AnnuityLoggerFactory.getAcmeServerLogger(configrable, getClass().getName());
	}
	
	/*
	 * utility method to wrap responses in AtomEntry
	 */
	private AtomEntry wrapInAtom(Object obj) {
		AtomContent atomContent = new AtomContent();
		atomContent.setType(MediaType.APPLICATION_XML);
		atomContent.setValue(obj);
		AtomEntry retAtomEntry = new AtomEntry();
		retAtomEntry.setContent(atomContent);
		return retAtomEntry;
	}
	
	@Context	
	javax.ws.rs.core.Application app;	
    @javax.ws.rs.GET
    //@Produces("application/vnd.sun.wadl+xml")
    @Produces("application/xml")
    public org.apache.wink.common.model.wadl.Application getOptions() {
    	
        org.apache.wink.common.model.wadl.Application wadlAppDoc = new WADLGenerator().generate("", app.getClasses());
        return wadlAppDoc;        
    }
    
	@Context	
	javax.ws.rs.core.Application app2;	
    @javax.ws.rs.OPTIONS
    //@Produces("application/vnd.sun.wadl+xml")
    @Produces("application/xml")
    public org.apache.wink.common.model.wadl.Application getOptionsForClient() {
    	
        org.apache.wink.common.model.wadl.Application wadlAppDoc = new WADLGenerator().generate("", app2.getClasses());
        return wadlAppDoc;        
    }
    
}
