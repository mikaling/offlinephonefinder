package com.abdev.offlinephonefinder;

public class Code {
    private int codeID;
    private String feature;
    private String code;

    public Code() {
    }

    public Code(int codeID, String feature, String code) {
        this.codeID = codeID;
        this.feature = feature;
        this.code = code;
    }

    public int getCodeID() {
        return codeID;
    }

    public void setCodeID(int codeID) {
        this.codeID = codeID;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
