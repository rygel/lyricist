package rygel.lyricist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteHandler;
import ro.pippo.core.util.ClasspathUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Alexander on 08.06.2015.
 */
public final class Lyricist {
    private final static Logger LOGGER = LoggerFactory.getLogger(Lyricist.class);
    private static Lyricist lyricist;
    private final Application application;

    private Map<String, Blog> blogs = new HashMap<>();

    /*public static Lyricist getInstance() {
        if (lyricist == null) {
            lyricist = new Lyricist();
        }
        return lyricist;
    }*/

    public Lyricist (Application application) {
        this.application = application;

        PippoSettings settings = application.getPippoSettings();
        List<String> blogStrings = settings.getStrings("lyricist.blogs");
        for (String blogString: blogStrings) {
            String[] splits = blogString.split(":");
            if (splits.length != 2) {
                LOGGER.error("Wrong blog definition (" + blogString + ")! Must be [blogName]:[blogDirectory].");
                return;
            }
            URL location = ClasspathUtils.locateOnClasspath("lyricist/" + splits[1]);
            String basedir = System.getProperty("user.dir") + "/src/main/resources/lyricist/";
            Blog blog = new Blog(splits[0], basedir + splits[1]);
            blogs.put(splits[0], blog);
        }

        LOGGER.debug(blogStrings.toString());
    }

    public void registerLyricist(String blogName, String pattern) {
        if (!doesBlogExist(blogName)) {
            LOGGER.error("The blog with the name \"" + blogName + "\" does not exist!");
            return;
        }

        final Map<String, Post> posts = blogs.get(blogName).getPosts();
        for (final String item : posts.keySet()) {
            String route = pattern + item;
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = new TreeMap<String, Object>();
                    context.put("content", posts.get(item).getContent());
                    context.put("post", posts.get(item).getFrontMatter());
                    context.put("title", posts.get(item).getFrontMatter().get("title") + " - ") ; //+ Global.NAME);
                    routeContext.render("post", context);
                }
            });
        }
    }

    private boolean doesBlogExist(String blogName) {
        if (blogs.containsKey(blogName)) {
            return true;
        }
        else {
            return false;
        }
    }

}
