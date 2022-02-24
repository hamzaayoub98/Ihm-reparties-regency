package com.example.ihm_reparties;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class CourantSequence implements Serializable
{
    public CourantSequence(){}

    public CourantSequence(List<Integer> sequence){
        this.sequence = sequence;
    }

    @SerializedName("sequence")
    @Expose
    private List<Integer> sequence;

    public List<Integer> getCourantSequence() {
        return sequence;
    }

    public void setCourantSequence(List<Integer> sequence) {
        this.sequence = sequence;
    }

}