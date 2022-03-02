package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AntimatiereVRValue implements Serializable
{
    public AntimatiereVRValue(){ }

    public AntimatiereVRValue(int value){
        this.value = value;
    }

    @SerializedName("antimatiereValueVR")
    @Expose
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}