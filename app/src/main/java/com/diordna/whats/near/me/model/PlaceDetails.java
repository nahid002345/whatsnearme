package com.diordna.whats.near.me.model;


import com.google.api.client.util.Key;

import java.io.Serializable;

public class PlaceDetails implements Serializable {

    @Key
    public String status;

    @Key
    public Place result;

    @Override
    public String toString() {
        if (result!=null) {
            return result.toString();
        }
        return super.toString();
    }
}

