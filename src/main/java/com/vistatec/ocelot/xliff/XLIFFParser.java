package com.vistatec.ocelot.xliff;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface XLIFFParser {
    public List<OcelotSegment> parse(File xliffFile) throws IOException;

    public String getSourceLang();

    public String getTargetLang();
    
    public String getOriginalFileName();
}
