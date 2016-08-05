package com.github.rygel.lyricist.posttypes;

import com.github.rygel.lyricist.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Brandt on 04.08.2016.
 */
public class StaticPage extends Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(StaticPage.class);

    private String route;

    public StaticPage(String newFilename) throws Exception {
        filename = newFilename;
        readFile();
    }

    public String getRoute() {
        return route;
    }

    protected void interpretYamlFrontMatterSpecial() {
        route = (String)frontMatter.get(Constants.ROUTE_ID);
        if (route.startsWith("/")) {
            LOGGER.warn("A static page should never start with a \"/\"!");
        }
        context.put("route", route);

    }
}
