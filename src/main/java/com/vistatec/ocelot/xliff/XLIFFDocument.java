package com.vistatec.ocelot.xliff;

import java.io.File;
import java.util.List;

import com.vistatec.ocelot.segment.model.OcelotSegment;

import net.sf.okapi.common.LocaleId;

public interface XLIFFDocument {

    public File getFile();

    public LocaleId getSrcLocale();

    public LocaleId getTgtLocale();

    public List<OcelotSegment> getSegments();

    public XLIFFVersion getVersion();
    
    public String getOriginal();
}
