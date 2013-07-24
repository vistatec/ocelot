package com.spartansoftwareinc.plugins.samples;

import net.sf.okapi.common.annotation.GenericAnnotationType;

/**
 * JSON format for Provenance when posting to the VistaTEC Web Service.
 */
public class VistaTECProvenance {
    private String fileOriginal, transUnitId, standoffId, sourceLang,
            targetLang, sourceText, targetText, provPerson, provOrg,
            provTool, provPersonRef, provOrgRef, provToolRef, provRevPerson,
            provRevOrg, provRevTool, provRevPersonRef, provRevOrgRef,
            provRevToolRef;

    public VistaTECProvenance(String fileOriginal, String transUnitId,
            String standoffId, String sourceLang, String targetLang,
            String sourceText, String targetText, String person, String org,
            String tool, String revPerson, String revOrg, String revTool) {
        this.fileOriginal = fileOriginal;
        this.transUnitId = transUnitId;
        this.standoffId = standoffId;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.sourceText = sourceText;
        this.targetText = targetText;
        setPerson(person);
        setOrg(org);
        setTool(tool);
        setRevPerson(revPerson);
        setRevOrg(revOrg);
        setRevTool(revTool);
    }

    public String getFileOriginal() {
        return fileOriginal;
    }

    public void setFileOriginal(String fileOriginal) {
        this.fileOriginal = fileOriginal;
    }

    public String getTransUnitId() {
        return transUnitId;
    }

    public void setTransUnitId(String transUnitId) {
        this.transUnitId = transUnitId;
    }

    public String getStandoffId() {
        return standoffId;
    }

    public void setStandoffId(String standoffId) {
        this.standoffId = standoffId;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public void setPerson(String person) {
        if (person != null) {
            if (person.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvPersonRef(person);
            } else {
                setProvPerson(person);
            }
        }
    }

    public String getProvPerson() {
        return provPerson;
    }

    public void setProvPerson(String provPerson) {
        this.provPerson = provPerson;
    }

    public String getProvPersonRef() {
        return provPersonRef;
    }

    public void setProvPersonRef(String provPersonRef) {
        this.provPersonRef = provPersonRef;
    }

    public void setOrg(String org) {
        if (org != null) {
            if (org.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvOrgRef(org);
            } else {
                setProvOrg(org);
            }
        }
    }

    public String getProvOrg() {
        return provOrg;
    }

    public void setProvOrg(String provOrg) {
        this.provOrg = provOrg;
    }

    public String getProvOrgRef() {
        return provOrgRef;
    }

    public void setProvOrgRef(String provOrgRef) {
        this.provOrgRef = provOrgRef;
    }

    public void setTool(String tool) {
        if (tool != null) {
            if (tool.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvToolRef(tool);
            } else {
                setProvTool(tool);
            }
        }
    }

    public String getProvTool() {
        return provTool;
    }

    public void setProvTool(String provTool) {
        this.provTool = provTool;
    }

    public String getProvToolRef() {
        return provToolRef;
    }

    public void setProvToolRef(String provToolRef) {
        this.provToolRef = provToolRef;
    }

    public void setRevPerson(String revPerson) {
        if (revPerson != null) {
            if (revPerson.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvRevPersonRef(revPerson);
            } else {
                setProvRevPerson(revPerson);
            }
        }
    }

    public String getProvRevPerson() {
        return provRevPerson;
    }

    public void setProvRevPerson(String provRevPerson) {
        this.provRevPerson = provRevPerson;
    }

    public String getProvRevPersonRef() {
        return provRevPersonRef;
    }

    public void setProvRevPersonRef(String provRevPersonRef) {
        this.provRevPersonRef = provRevPersonRef;
    }

    public void setRevOrg(String revOrg) {
        if (revOrg != null) {
            if (revOrg.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvRevOrgRef(revOrg);
            } else {
                setProvRevOrg(revOrg);
            }
        }
    }

    public String getProvRevOrg() {
        return provRevOrg;
    }

    public void setProvRevOrg(String provRevOrg) {
        this.provRevOrg = provRevOrg;
    }

    public String getProvRevOrgRef() {
        return provRevOrgRef;
    }

    public void setProvRevOrgRef(String provRevOrgRef) {
        this.provRevOrgRef = provRevOrgRef;
    }

    public void setRevTool(String revTool) {
        if (revTool != null) {
            if (revTool.startsWith(
                    GenericAnnotationType.REF_PREFIX)) {
                setProvRevToolRef(revTool);
            } else {
                setProvRevTool(revTool);
            }
        }
    }

    public String getProvRevTool() {
        return provRevTool;
    }

    public void setProvRevTool(String provRevTool) {
        this.provRevTool = provRevTool;
    }

    public String getProvRevToolRef() {
        return provRevToolRef;
    }

    public void setProvRevToolRef(String provRevToolRef) {
        this.provRevToolRef = provRevToolRef;
    }
}
