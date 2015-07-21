package com.vistatec.ocelot.segment.model;

import java.awt.Image;

public abstract class Enrichment {

    private static final String NIF_OFFSET_STRING = "char=";

    protected int offsetStartIdx;

    protected int offsetEndIdx;
    
    protected String id;
    
    protected boolean disabled;
    
    public Enrichment(final String nifOffsetString) {

        retrieveOffset(nifOffsetString);
    }

    public Enrichment(final int offsetStartIdx, final int offsetEndIdx) {

        if (offsetEndIdx < offsetStartIdx) {
            throw new IllegalArgumentException(
                    "The offsetStartIdx parameter value has to be less then offsetEndIdx value. Actual values: offsetStartIdx = "
                            + offsetStartIdx
                            + " - offsetEndIdx = "
                            + offsetEndIdx);
        }
        this.offsetEndIdx = offsetEndIdx;
        this.offsetStartIdx = offsetStartIdx;
    }

    private void retrieveOffset(final String nifOffsetString) {

        if (nifOffsetString == null || nifOffsetString.isEmpty() || !nifOffsetString.contains(NIF_OFFSET_STRING)) {
            throw new IllegalArgumentException("Invalid NIF string: "
                    + nifOffsetString + ". A valid NIF string contains \""
                    + NIF_OFFSET_STRING + "<startIdx>,<endIdx>\"");
        }
        int cutIndex = nifOffsetString.lastIndexOf(NIF_OFFSET_STRING);
        String offsetString = nifOffsetString.substring(cutIndex);
        cutIndex = offsetString.indexOf(",");
        try {
            offsetStartIdx = Integer.valueOf(offsetString
                    .substring(NIF_OFFSET_STRING.length(), cutIndex));
            offsetEndIdx = Integer
                    .valueOf(offsetString.substring(cutIndex + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid NIF string: "
                    + nifOffsetString + ". A valid NIF string contains \""
                    + NIF_OFFSET_STRING + "<startIdx>,<endIdx>\" where <startIdx> and <endIdx> are integer numbers.");
        }

    }

    public int getOffsetStartIdx() {
        return offsetStartIdx;
    }

    // public void setOffsetStartIdx(int offsetStartIdx) {
    // this.offsetStartIdx = offsetStartIdx;
    // }

    public int getOffsetEndIdx() {
        return offsetEndIdx;
    }
    
    public String getEnrichedText(final String text){
        
        StringBuffer enrichedText = new StringBuffer();
        if(text != null && offsetStartIdx < text.length() && offsetEndIdx < text.length()){
            
            enrichedText.append(text.substring(0, offsetStartIdx));
            enrichedText.append("<mrk id=\"");
            enrichedText.append(id);
            enrichedText.append("\" type=\"");
            enrichedText.append(getTagType());
            enrichedText.append("\" ");
            enrichedText.append(getTag());
            enrichedText.append(">");
            enrichedText.append(text.substring(offsetStartIdx, offsetEndIdx));
            enrichedText.append("</mrk>");
            enrichedText.append(text.substring(offsetEndIdx));
        }
        
        return enrichedText.toString();
    }
    
    
    protected abstract String getTagType();
    
    protected abstract String getTag();
    
    public abstract String getType();
    
    public void setDisabled(final boolean disabled){
        this.disabled = disabled;
    }
    
    public boolean isDisabled(){
        return disabled;
    }

    // public void setOffsetEndIdx(int offsetEndIdx) {
    // this.offsetEndIdx = offsetEndIdx;
    // }

    
}
