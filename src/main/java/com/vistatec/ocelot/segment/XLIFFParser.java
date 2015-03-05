package com.vistatec.ocelot.segment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface XLIFFParser {
    public List<OcelotSegment> parse(File xliffFile) throws IOException;

    public String getSourceLang();

    public String getTargetLang();
}
