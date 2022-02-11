package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class GameFinished implements Serializable
{
    public GameFinished(){}

    public GameFinished(boolean isFinished){
        this.isFinished = isFinished;
    }

    @SerializedName("isFinished")
    @Expose
    private Boolean isFinished;

    public Boolean getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(Boolean isFinished) {
        this.isFinished = isFinished;
    }

}