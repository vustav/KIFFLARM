package com.kiefer.kifflarm.files;

import java.io.Serializable;

public class Param implements Serializable {
    public String key, value;

    public Param(String key, String value){
        this.key = key;
        this.value = value;
    }
}
