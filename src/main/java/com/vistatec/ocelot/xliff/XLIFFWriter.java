package com.vistatec.ocelot.xliff;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface XLIFFWriter {
    public void updateSegment(OcelotSegment seg);
    
    public void updateNotes(OcelotSegment seg);
    
    public void updateTiming(Double time);
    
    public void updateLqiConfiguration(String lqiConfName);

    public void save(File file) throws IOException, UnsupportedEncodingException;
}
