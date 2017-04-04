package com.vistatec.ocelot.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.xliff.XLIFFDocument;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.XLIFFVersion;
import com.vistatec.ocelot.xliff.XLIFFWriter;

import net.sf.okapi.common.LocaleId;

class OkapiXLIFFDocument implements XLIFFDocument {
    private File file;
    private XLIFFVersion version;
    private LocaleId srcLocale;
    private LocaleId tgtLocale;
    private String original;
    private XLIFFParser parser;
    private XLIFFWriter writer;
    private List<OcelotSegment> segments = new ArrayList<>();

    OkapiXLIFFDocument(File file, XLIFFVersion version, LocaleId srcLocale, LocaleId tgtLocale, String original,
                   List<OcelotSegment> segments, XLIFFParser parser, XLIFFWriter writer) {
        this.file = file;
        this.version = version;
        this.srcLocale = srcLocale;
        this.tgtLocale = tgtLocale;
        this.original = original;
        this.parser = parser;
        this.writer = writer;
        this.segments = segments;
    }

    public File getFile() {
        return file;
    }

    public XLIFFVersion getVersion() {
        return version;
    }

    public LocaleId getSrcLocale() {
        return srcLocale;
    }

    public LocaleId getTgtLocale() {
        return tgtLocale;
    }

    public List<OcelotSegment> getSegments() {
        return segments;
    }

    XLIFFParser getParser() {
        return parser;
    }

    XLIFFWriter getWriter() {
        return writer;
    }

	@Override
	public String getOriginal() {
		return original;
	}
}
