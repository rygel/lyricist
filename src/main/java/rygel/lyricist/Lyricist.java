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

/**
 * Created by Alexander Brandt on 08.06.2015.
 */
public final class Lyricist {
    private final static Logger LOGGER = LoggerFactory.getLogger(Lyricist.class);
    private final Application application;

    private Map<String, Blog> blogs = new HashMap<>();

    public Lyricist(Application application) {
        this.application = application;

        PippoSettings settings = application.getPippoSettings();
        List<String> blogStrings = settings.getStrings("lyricist.blogs");
        for (String blogString : blogStrings) {
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
                Blog blog = new Blog(splits[0], location);
                blogs.put(splits[0], blog);
                LOGGER.debug("Added blog \"" + splits[0] + "\" with its data directory: " + location + ".");
            }
        }
    }

    public void registerBlog(String blogName, String pattern, Layouts layouts) {
        registerBlog(blogName, pattern, layouts, null);
    }

    public void registerBlog(String name, final String pattern, final Layouts layouts, Map<String, Object> context) {
        if (!doesBlogExist(name)) {
            LOGGER.error("Cannot register blog. The blog with the name \"" + name + "\" does not exist!");
            return;
        }

        final Blog blog = blogs.get(name);
        blog.setLayouts(layouts);
        blog.putAllContext(context);
        preparePostings(blog, pattern);
        final Map<String, Post> posts = blog.getPosts();
        for (final Map.Entry<String, Post> entry : posts.entrySet()) {
            String route = pattern + Constants.AUTHORS_ROUTE + entry.getKey();
            final Post post = entry.getValue();
            post.setUrl(pattern + post.getContext().get(Constants.SLUG_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    context.putAll(post.getContext());
                    context.put("content", post.getContent());
                    context.put("post", post.getFrontMatter());

                    routeContext.render(post.getLayout(), context);
                }
            });
        }

        registerBlogAuthors(blog, pattern);
        registerBlogPage(blog, pattern);
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
        } else {
            return false;
        }
    }

    private void preparePostings(Blog blog, final String pattern) {
        final Map<String, Post> posts = blog.getPosts();
        Layouts layouts = blog.getLayouts();
        for (Map.Entry<String, Post> entry : posts.entrySet()) {
            Post post = entry.getValue();
            post.setUrl(pattern + post.getContext().get(Constants.SLUG_ID));

            // Layout check
            if (post.getLayout() == null) {
                // If there is no local layout use the global one
                post.setLayout(layouts.getPost());
            }
            if (post.getLayout() == null) {
                LOGGER.error("No layout available for post \"" + post.getFilename() + "\"! "
                        + "Please add either a global post layout via Layout or a local one via the posts front matter!");
            }
        }
    }

    private void registerBlogAuthors(final Blog blog, final String pattern) {
        final Map<String, Post> authors = blog.getAuthors();

        for (final Map.Entry<String, Post> entry : authors.entrySet()) {
            String route = pattern + Constants.AUTHORS_ROUTE + entry.getKey();
            Post post = entry.getValue();
            post.setUrl(pattern + Constants.AUTHORS_ROUTE + post.getContext().get(Constants.SLUG_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    final Post author = authors.get(entry.getKey());
                    context.putAll(author.getContext());
                    context.put("content", author.getContent());
                    context.put("post", author.getFrontMatter());
                    context.put("url", pattern + Constants.AUTHORS_ROUTE + author.getContext().get(Constants.SLUG_ID));
                    routeContext.render(author.getLayout(), context);
                }
            });
        }
    }

    private void registerBlogPage(final Blog blog, final String pattern) {
        String route = pattern;
        application.GET(route, new RouteHandler() {
            @Override
            public void handle(RouteContext routeContext) {
                final Map<String, Object> context = blog.getContext();
                context.put("url", pattern);
                context.put("blog", blog);
                context.put("posts", blog.getPosts());
                routeContext.render(blog.getLayouts().getBlog(), context);
            }
        });
    }

    private void registerCategories(final Blog blog, final String pattern) {
        final Map<String, Post> authors = blog.getPosts();

        for (final Map.Entry<String, Post> entry : authors.entrySet()) {
            String route = pattern + Constants.CATEGORIES_ROUTE + entry.getKey();
            Post post = entry.getValue();
            post.setUrl(pattern + Constants.AUTHORS_ROUTE + post.getContext().get(Constants.SLUG_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    final Post author = authors.get(entry.getKey());
                    context.putAll(author.getContext());
                    context.put("content", author.getContent());
                    context.put("post", author.getFrontMatter());
                    context.put("url", pattern + Constants.AUTHORS_ROUTE + author.getContext().get(Constants.SLUG_ID));
                    routeContext.render(author.getLayout(), context);
                }
            });
        }
    }

}
