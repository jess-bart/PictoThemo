package com.jessy_barthelemy.pictothemo.Enum;

public enum HttpVerb {
    GET("GET"), PUT("PUT"), POST("POST"), DELETE("DELETE");
    private String method;

    HttpVerb(String brand) {
        this.method = brand;
    }

    @Override
    public String toString(){
        return method;
    }
}
