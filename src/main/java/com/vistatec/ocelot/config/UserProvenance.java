package com.vistatec.ocelot.config;

import com.vistatec.ocelot.its.model.Provenance;

public class UserProvenance extends Provenance {
	
	private String langCode;
	
	private String email;
	
    public UserProvenance(String revPerson, String revOrg, String extRef, String email) {
    	this(revPerson, revOrg, extRef, email, null);
    }
    
    public UserProvenance(String revPerson, String revOrg, String extRef, String email, String langCode) {
        setRevPerson(revPerson);
        setRevOrg(revOrg);
        setProvRef(extRef);
        this.langCode = langCode;
        this.email = email;
    }

    public boolean isEmpty() {
        return !(getRevPerson() != null ||
                 getRevOrg() != null||
                 getProvRef() != null || langCode != null || email != null);
    }
    
    public void setLangCode(String langCode){

    	this.langCode = langCode;
    }
    
    public String getLangCode(){
    	return langCode;
    }
    
    public void setEmail(String email){
    	this.email = email;
    }
    
    public String getEmail(){
    	return email;
    }
}
