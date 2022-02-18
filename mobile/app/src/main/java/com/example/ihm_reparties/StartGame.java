package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class StartGame implements Serializable
{
    public StartGame(){
        this.isStarted = false;
    }

    public StartGame(boolean isStarted){
        this.isStarted = isStarted;
    }

    @SerializedName("isStarted")
    @Expose
    private Boolean isStarted;

    public Boolean getIsStarted() {
        return isStarted;
    }

    public void setIsStarted(Boolean isStarted) {
        this.isStarted = isStarted;
    }

}