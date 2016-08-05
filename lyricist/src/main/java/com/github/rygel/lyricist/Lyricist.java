package com.github.rygel.lyricist;

import com.github.rygel.lyricist.posttypes.Author;
import com.github.rygel.lyricist.posttypes.Post;
import com.github.rygel.lyricist.posttypes.StaticPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.PippoSettings;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteHandler;
import ro.pippo.core.util.ClasspathUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexander Brandt
 */
public final class Lyricist {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Lyricist.class);
    /** Reference to the Pippo application instance. */
    private final Application application;

    private Map<String, Blog> blogs = new HashMap<>();

    /**
     * Creates the Lyricist instance. Only one instance is needed in a Pippo application.
     * @param application The Pippo Application instance. Usually always this.
     */
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
                try {
                    Blog blog = new Blog(splits[0], location);
                    blogs.put(splits[0], blog);
                    LOGGER.debug("Added blog \"" + splits[0] + "\" with its data directory: " + location + ".");
                } catch (Exception e) {
                    LOGGER.error("Error during blog creation: " + e.getMessage());
                }
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
        blog.url = pattern;
        preparePostings(blog, pattern);
        final Map<String, Post> posts = blog.getPosts();
        for (final Map.Entry<String, Post> entry : posts.entrySet()) {
            String route = pattern + entry.getKey();
            final Post post = entry.getValue();
            blog.addCategory(post.getFrontMatter().get(Constants.CATEGORIES_ID));
            //Route checking here?
            //Router router = application.getRouter();
            //List<Route> routes = router.getRoutes();
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    context.putAll(post.getContext());
                    context.put("content", post.getContent());
                    context.put("post", post.getFrontMatter());
                    context.put("blog", blog.globalContext);
                    context.put("blogUrl", blog.url);

                    routeContext.render(post.getLayout(), context);
                }
            });
        }

        registerBlogPage(blog, pattern);
        registerBlogAuthors(blog, pattern);
        registerAuthorsPage(blog, pattern);
        registerCategories(blog, pattern);
        registerStaticPages(blog, pattern);
    }

    public Blog getBlog(String name) {
        if (doesBlogExist(name)) {
            return blogs.get(name);
        } else {
            return null;
        }
    }

    private boolean doesBlogExist(String name) {
        return blogs.containsKey(name);
    }

    private void preparePostings(Blog blog, final String pattern) {
        final Map<String, Post> posts = blog.getPosts();
        Layouts layouts = blog.getLayouts();
        final List<Object> globalContext = new ArrayList<>();
        for (Map.Entry<String, Post> entry : posts.entrySet()) {
            Post post = entry.getValue();
            post.setUrl(pattern + post.getFrontMatter().get(Constants.SLUG_ID));
            globalContext.add(post.getFrontMatter());

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
        blog.globalContext = globalContext;
    }

    private void registerBlogPage(final Blog blog, final String pattern) {
        String route = Utilities.removeTrailingSlash(pattern);
        application.GET(route, new RouteHandler() {
            @Override
            public void handle(RouteContext routeContext) {
                final Map<String, Object> context = blog.getContext();
                context.put("url", pattern);
                context.put("blog", blog.globalContext);
                context.put("posts", blog.getPosts());
                routeContext.render(blog.getLayouts().getBlog(), context);
            }
        });
    }

    private void registerBlogAuthors(final Blog blog, final String pattern) {
        final Map<String, Author> authors = blog.getAuthors();

        for (final Map.Entry<String, Author> entry : authors.entrySet()) {
            String route = pattern + Constants.AUTHORS_ROUTE + entry.getKey();
            Author author = entry.getValue();
            author.setUrl(pattern + Constants.AUTHORS_ROUTE + author.getFrontMatter().get(Constants.SHORT_NAME_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    final Author author = authors.get(entry.getKey());
                    context.putAll(author.getContext());
                    context.put("content", author.getContent());
                    context.put("post", author.getFrontMatter());
                    context.put("authors", blog.getAuthorsList());
                    //context.put("url", pattern + Constants.AUTHORS_ROUTE + author.getContext().get(Constants.SLUG_ID));
                    routeContext.render(author.getLayout(), context);
                }
            });
        }
    }

    private void registerAuthorsPage(final Blog blog, final String pattern) {
        if (blog.getAuthorsList().size() != 0) {
            String route = Utilities.removeTrailingSlash(pattern + Constants.AUTHORS_ROUTE);
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    context.put("url", pattern);
                    context.put("blog", blog.globalContext);
                    context.put("authors", blog.getAuthorsList());
                    routeContext.render(blog.getLayouts().getAuthors(), context);
                }
            });
        } else {
            LOGGER.info("No author page for blog \"" + blog.getName() + "\" created." );
        }
    }

    private void registerStaticPages(final Blog blog, final String pattern) {
        final Map<String, StaticPage> staticPages = blog.getStaticPages();

        for (final Map.Entry<String, StaticPage> entry : staticPages.entrySet()) {
            String route = pattern + entry.getKey();
            StaticPage staticPage = entry.getValue();
            staticPage.setUrl(pattern + Constants.AUTHORS_ROUTE + staticPage.getFrontMatter().get(Constants.SHORT_NAME_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    final StaticPage staticPage = staticPages.get(entry.getKey());
                    context.putAll(staticPage.getContext());
                    context.put("content", staticPage.getContent());
                    context.put("post", staticPage.getFrontMatter());
                    //context.put("authors", blog.getAuthorsList());
                    //context.put("url", pattern + Constants.AUTHORS_ROUTE + author.getContext().get(Constants.SLUG_ID));
                    routeContext.render(staticPage.getLayout(), context);
                }
            });
        }
    }

    private void registerCategories(final Blog blog, final String pattern) {
        final Map<String, Post> posts = blog.getPosts();

        for (final Map.Entry<String, Post> entry : posts.entrySet()) {
            String route = pattern + Constants.CATEGORIES_ROUTE + entry.getKey();
            Post post = entry.getValue();
            //post.setUrl(pattern + Constants.AUTHORS_ROUTE + post.getContext().get(Constants.SLUG_ID));
            application.GET(route, new RouteHandler() {
                @Override
                public void handle(RouteContext routeContext) {
                    final Map<String, Object> context = blog.getContext();
                    final Post post = posts.get(entry.getKey());
                    context.putAll(post.getContext());
                    context.put("content", post.getContent());
                    context.put("post", post.getFrontMatter());
                    context.put("url", pattern + Constants.AUTHORS_ROUTE + post.getContext().get(Constants.SLUG_ID));
                    routeContext.render(post.getLayout(), context);
                }
            });
        }
    }

}
