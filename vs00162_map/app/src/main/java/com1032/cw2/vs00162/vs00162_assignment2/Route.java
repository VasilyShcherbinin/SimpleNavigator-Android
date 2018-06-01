package com1032.cw2.vs00162.vs00162_assignment2;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Vasily on 20/05/2016.
 */

/**
 * Class to define a Route object.
 */
public class Route {

    /**
     * Defining local variables.
     */
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
