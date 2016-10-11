package io.andromeda.lyricist;

import io.andromeda.lyricist.posttypes.Author;
import io.andromeda.lyricist.posttypes.Post;
import io.andromeda.lyricist.posttypes.StaticPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The main blog class.
 *
 * Created by Alexander Brandt on 07.07.2015.
 */
public class Blog {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Blog.class);

    private String directory;
    private String postsDirectory;
    private String authorsDirectory;
    private String staticPagesDirectory;
    private String name;
    private Layouts layouts;
    private Map<String, Post> posts = new HashMap<>();
    private Map<String, Post> drafts = new HashMap<>();
    private Map<String, StaticPage> staticPages = new HashMap<>();
    private SortedMap<Date, Post> postsOrderedByDate = new TreeMap<>();
    private Map<String, Object> context = new HashMap<>();
    private Map<String, Author> authors = new HashMap<>();
    private Map<String, List<Post>> postsByCategory = new TreeMap<>();
    private Map<String, List<Post>> postsByTag = new TreeMap<>();
    public List<Object> globalContext = new ArrayList<>();
    public Set<String> categories = new TreeSet<>();
    public String url;

    public Blog(String newName, URL newDirectory) throws Exception {
        String mainDirectory;

        if (newDirectory == null) {
            throw new Exception("The blog directory is empty! Blog: " + newName);
        }

        if (newDirectory.getProtocol().equals("jar")) {

        }

        try {
            mainDirectory = Utilities.checkIfDirectoryExists(newDirectory.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new Exception("The blog directory does not exist: " + newDirectory.toString());
        }

        String authorDirectory = mainDirectory + Constants.AUTHORS_DIR;
        this.authorsDirectory = Utilities.checkIfDirectoryExists(authorDirectory);

        String postsDirectory = mainDirectory + Constants.POSTS_DIR;
        this.postsDirectory = Utilities.checkIfDirectoryExists(postsDirectory);

        String staticPagesDirectory = mainDirectory + Constants.STATIC_PAGES_DIR;
        this.staticPagesDirectory = Utilities.checkIfDirectoryExists(staticPagesDirectory);

        this.directory = mainDirectory;
        this.name = newName;
        readAuthorsDirectory();
        readPostsDirectory();
        readStaticPagesDirectory();
    }

    /**
     * Method to reload all the files.
     * TODO: final implementation needed!
     */
    public void reload() {
        readPostsDirectory();
        readAuthorsDirectory();
        readStaticPagesDirectory();
    }

    public String getName() {
        return name;
    }

    public Map<String, Post> getPosts() {
        return posts;
    }

    public Post getPost(String name) {
        return posts.get(name);
    }

    public SortedMap<Date, Post> getPosts(Date from, Date to) {
        return postsOrderedByDate;
    }

    public Map<String, StaticPage> getStaticPages() {
        return staticPages;
    }

    public Map<String, Author> getAuthors() {
        return authors;
    }

    public List<Author> getAuthorsList() {
        return new ArrayList<Author>(authors.values());
    }

    public Map<String, List<Post>> getCategories() {
        return postsByCategory;
    }

    public Map<String, List<Post>> getTags() {
        return postsByTag;
    }

    public void addCategory(Object categories) {
        if (categories != null) {
            if (categories.getClass() == String.class) {
                this.categories.add((String)categories);
            } else if (categories.getClass() == ArrayList.class) {
                this.categories.addAll((ArrayList)categories);
            } else {
                LOGGER.info("fewaf " + categories.getClass());
            }
        }
    }

    public void putAllContext(Map<String, Object> context) {
        if (context != null) {
            this.context.putAll(context);
        } else {
            LOGGER.debug("Null object given for context of blog \"" + name + "\".");
        }
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public Layouts getLayouts() {
        return layouts;
    }

    public void setLayouts(Layouts layouts) {
        this.layouts = layouts;
    }

    private void readPostsDirectory() {
        posts.clear();
        posts.putAll(readDirectory(postsDirectory));
    }

    private void readAuthorsDirectory() {
        if (!authorsDirectory.equals("")) {
            authors.clear();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(authorsDirectory))) {
                for (Path path : directoryStream) {
                    try {
                        Author author = new Author(path.normalize().toString());
                        authors.put(author.getShortName(), author);
                    } catch (Exception e) {
                    LOGGER.error(e.toString());
                    }
                }
            } catch (IOException ex) {
                LOGGER.error("[Blog: " + name + "] Error reading authors directory (" + directory + "): ", ex.getCause());
            }
        } else {
            LOGGER.info("There are no authors for blog \"" + name + "\".");
        }
    }

    private Map<String, Post> readDirectory(String directory) {
        Map<String, Post> result = new TreeMap<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                try {
                    Post post = new Post(path.normalize().toString(), authors);
                    if (post.getDraft()) {
                        drafts.put(post.getSlug(), post);
                    } else {
                        result.put(post.getSlug(), post);
                    }
                    addPostToCategories(post);
                    addPostToTags(post);
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        } catch (IOException ex) {
            LOGGER.error("[Blog: " + name + "] Error reading post directory (" + directory + "): ", ex.getCause());
        }
        return result;
    }

    private void readStaticPagesDirectory() {
        if (!staticPagesDirectory.equals("")) {
            staticPages.clear();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(staticPagesDirectory))) {
                for (Path path : directoryStream) {
                    try {
                        StaticPage staticPage = new StaticPage(path.normalize().toString());
                        staticPages.put(staticPage.getSlug(), staticPage);
                    } catch (Exception e) {
                        LOGGER.error(e.toString());
                    }
                }
            } catch (IOException ex) {
                LOGGER.error("[Blog: " + name + "] Error reading authors directory (" + directory + "): ", ex.getCause());
            }
        } else {
            LOGGER.info("There are no authors for blog \"" + name + "\".");
        }
    }

    private void addPostToCategories(Post post) {
        List<String> categories = (List<String>)post.getFrontMatter().get(Constants.CATEGORIES_ID);
        if (categories != null) {
            for (String category: categories) {
                List<Post> currentCategory = postsByCategory.get(category);
                if (currentCategory == null) {
                    currentCategory = new ArrayList<>();
                    postsByCategory.put(category, currentCategory);
                }
                currentCategory.add(post);
            }
        }
    }

    private void addPostToTags(Post post) {
        List<String> tags = (List<String>)post.getFrontMatter().get(Constants.TAGS_ID);
        if (tags != null) {
            for (String tag: tags) {
                List<Post> currentTag = postsByCategory.get(tag);
                if (currentTag == null) {
                    currentTag = new ArrayList<>();
                    postsByTag.put(tag, currentTag);
                }
                currentTag.add(post);
            }
        }
    }

}
