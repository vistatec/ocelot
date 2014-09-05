/*
 * Copyright (C) 2014, VistaTEC or third-party contributors as indicated
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
package com.vistatec.ocelot.segment.okapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import com.vistatec.ocelot.config.ProvenanceConfig;
import com.vistatec.ocelot.segment.XLIFFFactory;
import com.vistatec.ocelot.segment.XLIFFParser;
import com.vistatec.ocelot.segment.XLIFFWriter;

/**
 * Detect which XLIFF parser version to use for the XLIFF file.
 */
public class OkapiXLIFFFactory implements XLIFFFactory {

    @Override
    public XLIFFParser newXLIFFParser(File detectVersion) throws FileNotFoundException, IOException, XMLStreamException {
        BOMInputStream bomInputStream = new BOMInputStream(new FileInputStream(detectVersion),
                ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);
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
                        Iterator<Attribute> attrs = startElement.getAttributes();
                        while (attrs.hasNext()) {
                            Attribute attr = attrs.next();
                            String name = attr.getName().getLocalPart();
                            String value = attr.getValue();

                            if (name.equals("version")) {
                                if ("2.0".equals(value)) {
                                    reader.close();
                                    return new OkapiXLIFF20Parser();
                                } else {
                                    return new OkapiXLIFF12Parser();
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

    @Override
    public XLIFFWriter newXLIFFWriter(XLIFFParser parser, ProvenanceConfig config) {
        if (parser instanceof OkapiXLIFF12Parser) {
            return new OkapiXLIFF12Writer((OkapiXLIFF12Parser) parser, config);
        } else if (parser instanceof OkapiXLIFF20Parser) {
            return new OkapiXLIFF20Writer((OkapiXLIFF20Parser) parser, config);
        } else {
            throw new IllegalArgumentException("Unrecognized XLIFF parser version!");
        }
    }

}
