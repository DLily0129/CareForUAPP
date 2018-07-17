package com.example.mybuttonview;

/**
 * Created by de on 2018/2/15.
 */

public class PatientItemMedicine {
    private String medno;
    private String name;
    private String comuse;
    public void setMedno(String medno) {
        this.medno = medno;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setComuse(String comuse) {
        this.comuse = comuse;
    }
    public String getMedno() {
        return medno;
    }
    public String getName() {
        return name;
    }
    public String getComuse() {
        return comuse;
    }

    public PatientItemMedicine(){};
    public PatientItemMedicine(String medno,String name,String comuse){
        this.medno = medno;
        this.name = name;
        this.comuse = comuse;
    }
}
