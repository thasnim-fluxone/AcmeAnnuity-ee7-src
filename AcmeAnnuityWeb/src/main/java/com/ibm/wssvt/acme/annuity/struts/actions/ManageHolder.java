package com.ibm.wssvt.acme.annuity.struts.actions;

import java.util.Date;
//import java.util.UUID;

import com.ibm.wssvt.acme.annuity.common.bean.AnnuityHolderCategory;
import com.ibm.wssvt.acme.annuity.common.bean.IAnnuityHolder;
import com.ibm.wssvt.acme.annuity.common.bean.IContact;
import com.ibm.wssvt.acme.annuity.common.business.IAnnuityService;
import com.ibm.wssvt.acme.annuity.common.exception.ServerBusinessModuleException;
import com.ibm.wssvt.acme.common.util.StringUtils;

public class ManageHolder extends AnnuitySupport {
    
	private static final long serialVersionUID = 1L;
	private IAnnuityHolder holder = null;
    private String holderId;
    private String holderFirstName;
    private String holderLastName;
    private String holderGovernmentId;
    private int holderVersion;
    private Date holderDateOfBirth;
    private IContact holderContact;
    private String contactId;
    private String holderCategory;

	
	public String input() throws Exception{
		System.out.println("I am in input");
		setTask("CREATE");       
        return INPUT;       
    }
			
	public String autoFill() throws Exception{
		setValuesForInput();
		return INPUT;
	}
	
	public String find() throws Exception {      
		System.out.println("I am in find");
		setTask("FIND");
		
		boolean found = false;
		found = findHolderInDB();		
		
		if(found)
			return execute(); 
		else{
			this.addActionError("Did not find this Holder in DB. Please try again"); 
			return "find";
		}	 			
    }

	public String update() throws Exception{
        System.out.println("I am in update");
        setTask("UPDATE"); 
        
        boolean found = false;
		found = findHolderInDB();
		
		if(found){
			setInputValuesFromHolder();
			return INPUT; 
		}			
		else {
			this.addActionError("Did not find Holder in DB."); 
			return ERROR;
		}				              
    }
	
	public String view() throws Exception{
		boolean found = false;
		found = findHolderInDB();		
		
		if(found)
			return execute(); 
		else 
			return ERROR;    
	}

	public String delete() throws Exception{       
        setTask("DELETE");
        return "delete"; 
	}
	
	
	public String deleteConfirm() throws Exception{ 
		boolean isDeleted = false;
		if(getTask().equals("DELETE"))						
			isDeleted = deleteHolderInDB();
		if(isDeleted)
			return "deleteSuccess"; 
		else{
			this.addActionError("Holder was not deleted."); 			 
			return ERROR;
		}
	}
	
	
	public String save() throws Exception{
		boolean isModified = false;
		
		System.out.println("I am in save");
		if(getTask().equals("CREATE"))			
				isModified = createHolderInDB();
		else if(getTask().equals("UPDATE"))						
				isModified = updateHolderInDB();
		
		else{
			this.addActionError("Save can only be performed on Create and Update - Holder not saved."); 
			return ERROR;
		}
		if(isModified)
			return execute(); 
		else{
			this.addActionError("Holder was not saved."); 			 
			return ERROR;
		}				
    }
		
	public String execute() throws Exception {    	 	        
		System.out.println("I am in execute");		
						
		if (isInvalid(getHolderId())) {
			this.addFieldError("holderId", "Holder Id is invalid.");        	 
        	return INPUT;
        
		} else {
                if (holder == null) {
        		this.addActionError("Holder value not set");
        		return ERROR;
        	}        	
        	//System.out.println("Contact id (in execute) is : "+ getContactId());
        	setHolderId(holder.getId());
        	if(holder.getContact()!= null) {
        		setContactId(holder.getContact().getId());
        	}
        	else
        		setContactId("");
        	System.out.println("Contact id is : "+ getContactId());
        	//System.out.println("I am in execute - before returning success");
        	return SUCCESS;
        }    	
	}
    
    
    private boolean isInvalid(String value) {        
    	return (value.equals(null));
    }

