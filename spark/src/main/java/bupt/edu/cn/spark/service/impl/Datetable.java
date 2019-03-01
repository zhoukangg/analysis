package bupt.edu.cn.spark.service.impl;

import bupt.edu.cn.spark.base.AbstractSchema;

public class Datetable extends AbstractSchema{
    private Integer year;

    private Integer month;

    private Integer day;

    private String stringTime;

    Datetable(){
        this.day = 0;
        this.month = 0;
        this.year = 0;
        this.stringTime = "1997/01/01";
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }
}
