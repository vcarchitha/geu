package com.geu.aem.cca.beans;

public class LeadUniqueKeyBean {
	
	private String firstName;
	private String lastName;
	private String contactNum;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getContactNum() {
		return contactNum;
	}
	public void setContactNum(String contactNum) {
		this.contactNum = contactNum;
	}	
	
	public boolean equals(Object other){
	    boolean result;
	    if((other == null) || (getClass() != other.getClass())){
	        result = false;
	    } // end if
	    else{
	    	LeadUniqueKeyBean otherLead = (LeadUniqueKeyBean) other;
	        result = firstName.equalsIgnoreCase(otherLead.firstName) &&  lastName.equalsIgnoreCase(otherLead.lastName) && contactNum.equalsIgnoreCase(otherLead.contactNum);
	    } // end else

	    return result;
	}
	
	public int hashCode() {
		return 1;
	}

}
