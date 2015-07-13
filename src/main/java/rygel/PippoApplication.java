package rygel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteHandler;
import rygel.lyricist.Layouts;
import rygel.lyricist.Lyricist;

import java.util.Map;
import java.util.TreeMap;

/**
 * A simple Pippo application.
 *
 * @see rygel.PippoLauncher#main(String[])
 */
public class PippoApplication extends Application {

    private final static Logger log = LoggerFactory.getLogger(PippoApplication.class);

    @Override
    protected void onInit() {
        getRouter().ignorePaths("/favicon.ico");

        // send 'Hello World' as response
        GET("/", new RouteHandler() {

            @Override
            public void handle(RouteContext routeContext) {
                routeContext.send("Hello World");
            }

        });

        // send a template as response
        GET("/template", new RouteHandler() {

            @Override
            public void handle(RouteContext routeContext) {
                String message;

                String lang = routeContext.getParameter("lang").toString();
                if (lang == null) {
                    message = getMessages().get("pippo.greeting", routeContext);
                } else {
                    message = getMessages().get("pippo.greeting", lang);
                }

                routeContext.setLocal("greeting", message);
                routeContext.render("hello");
            }

        });

        Map<String, Object> blogContext = new TreeMap<>();
        blogContext.put("pageTitle", "Lyricist Blog");
        Layouts layouts = new Layouts("blog", null, null, null, null, null);
        Lyricist lyricist = new Lyricist(this);
        lyricist.registerBlog("rootBlog", "/", layouts);
        lyricist.registerBlog("blog", "/blog/", layouts, blogContext);
    }

}
