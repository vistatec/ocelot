package com.vistatec.ocelot.its.stats.model;

import java.util.Objects;

public class TextAnalysisStats implements ITSStats {

	private Integer count = 1;
	
	private Type type;
	
	private String value;
	
	public enum Type {
		
		taConfidence,
		taClassRef,
		taIdentRef,
		annotatorsRef
	}
	
	public TextAnalysisStats() {
    }
	
	public TextAnalysisStats(Type type, String value) {
	 
		this.type = type;
		this.value = value;
    }
	
	@Override
    public String getKey() {
		return getClass().getName() + ":" + type + ":" + value;
    }

	@Override
    public void combine(ITSStats stats) {
	    count++;
	    
    }

	@Override
    public String getDataCategory() {
	    
	    return "Text-Analysis";
    }

	@Override
    public String getType() {
	    return type.toString();
    }

	@Override
    public String getValue() {
	    return value;
    }

	@Override
    public Integer getCount() {
	    return count;
    }

	@Override
    public void setCount(Integer count) {
		
		this.count = count;
    }
	
	@Override
	public boolean equals(Object o) {
		 if (o == this) return true;
	        if (o == null || !(o instanceof ProvenanceStats)) return false;
	        ProvenanceStats prov = (ProvenanceStats)o;
	        return Objects.equals(type, prov.getType()) &&
	               Objects.equals(value, prov.getValue()) &&
	               Objects.equals(count, prov.getCount());
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, value, count);
	}
}
