package com.vistatec.ocelot.segment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface XLIFFWriter {
    public void updateSegment(Segment seg, SegmentController controller);

    public void save(File file) throws IOException, UnsupportedEncodingException;
}
