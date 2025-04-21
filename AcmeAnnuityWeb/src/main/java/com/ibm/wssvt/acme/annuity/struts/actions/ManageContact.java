package com.ibm.wssvt.acme.annuity.struts.actions;

import com.ibm.wssvt.acme.annuity.common.bean.ContactType;
import com.ibm.wssvt.acme.annuity.common.bean.IAddress;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import java.util.UUID;


public class ManageContact extends AnnuitySupport {

    /**
	 * Action for managing the Contact class
	 */
	private static final long serialVersionUID = 1L;
	private IContact contact;
	private String contactId;
    private String contactPhone;
    private String contactEmail;
    private int contactVersion;
    private String holderId;

    public IContact getContact() {
        return contact;
    }

    public void setContact(IContact con) {
        this.contact = con;
    
    }
       
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactname) {
        this.contactId = contactname;
    
    }

  
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String ph) {
        this.contactPhone = ph;
    
    }
    
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String email) {
        this.contactEmail = email;
    
    }
    
    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String hId) {
        this.holderId = hId;
    
    }
    
    public void setContactVersion(int version){
		contactVersion = version;
	}

    public int getContactVersion(){
		return contactVersion;
	}
    
    private void setInputValuesFromContact()    {
    	this.setContactId(contact.getId());
 	   	this.setContactPhone(contact.getPhone());
 	   	this.setContactEmail(contact.getEmail());	   
 	   	this.setContactVersion(contact.getVersion()); 	   
    }
            
	public String input() throws Exception{
		setTask("CREATE");                
        return INPUT;        
    }
	
	public String autoFill() throws Exception{
		setValuesForInput();
		return INPUT;
	}
	public String delete() throws Exception{       
        setTask("DELETE");
        return "delete"; 
	}
	
	public String update() throws Exception{
        System.out.println("I am in update");
        setTask("UPDATE"); 
        
        boolean found = false;
		found = findContactInDB();
		
		if(found){
			setInputValuesFromContact();
			return INPUT; 
		}			
		else {
			this.addActionError("Did not find Contact in DB."); 
			return ERROR;
		}				              
    }
	
	public String execute() throws Exception {    	 
		if  (getTask().equals("DELETE")){
    		   setContactId("");
    		   return SUCCESS;
    	   }
        if (contact == null) {
        		this.addActionError("Contact value not set");
        		return ERROR;
        	}        	
        setContactId(contact.getId());
        return SUCCESS;       
    }
    
	public String save() throws Exception{
		boolean isSaved = false;
		System.out.println("I am in save");
		if(getTask().equals("CREATE"))			
				isSaved = createContactInDB();
		else if(getTask().equals("UPDATE"))						
				isSaved = updateContactInDB();
		else if(getTask().equals("DELETE"))						
				isSaved = deleteContactInDB();
		else{
			this.addActionError("Save can only be performed on Create and Update and Delete - Contact not saved."); 
			return ERROR;
		}
		if(isSaved)
			return execute(); 
		else 
			return ERROR;
        
    } 
        
	public String view() throws Exception{
		
		setTask("VIEW");
		boolean found = false;
		found = findContactInDB();		
		
		if(found)
			return execute(); 
		else 
			return ERROR;    
	}

	private void setValuesForInput(){
		setContactId(UUID.randomUUID().toString());
		setContactPhone("512-222-333");
		setContactEmail("all@us.ibm.com");
		
	   }
    /*
     * Creates the Contact in the DB. Also updates the holder in the DB. (Finds the holder using the HolderID, and then
     * sets its contact to be the newly created contact
     */
    public boolean createContactInDB(){
    	   	
    	IContact result;
    	IContact contactToCreate = super.getBeansFactory().createContact();
    	
		
		//set these values from the input from the jsp page
    	contactToCreate.setId(getContactId());
    	contactToCreate.setEmail(getContactEmail());
    	contactToCreate.setPhone(getContactPhone());
    	contactToCreate.setContactType(ContactType.HOME);
    	
    	IAddress address = super.getBeansFactory().createAddress();
		address.setLine1("123 Main Street");
		address.setLine2("APT # 123");
		address.setCity("Austin");
		address.setState("Texas");
		address.setZipCode("78758");
		address.setCountry("USA");
    	
    	contactToCreate.setAddress(address);
    	
    	
		
		super.setConfigs(contactToCreate);		
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(contactToCreate);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}
		
		try {
			result = svc.createContact(contactToCreate);
			updateHolderWithContactInDB(result);			
			setContact(result);
			
		} catch (Exception e) {
			this.addActionError("Unable to Create the Contact in the database - "+ e.getMessage() );
			return false;
		}
		return true;
	}
   
    public boolean updateContactInDB(){
    	
    	IContact result;
    	IContact contactToUpdate = super.getBeansFactory().createContact();
    	
    	contactToUpdate.setId(getContactId());
    	contactToUpdate.setEmail(getContactEmail());
    	contactToUpdate.setPhone(getContactPhone());
    	contactToUpdate.setVersion(getContactVersion());
    	contactToUpdate.setContactType(ContactType.HOME);
    	
    	IAddress address = super.getBeansFactory().createAddress();
		address.setLine1("456 Wall Street");
		address.setLine2("APT # 123");
		address.setCity("Austin");
		address.setState("Texas");
		address.setZipCode("78758");
		address.setCountry("USA");
    	
		contactToUpdate.setAddress(address);
		
		
		super.setConfigs(contactToUpdate);		
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(contactToUpdate);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}
		
		//update the contact in the DB
		try {
			result = svc.updateContact(contactToUpdate);
		
		//update the holder with the updated contact
		//updateHolderWithContactInDB(result);		
		setContact(result);
		System.out.println("Inside update contact in DB");
		
		} catch (Exception e) {
			this.addActionError("Unable to Update the Contact in the database - "+ e.getMessage() );
			return false;
		}
		
    return true;
    }
    
    public boolean deleteContactInDB(){
    	IContact contactToDelete = super.getBeansFactory().createContact();
    	contactToDelete.setId(getContactId());
    	
    	super.setConfigs(contactToDelete);		
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(contactToDelete);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
			}		
		try {
		//Set the contact of the Holder to none
		updateHolderWithContactInDB(null);
		} catch (Exception e) {
			// add action error
			this.addActionError("Unable to Update the Holder in the database with the Contact - "+ e.getMessage() );
			return false;
		}				
		//delete the contact from the DB
		try {
			svc.deleteContact(contactToDelete);
		} catch (Exception e) {
			this.addActionError("Unable to Delete Contact in the database - "+ e.getMessage() );
			return false;
		}
    	    	
		setContact(null);
		return true;
    }
    
    public boolean findContactInDB(){
    	IContact result = null;
    	IAnnuityHolder holderToFind = super.getBeansFactory().createAnnuityHolder();
    
    	holderToFind.setId(getHolderId());
    	
    	super.setConfigs(holderToFind);
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(holderToFind);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}
		
		try {
			//find the holder with the hoderID
			holderToFind = svc.findHolderById(holderToFind);						
			
			//find the contact of that holder			
			result = holderToFind.getContact();
				    							
			if(result == null){
				this.addActionError("Holder does not have a contact. Please create contact first" );
				return false;
			}
			
			setContact(result);
			
		} catch (Exception e) {
			this.addActionError("Unable to Find the Holder and Contact in the database - "+ e.getMessage() );
			return false;
		}   	
    	return true;
    }
       
    public void updateHolderWithContactInDB  (IContact contactToAdd)throws Exception{
    	
    	IAnnuityHolder holderToUpdate = super.getBeansFactory().createAnnuityHolder();
      	
    	holderToUpdate.setId(getHolderId()); 
    	
    	super.setConfigs(holderToUpdate);
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(holderToUpdate);
		} catch (ServerBusinessModuleException e) {
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			throw e;
		}
		//find the Holder using the Id
		try {
			holderToUpdate = svc.findHolderById(holderToUpdate);
		}
		catch (Exception e) {
			this.addActionError("Unable to Find the Holder in the database - "+ e.getMessage() );
			//return false;
			throw e;
		}
		
		try
		{
			//set the configs for this holder
			super.setConfigs(holderToUpdate);
			
			//update the contact for this holder - and update holder in DB
			//For deleting the contact - the contactToAdd is null						
			holderToUpdate.setContact(contactToAdd);
			holderToUpdate = svc.updateAnnuityHolder(holderToUpdate);
			}
		catch (Exception e) {
			this.addActionError("Unable to Update the Holder in the database with the Contact- "+ e.getMessage() );
			//return false;
			throw e;
		}
    //return true;	
    }
    
}