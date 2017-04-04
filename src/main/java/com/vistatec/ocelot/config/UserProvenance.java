package com.vistatec.ocelot.config;

import com.vistatec.ocelot.its.model.Provenance;

public class UserProvenance extends Provenance {
	
	private String langCode;
	
    public UserProvenance(String revPerson, String revOrg, String extRef) {
    	this(revPerson, revOrg, extRef, null);
    }
    
    public UserProvenance(String revPerson, String revOrg, String extRef, String langCode) {
        setRevPerson(revPerson);
        setRevOrg(revOrg);
        setProvRef(extRef);
        this.langCode = langCode;
    }

    public boolean isEmpty() {
        return !(getRevPerson() != null ||
                 getRevOrg() != null||
                 getProvRef() != null || langCode != null);
    }
    
    public void setLangCode(String langCode){

    	this.langCode = langCode;
    }
    
    public String getLangCode(){
    	return langCode;
    }
}
