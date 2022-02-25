package com.example.ihm_reparties;

import java.io.Serializable;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class AntimatiereUnlocked implements Serializable
{
    public AntimatiereUnlocked(){}

    public AntimatiereUnlocked(boolean unlocked){
        this.unlocked = unlocked;
    }

    @SerializedName("unlocked")
    @Expose
    private Boolean unlocked;

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }

}