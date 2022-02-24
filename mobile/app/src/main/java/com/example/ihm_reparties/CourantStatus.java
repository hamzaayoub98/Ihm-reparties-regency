package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CourantStatus implements Serializable
{
    public CourantStatus(){}

    public CourantStatus(boolean restart){
        this.restart = restart;
    }

    @SerializedName("restart")
    @Expose
    private Boolean restart;

    public Boolean getCourantStatus() {
        return restart;
    }

    public void setCourantStatus(Boolean courantStatus) {
        this.restart = courantStatus;
    }

}