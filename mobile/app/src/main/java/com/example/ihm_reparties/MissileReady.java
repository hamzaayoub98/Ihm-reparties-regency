package com.example.ihm_reparties;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class MissileReady implements Serializable
{
    public MissileReady(){}

    public MissileReady(Boolean missileReady){
        this.missileReady = missileReady;
    }

    @SerializedName("missileReady")
    @Expose
    private Boolean missileReady;

    public Boolean getMissileReady() {
        return missileReady;
    }

    public void setMissileReady(Boolean missileReady) {
        this.missileReady = missileReady;
    }

}