package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class NoMoreAntimatiere implements Serializable
{
    public NoMoreAntimatiere(){}

    public NoMoreAntimatiere(boolean noMoreAntimatiere){
        this.noMoreAntimatiere = noMoreAntimatiere;
    }

    @SerializedName("noMoreAntimatiere")
    @Expose
    private Boolean noMoreAntimatiere;

    public Boolean getNoMoreAntimatiere() {
        return noMoreAntimatiere;
    }

    public void setNoMoreAntimatiere(Boolean noMoreAntimatiere) {
        this.noMoreAntimatiere = noMoreAntimatiere;
    }

}