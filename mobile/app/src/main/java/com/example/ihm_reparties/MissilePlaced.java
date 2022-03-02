package com.example.ihm_reparties;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class MissilePlaced implements Serializable
{
    public MissilePlaced(){}

    public MissilePlaced(Boolean missilePlaced){
        this.missilePlaced = missilePlaced;
    }

    @SerializedName("missilePlaced")
    @Expose
    private Boolean missilePlaced;

    public Boolean getMissilePlaced() {
        return missilePlaced;
    }

    public void setMissilePlaced(Boolean missilePlaced) {
        this.missilePlaced = missilePlaced;
    }

}