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

    public MissileReady(Boolean isReady){
        this.isReady = isReady;
    }

    @SerializedName("isReady")
    @Expose
    private Boolean isReady;

    public Boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(Boolean isReady) {
        this.isReady = isReady;
    }

}