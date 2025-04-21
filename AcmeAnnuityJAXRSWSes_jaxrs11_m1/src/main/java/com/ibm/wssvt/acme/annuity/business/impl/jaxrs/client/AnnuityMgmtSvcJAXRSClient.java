/*  Client Generated for WDT JAX-RS Client Generation Test only
package com.ibm.wssvt.acme.annuity.business.impl.jaxrs.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityValueObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Entity;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.AnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Contact;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.Payor;
import com.ibm.wssvt.acme.annuity.common.bean.jaxbjpa.PayoutValueObject;
import java.lang.String;

public class AnnuityMgmtSvcJAXRSClient {

	final String resourceUrl;
	
	public AnnuityMgmtSvcJAXRSClient(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	
	public Response invokeCreateAnnuity(AnnuityValueObject a){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.post(Entity.entity(a, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding POST request.
		// Response response = builder.post(Entity.entity(a, "application/json"));
		
		return response; 
	}

	public Response invokeCreateAnnuityHolder(AnnuityHolder a){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.post(Entity.entity(a, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding POST request.
		// Response response = builder.post(Entity.entity(a, "application/json"));
		
		return response; 
	}

	public Response invokeCreateContact(Contact c){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.post(Entity.entity(c, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding POST request.
		// Response response = builder.post(Entity.entity(c, "application/json"));
		
		return response; 
	}

	public Response invokeCreatePayor(Payor p){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.post(Entity.entity(p, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding POST request.
		// Response response = builder.post(Entity.entity(p, "application/json"));
		
		return response; 
	}

	public Response invokeCreatePayout(PayoutValueObject p){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.post(Entity.entity(p, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding POST request.
		// Response response = builder.post(Entity.entity(p, "application/json"));
		
		return response; 
	}

	public Response invokeDeleteAnnuity(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteAnnuityAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteContact(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact").path("{contactId}");
		target = target.resolveTemplate("contactId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteContactAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact").path("{contactId}");
		target = target.resolveTemplate("contactId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteHolder(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteHolderAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeletePayor(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeletePayorAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeletePayout(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout").path("{payoutId}");
		target = target.resolveTemplate("payoutId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeletePayoutAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout").path("{payoutId}");
		target = target.resolveTemplate("payoutId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteRider(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/rider").path("{riderId}");
		target = target.resolveTemplate("riderId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeDeleteRiderAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/rider").path("{riderId}");
		target = target.resolveTemplate("riderId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.delete();
		return response; 
	}

	public Response invokeFindAnnuityById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindAnnuityByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindAnnuityHolder(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindAnnuityHolderAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder/annuity").path("{annuityId}");
		target = target.resolveTemplate("annuityId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindContactById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact").path("{contactId}");
		target = target.resolveTemplate("contactId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindContactByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact").path("{contactId}");
		target = target.resolveTemplate("contactId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindHolderAnnuities(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity/holder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindHolderAnnuitiesAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity/holder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindHolderById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindHolderByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder").path("{holderId}");
		target = target.resolveTemplate("holderId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayorAnnuities(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayorAnnuitiesAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayorById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayorByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor").path("{payorId}");
		target = target.resolveTemplate("payorId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayoutById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout").path("{payoutId}");
		target = target.resolveTemplate("payoutId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindPayoutByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout").path("{payoutId}");
		target = target.resolveTemplate("payoutId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindRiderById(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/rider").path("{riderId}");
		target = target.resolveTemplate("riderId", s);
		
		Invocation.Builder builder = target.request("application/xml", "application/json");
		Response response = builder.get();
		return response; 
	}

	public Response invokeFindRiderByIdAtom(String s){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/rider").path("{riderId}");
		target = target.resolveTemplate("riderId", s);
		
		Invocation.Builder builder = target.request("application/atom+xml");
		Response response = builder.get();
		return response; 
	}

	public Response invokeTestService(){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/test");
		Invocation.Builder builder = target.request("text/plain");
		Response response = builder.get();
		return response; 
	}

	public Response invokeUpdateAnnuity(AnnuityValueObject a){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuity");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.put(Entity.entity(a, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding PUT request.
		// Response response = builder.put(Entity.entity(a, "application/json"));
		
		return response; 
	}

	public Response invokeUpdateAnnuityHolder(AnnuityHolder a){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/annuityholder");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.put(Entity.entity(a, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding PUT request.
		// Response response = builder.put(Entity.entity(a, "application/json"));
		
		return response; 
	}

	public Response invokeUpdateContact(Contact c){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/contact");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.put(Entity.entity(c, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding PUT request.
		// Response response = builder.put(Entity.entity(c, "application/json"));
		
		return response; 
	}

	public Response invokeUpdatePayor(Payor p){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payor");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.put(Entity.entity(p, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding PUT request.
		// Response response = builder.put(Entity.entity(p, "application/json"));
		
		return response; 
	}

	public Response invokeUpdatePayout(PayoutValueObject p){
		Client client = ClientBuilder.newBuilder().build();
		WebTarget target = client.target(resourceUrl + "/jaxrs/payout");
		Invocation.Builder builder = target.request("application/xml", "application/json");
		
		Response response = builder.put(Entity.entity(p, "application/xml"));
		// TODO: Optionally, select an alternative media type for the preceding PUT request.
		// Response response = builder.put(Entity.entity(p, "application/json"));
		
		return response; 
	}

}
*/