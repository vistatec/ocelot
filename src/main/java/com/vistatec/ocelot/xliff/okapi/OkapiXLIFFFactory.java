/*
 * Copyright (C) 2014-2015, VistaTEC or third-party contributors as indicated
 * by the @author tags or express copyright attribution statements applied by
 * the authors. All third-party contributions are distributed under license by
 * VistaTEC.
 *
 * This file is part of Ocelot.
 *
 * Ocelot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ocelot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, write to:
 *
 *     Free Software Foundation, Inc.
 *     51 Franklin Street, Fifth Floor
 *     Boston, MA 02110-1301
 *     USA
 *
 * Also, see the full LGPL text here: <http://www.gnu.org/copyleft/lesser.html>
 */
package com.vistatec.ocelot.xliff.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import com.vistatec.ocelot.config.UserProvenance;
import com.vistatec.ocelot.events.api.OcelotEventQueue;
import com.vistatec.ocelot.xliff.XLIFFFactory;
import com.vistatec.ocelot.xliff.XLIFFParser;
import com.vistatec.ocelot.xliff.XLIFFVersion;
import com.vistatec.ocelot.xliff.XLIFFWriter;

/**
 * Detect which XLIFF parser version to use for the XLIFF file.
 */
public class OkapiXLIFFFactory implements XLIFFFactory {

    @Override
    public XLIFFVersion detectXLIFFVersion(File detectVersion) throws IOException, XMLStreamException {
        try (BOMInputStream bomInputStream = new BOMInputStream(new FileInputStream(detectVersion),
                ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE)) {
            String bom = "UTF-8";
            if (bomInputStream.hasBOM()) {
                bom = bomInputStream.getBOMCharsetName();
            }

            XMLInputFactory xml = XMLInputFactory.newInstance();
            XMLEventReader reader = xml.createXMLEventReader(bomInputStream, bom);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLEvent.START_ELEMENT:
                        StartElement startElement = (StartElement) event;
                        String localPart = startElement.getName().getLocalPart();
                        if (localPart.equals("xliff")) {
                            @SuppressWarnings("unchecked")
                            Iterator<Attribute> attrs = startElement.getAttributes();
                            while (attrs.hasNext()) {
                                Attribute attr = attrs.next();
                                if (isXliffVersionAttributeName(attr.getName())) {
                                    String value = attr.getValue();
                                    reader.close();
                                    if ("2.0".equals(value)) {
                                        return XLIFFVersion.XLIFF20;
                                    } else {
                                        return XLIFFVersion.XLIFF12;
                                    }
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
            throw new IllegalStateException("Could not detect XLIFF version");
        }
    }

    // The XLIFF spec is unclear on whether @version is namespaced.  My assumption
    // is no, it is not namespaced, but just in case other implementations disagree,
    // we'll be permissive.
    private boolean isXliffVersionAttributeName(QName name) {
        if (!"version".equals(name.getLocalPart())) return false;
        String ns = name.getNamespaceURI();
        return (ns == null || "".equals(ns) ||
                "urn:oasis:names:tc:xliff:document:1.2".equals(ns) ||
                "urn:oasis:names:tc:xliff:document:2.0".equals(ns));
    }

    @Override
    public XLIFFParser newXLIFFParser(XLIFFVersion version) throws IOException, XMLStreamException {
        switch (version) {
        case XLIFF12:
            return new OkapiXLIFF12Parser();
        case XLIFF20:
            return new OkapiXLIFF20Parser();
        }
        throw new IllegalStateException();
    }

    @Override
    public XLIFFWriter newXLIFFWriter(XLIFFParser parser,
            UserProvenance userProvenance, OcelotEventQueue eventQueue) {
        if (parser instanceof OkapiXLIFF12Parser) {
            return new OkapiXLIFF12Writer((OkapiXLIFF12Parser) parser, userProvenance, eventQueue);
        } else if (parser instanceof OkapiXLIFF20Parser) {
            return new OkapiXLIFF20Writer((OkapiXLIFF20Parser) parser, userProvenance, eventQueue);
        } else {
            throw new IllegalArgumentException("Unrecognized XLIFF parser version!");
        }
    }

}
