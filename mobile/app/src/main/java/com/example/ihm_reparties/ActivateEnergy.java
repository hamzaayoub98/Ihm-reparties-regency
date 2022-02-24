package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ActivateEnergy implements Serializable
{
    public ActivateEnergy(){
        this.action = "activerCourant";
    }

    public ActivateEnergy(String action){
        this.action = action;
    }

    @SerializedName("action")
    @Expose
    private String action;

    public String getAction() { return action; }

    public void setAction(String action) {
        this.action = action;
    }

}