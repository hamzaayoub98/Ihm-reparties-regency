package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class HypervitesseReady implements Serializable
{
    public HypervitesseReady(){}

    public HypervitesseReady(boolean hypervitesseReady){
        this.hypervitesseReady = hypervitesseReady;
    }

    @SerializedName("hyperVitesseStatus")
    @Expose
    private Boolean hypervitesseReady;

    public Boolean getHypervitesseReady() {
        return hypervitesseReady;
    }

    public void setHypervitesseReady(Boolean hypervitesseReady) {
        this.hypervitesseReady = hypervitesseReady;
    }

}