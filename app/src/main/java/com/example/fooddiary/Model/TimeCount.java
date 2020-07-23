package com.example.fooddiary.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeCount {

    @SerializedName("COUNT(*)")
    @Expose
    private Integer cOUNT;

    public Integer getCOUNT() {
        return cOUNT;
    }

    public void setCOUNT(Integer cOUNT) {
        this.cOUNT = cOUNT;
    }

}