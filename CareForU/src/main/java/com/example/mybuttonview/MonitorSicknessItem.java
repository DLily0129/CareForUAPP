package com.example.mybuttonview;


public class MonitorSicknessItem {
    String pillname;
    String pernum = "每次";
    String starttime;
    String pertime;
    String ischeck;

    public void setPillname(String pillname){
        this.pillname = pillname;
    }
    public void setPernum(String pernum){
        this.pernum = this.pernum + pernum;
    }
    public void setStarttime(String starttime){
        this.starttime = starttime;
    }
    public void setPertime(String pertime){
        this.pertime = pertime;
    }
    public void setIscheck(String ischeck){
        this.ischeck = ischeck;
    }
    public String getPillname(){
        return pillname;
    }
    public String getPernum(){
        return pernum;
    }
    public String getStarttime(){
        return starttime;
    }
    public String getPertime(){
        return pertime;
    }
    public String getIscheck(){
        return ischeck;
    }
}
