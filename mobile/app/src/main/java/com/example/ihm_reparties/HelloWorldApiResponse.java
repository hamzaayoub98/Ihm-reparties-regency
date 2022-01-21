package com.example.ihm_reparties;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
// auto generated from https://www.jsonschema2pojo.org
@Generated("jsonschema2pojo")
public class HelloWorldApiResponse {

    @SerializedName("helloworld")
    @Expose
    private String helloworld;

    public String getHelloworld() {
        return helloworld;
    }

    public void setHelloworld(String helloworld) {
        this.helloworld = helloworld;
    }

}