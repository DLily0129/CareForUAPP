package com.example.mybuttonview;

/**
 * Created by de on 2018/2/15.
 */

public class PatientItemChoice {
    private String name;
    private String comuse;
    private String medno;
    private String ischeck;
    public void setMedno(String medno) {
        this.medno = medno;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUseNum(String comuse) {
        this.comuse = comuse;
    }
    public void setIscheck(String ischeck){this.ischeck=ischeck;}
    public String getName() {
        return name;
    }
    public String getUseNum() {
        return comuse;
    }
    public String getMedno() {
        return medno;
    }
    public String getIscheck(){return ischeck;}
    public PatientItemChoice(){};
}
