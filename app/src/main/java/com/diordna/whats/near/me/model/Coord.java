package com.diordna.whats.near.me.model;
/**
 * Created by Nahid 002345 on 1/9/2017.
 */
public class Coord
{
    private String lon;

    private String lat;

    public String getLon ()
    {
        return lon;
    }

    public void setLon (String lon)
    {
        this.lon = lon;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "[lon = "+lon+", lat = "+lat+"]";
    }
}

