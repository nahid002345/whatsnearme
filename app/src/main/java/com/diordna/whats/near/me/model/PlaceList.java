package com.diordna.whats.near.me.model;


import com.google.api.client.util.Key;
//import google client library
//import com.google.api.client.util.Key;

public class PlaceList {

    @Key
    public Place placeInfo;

    @Key
    public PlaceDetails placeDetails;
    public  PlaceList (){

    }
}

