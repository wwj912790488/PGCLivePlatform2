package com.arcvideo.system.host;

import com.arcvideo.system.model.Route;

import java.util.List;

public interface RouteUtil {
    List<Route> getRoutingList();
    void addRoute(Route route);
    void deleteRoute(Route route);
}