   private boolean notEmpty(String value) {
	   if (value.equals(""))
		   return false;
	   else 
		   return true;
   }
   private void setInputValuesFromHolder()   {	  
	   this.setHolderFirstName(holder.getFirstName());
	   this.setHolderLastName(holder.getLastName());
	   this.setHolderGovernmentId(holder.getGovernmentId());
	   this.setHolderVersion(holder.getVersion());
	   this.setHolderDateOfBirth(holder.getDateOfBirth());
	   this.setHolderCategory((holder.getCategory()).toString());
	   
	   System.out.println("Holders'category from DB is: "+ (holder.getCategory()).toString());
	   System.out.println("Holders'category is: "+ holderCategory);
	   //holder has a contact - get the contactId	  
	   if (notEmpty(contactId)){
		   this.setContactId(holder.getContact().getId());
	   }	   
   }

   
   private void setValuesForInput(){
	   //setHolderId(UUID.randomUUID().toString());
	   setHolderFirstName("First Name");
	   setHolderLastName("Last Name");
	   setHolderGovernmentId("111-22-3333");
   }
   
   public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String id) {
        this.holderId= id;
    
    }
  
    public String getHolderFirstName() {
        return holderFirstName;
    }

    public void setHolderFirstName(String firstName) {
        this.holderFirstName = firstName;
    
    }
    
    public String getHolderLastName() {
        return holderLastName;
    }

    public void setHolderLastName(String lastName) {
        this.holderLastName = lastName;
    
    }
    
    public String getHolderGovernmentId(){
    	return holderGovernmentId;
    }

	public void setHolderGovernmentId(String governmentId){
		this.holderGovernmentId = governmentId;
	}

	public void setHolderVersion(int version){
		holderVersion = version;
	}
	public int getHolderVersion(){
		return holderVersion;
	}
    
	public Date getHolderDateOfBirth(){
		return holderDateOfBirth;
	}

	public void setHolderDateOfBirth(Date dateOfBirth){
		holderDateOfBirth = dateOfBirth;
	}
	
	/*public AnnuityHolderCategory getHolderCategory(){
	 return holderCategory;
	}
	public void setHolderCategory(AnnuityHolderCategory category){
		holderCategory = category;
	}
	*/
	
	public String getHolderCategory(){
		 return holderCategory;
		}
	
	public void setHolderCategory(String category){
			holderCategory = category;
		}
	public IContact getHolderContact() {
        return holderContact;
    }

    public void setHolderContact(IContact con) {
        this.holderContact = con;   
    }
	
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String id) {
        this.contactId= id;   
    }
    
    public IAnnuityHolder getHolder() {
        return holder;
    }

    public void setHolder(IAnnuityHolder c) {
        this.holder = c;
    
    }
    public boolean createHolderInDB(){
    	IAnnuityHolder result;
    	IAnnuityHolder holderToCreate = super.getBeansFactory().createAnnuityHolder();
		
		//set these values from the input from the jsp page
		holderToCreate.setId(getHolderId());
		holderToCreate.setFirstName(getHolderFirstName());
		holderToCreate.setLastName(getHolderLastName());
		holderToCreate.setGovernmentId(getHolderGovernmentId());
		
		//put default values for the other fields
		//holder.setId(UUID.randomUUID().toString());							
		//holderToCreate.setGovernmentId("111-22-3333");
		
		holderToCreate.setDateOfBirth(new Date());	
		holderToCreate.setLastUpdateDate(new Date());
		Byte[] b = new Byte[100];
		for (int i=0; i<100;i++){
			b[i]= (byte) i;
		}
		holderToCreate.setPicture(b);
		holderToCreate.setTimeOfBirth(new Date());				
		
		//holderToCreate.setCategory(AnnuityHolderCategory.GOLD);
		
		//holderToCreate.setCategory(getHolderCategory());
		System.out.println("Holders'category is: "+ getHolderCategory());
		
		holderToCreate.setCategory(StringUtils.toEnum(AnnuityHolderCategory.class, holderCategory));		
		
		System.out.println("Holders'category after Enum is: "+ holderToCreate.getCategory());
		
		
		
		
		
		//holderToCreate.setCategory(AnnuityHolderCategory.GOLD);		
		
		super.setConfigs(holderToCreate);		
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(holderToCreate);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}		
		try {
			result = svc.createAnnuityHolder(holderToCreate);			
			setHolder(result);
			
		} catch (Exception e) {
			this.addActionError("Unable to Create the Holder in the database - "+ e.getMessage() );
			return false;
		}
		return true;
	}
    
    public boolean findHolderInDB(){
    	IAnnuityHolder result;
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
			result = svc.findHolderById(holderToFind);				
			setHolder(result);
			
		} catch (Exception e) {
			this.addActionError("Unable to Find the Holder in the database - "+ e.getMessage() );
			return false;
		}    	   	
    	return true;
    }
            
    public boolean updateHolderInDB(){
    	IAnnuityHolder result;
    	IContact contactInHolder;
    	    	
    	System.out.println("Inside update in DB");
    	IAnnuityHolder holderToUpdate = super.getBeansFactory().createAnnuityHolder();
      	
       	holderToUpdate.setId(getHolderId());     	     	     	     	
      	holderToUpdate.setFirstName(getHolderFirstName());
		holderToUpdate.setLastName(getHolderLastName());
		holderToUpdate.setGovernmentId(getHolderGovernmentId());
		//holderToUpdate.setCategory(AnnuityHolderCategory.GOLD);
		holderToUpdate.setVersion(getHolderVersion());
		holderToUpdate.setDateOfBirth(getHolderDateOfBirth());
		holderToUpdate.setCategory(StringUtils.toEnum(AnnuityHolderCategory.class, getHolderCategory()));
		
    	super.setConfigs(holderToUpdate);
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(holderToUpdate);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}
		
		try {
			System.out.println("Inside update Holder in DB - try block");
			//If there is a contact for this holder - get it from the DB - and set the contact						
			if (notEmpty(contactId)){
				System.out.println("Inside update in DB - if contact is not empty");
				contactInHolder = super.getBeansFactory().createContact();
				contactInHolder.setId(contactId);
				super.setConfigs(contactInHolder);
				contactInHolder = svc.findContactById(contactInHolder);
				System.out.println("ContactInHolders's email is: "+ contactInHolder.getEmail());
				
				holderToUpdate.setContact(contactInHolder);				
			}									
			System.out.println("Inside update - before calling update");
			result = svc.updateAnnuityHolder(holderToUpdate);
			System.out.println("result's gv id: "+ result.getGovernmentId());	
			setHolder(result);
			
		}catch (Exception e) {
			this.addActionError("Unable to Update the Holder in the database - "+ e.getMessage() );
			return false;
		}    	    	
    return true;
    }
    
    //Delete the Holder. If it contains a contact - delete the contact also
    public boolean deleteHolderInDB(){
    	
    	IAnnuityHolder holderToDelete = super.getBeansFactory().createAnnuityHolder();
    	IContact contactToDelete;
       	
    	holderToDelete.setId(getHolderId());     	     	     	     	
    	super.setConfigs(holderToDelete);		
		IAnnuityService svc = null;
		try {
			svc = super.getNextService(holderToDelete);
		} catch (ServerBusinessModuleException e) {
			// add action error
			this.addActionError("Unable to get the next Service - "+ e.getMessage() );
			return false;
		}
		
		//delete the Holder from the DB
		try {
			svc.deleteAnnuityHolder(holderToDelete);			
		} catch (Exception e) {
			this.addActionError("Unable to Delete Holder in the database - "+ e.getMessage() );
			return false;
		}
		try {		
			//If there is a contact for this holder - delete the contact also						
			if (notEmpty(contactId)){
				System.out.println("Inside delete Holder in DB - if contact is not empty");
				
				contactToDelete = super.getBeansFactory().createContact();
				contactToDelete.setId(contactId);
				super.setConfigs(contactToDelete);
				svc.deleteContact(contactToDelete);										
			}
			
		} catch (Exception e) {
			// add action error
			this.addActionError("Unable to Update the Holder in the database with the Contact - "+ e.getMessage() );
			return false;
		}
		
		
		
		
    	    	
		setHolder(null);
		return true;
    }
    
    
}