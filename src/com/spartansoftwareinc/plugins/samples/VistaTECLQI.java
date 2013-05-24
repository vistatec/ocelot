package com.spartansoftwareinc.plugins.samples;

/**
 * JSON format for LQI when posting to the VistaTEC Web Service.
 */
public class VistaTECLQI {
    private String fileOriginal, transUnitId, standoffId, sourceLang,
            targetLang, sourceText, targetText, lqiType,
            lqiComment;
    private double lqiSeverity;

    public VistaTECLQI(String fileOriginal, String transUnitId,
            String standoffId, String sourceLang, String targetLang,
            String sourceText, String targetText, String lqiType,
            double lqiSeverity, String lqiComment) {
        this.fileOriginal = fileOriginal;
        this.transUnitId = transUnitId;
        this.standoffId = standoffId;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.sourceText = sourceText;
        this.targetText = targetText;
        this.lqiType = lqiType;
        this.lqiSeverity = lqiSeverity;
        this.lqiComment = lqiComment;
    }

    public String getFileOriginal() {
        return fileOriginal;
    }

    public String getTransUnitId() {
        return transUnitId;
    }

    public String getStandoffId() {
        return standoffId;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public String getSourceText() {
        return sourceText;
    }

    public String getTargetText() {
        return targetText;
    }

    public String getLqiType() {
        return lqiType;
    }

    public double getLqiSeverity() {
        return lqiSeverity;
    }

    public String getLqiComment() {
        return lqiComment;
    }
}
