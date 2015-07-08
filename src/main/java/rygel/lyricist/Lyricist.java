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
 * Created by Alexander Brandt on 08.06.2015.
 */
public final class Lyricist {
    private final static Logger LOGGER = LoggerFactory.getLogger(Lyricist.class);
    private final Application application;

    private Map<String, Blog> blogs = new HashMap<>();

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
            if (location == null) {
                LOGGER.error("The directory for the blog data (\"" + splits[1] + "\") for blog \"" + splits[0]
                        + "\" does not exist or does not contain any files! The blog will not be loaded.");
            } else {
                String basedir = System.getProperty("user.dir") + "/src/main/resources/lyricist/";
                Blog blog = new Blog(splits[0], location);
                blogs.put(splits[0], blog);
                LOGGER.debug("Added blog \"" + splits[0] + "\" with its data directory: " + location + ".");
            }
        }
    }

    public void registerBlog(String blogName, String pattern) {
            registerBlog(blogName, pattern, null);
    }

    public void registerBlog(String name, String pattern, Map<String, Object> context) {
        if (!doesBlogExist(name)) {
            LOGGER.error("Cannot register blog. The blog with the name \"" + name + "\" does not exist!");
            return;
        }

        final Blog blog = blogs.get(name);
        blog.putAllContext(context);
        final Map<String, Post> posts = blog.getPosts();
        for (final String item : posts.keySet()) {
            String route = pattern + item;
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    context.put("content", posts.get(item).getContent());
                    context.put("header", posts.get(item).getFrontMatter());
                    context.put("title", posts.get(item).getFrontMatter().get("title")) ;
                    routeContext.render("post", context);
                }
            });
        }
    }

    public Blog getBlog(String name) {
        if (doesBlogExist(name)) {
            return blogs.get(name);
        } else {
            return null;
        }
    }

    private boolean doesBlogExist(String name) {
        if (blogs.containsKey(name)) {
            return true;
        }
        else {
            return false;
        }
    }

}
