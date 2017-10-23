package com.diordna.whats.near.me.model;


import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

public class PlacesList implements Serializable {

    @Key
    public String status;

    @Key
    public List<Place> results;

}