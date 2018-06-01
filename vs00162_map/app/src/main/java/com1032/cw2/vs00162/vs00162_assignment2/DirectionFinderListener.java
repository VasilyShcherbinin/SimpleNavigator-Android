package com1032.cw2.vs00162.vs00162_assignment2;

/**
 * Created by Vasily on 20/05/2016.
 */

import java.util.List;

/**
 * Interface for the MapActivity.java class.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
