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

    public MissilePlaced(Boolean isPlaced){
        this.isPlaced = isPlaced;
    }

    @SerializedName("isPlaced")
    @Expose
    private Boolean isPlaced;

    public Boolean getIsPlaced() {
        return isPlaced;
    }

    public void setIsPlaced(Boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

}