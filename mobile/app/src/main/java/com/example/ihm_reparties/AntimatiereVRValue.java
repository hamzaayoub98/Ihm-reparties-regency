package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AntimatiereVRValue implements Serializable
{
    public AntimatiereVRValue(){ }

    public AntimatiereVRValue(int antimatiereVRValue){
        this.antimatiereVRValue = antimatiereVRValue;
    }

    @SerializedName("antimatiereValueVR")
    @Expose
    private int antimatiereVRValue;

    public int getAntimatiereVRValue() {
        return antimatiereVRValue;
    }

    public void setAntimatiereVRValue(int antimatiereVRValue) {
        this.antimatiereVRValue = antimatiereVRValue;
    }

}