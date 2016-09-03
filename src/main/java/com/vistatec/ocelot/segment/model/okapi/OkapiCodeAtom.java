package com.vistatec.ocelot.segment.model.okapi;

import com.vistatec.ocelot.segment.model.CodeAtom;

import net.sf.okapi.common.resource.Code;

/**
 * Extend the regular CodeAtom to carry the original Okapi-specific data
 * structure for copy-and-paste purposes.
 * 
 * TODO: This seems like it might be a poor solution. Think harder.
 */
public class OkapiCodeAtom extends CodeAtom {

    private final Code code;

    public OkapiCodeAtom(String id, String data, String verboseData, Code code) {
        super(id, data, verboseData);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}