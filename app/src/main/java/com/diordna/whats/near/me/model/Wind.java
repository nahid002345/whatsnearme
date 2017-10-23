package com.diordna.whats.near.me.model;

/**
 * Created by Nahid 002345 on 1/9/2017.
 */

public class Wind
{
    private String speed;

    private String deg;

    public String getSpeed ()
    {
        return speed;
    }

    public void setSpeed (String speed)
    {
        this.speed = speed;
    }

    public String getDeg ()
    {
        return deg;
    }

    public void setDeg (String deg)
    {
        this.deg = deg;
    }

    @Override
    public String toString()
    {
        return "[speed = "+speed+", deg = "+deg+"]";
    }
}
