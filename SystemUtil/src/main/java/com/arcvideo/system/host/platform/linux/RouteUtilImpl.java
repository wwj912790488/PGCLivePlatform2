package com.arcvideo.system.host.platform.linux;

import com.arcvideo.system.host.RouteUtil;
import com.arcvideo.system.model.Route;
import com.arcvideo.system.util.ExecutingCommand;
import com.arcvideo.system.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RouteUtilImpl implements RouteUtil {
    private static final Logger logger = LoggerFactory.getLogger(RouteUtilImpl.class);
    private static final String configDirectory = "/etc/sysconfig/network-scripts";
    private static final String rountListCommand = "route -n | awk 'NR>2 {print $1,$2,$3,$4,$5,$6,$7,$8}'";

    @Override
    public List<Route> getRoutingList() {
        List<Route> list = new ArrayList<>();
        List<String> lines = ExecutingCommand.runShellNative(rountListCommand);
        for (String line : lines) {
            String[] strings = line.trim().replaceAll("\\s+ ", " ").split(" ");
            Route route = new Route(strings);
            list.add(route);
        }
        return list;
    }

    @Override
    public void addRoute(Route route) {
        ExecutingCommand.runShellNative("route add " + createShellParam(route));
        // add route to route-?
        List<Route> routeList = loadRoutesByIface(route.getIface());
        int index = findRoute(route, routeList);
        if (index < 0) {
            routeList.add(route);
            update(route.getIface(), routeList);
        }
    }

    @Override
    public void deleteRoute(Route route) {
        ExecutingCommand.runShellNative("route del" + createShellParam(route));
        List<Route> routeList = loadRoutesByIface(route.getIface());
        int index = findRoute(route, routeList);
        if (index >= 0) {
            routeList.remove(index);
            update(route.getIface(), routeList);
        }
    }

    private void update(String iface, List<Route> routes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            stringBuilder.append("ADDRESS").append(i).append("=")
                    .append(route.getDestination()).append("\n");
            stringBuilder.append("NETMASK").append(i).append("=")
                    .append(route.getMask()).append("\n");
            if (!StringHelper.isEmpty(route.getGateway())){
                stringBuilder.append("GATEWAY").append(i).append("=")
                        .append(route.getGateway()).append("\n");
            }
        }
        ExecutingCommand.runShellNative("echo -e '" + stringBuilder.toString() + "' >" + configPath(iface));
    }

    private List<Route> loadRoutesByIface(String iface) {
        ArrayList<Route> ret = new ArrayList<Route>();
        File fRoute = new File(configPath(iface));
        if (fRoute.exists()) {
            Properties prop = new Properties();
            try (FileInputStream fis = new FileInputStream(fRoute)) {
                prop.load(fis);
                for (int i = 0; i < 1024; i++) {
                    String gw = prop.getProperty("GATEWAY" + i);
                    String dest = prop.getProperty("ADDRESS" + i);
                    String mask = prop.getProperty("NETMASK" + i);
                    if ((gw == null || gw.length() == 0)
                            && (dest == null || dest.length() == 0)
                            && (mask == null || mask.length() == 0))
                        break;
                    Route route = new Route(dest, mask, gw, iface);
                    ret.add(route);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private int findRoute(Route route, List<Route> routeList) {
        int ret = -1;
        for (int i = 0; i < routeList.size(); i++) {
            Route each = routeList.get(i);
            if (each.getIface().equals(route.getIface())
                    && each.getMask().equals(route.getMask())
                    && each.getDestination().equals(route.getDestination())) {
                ret = i;
                break;
            }
        }
        return ret;
    }

    private String configPath(String iface) {
        return configDirectory + "/route-" + iface;
    }

    private String createShellParam(Route route) {
        String param = " -net " + route.getDestination() + " netmask " + route.getMask();
        /*if (!StringHelper.isEmpty(route.getGateway())) {
            param += (" gw " + route.getGateway());
        }*/

        if (!StringHelper.isEmpty(route.getIface())) {
            param += (" dev " + route.getIface());
        }
        return param;
    }
}
