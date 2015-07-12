package rygel.lyricist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Created by Alexander Brandt on 07.07.2015.
 */
public class Blog {
    private final static Logger LOGGER = LoggerFactory.getLogger(Blog.class);

    private String directory;
    private String postsDirectory;
    private String authorsDirectory;
    private String name;
    private Map<String, Post> posts = new HashMap<>();
    private SortedMap<Date, Post> postsOrderedByDate = new TreeMap<>();
    private Map<String, Object> context = new HashMap<>();
    private Map<String, Post> authors = new HashMap<>();
    private Map<String, List<Post>> postsByCategory = new TreeMap<>();
    private Map<String, List<Post>> postsByTag = new TreeMap<>();

    private String checkIfDirectoryExists(String newDirectory) {
        File file = new File(newDirectory);
        if (!Files.isDirectory(Paths.get(file.getAbsolutePath()))) {
            LOGGER.warn("The directory \"" + newDirectory + "\" does not exist!");
            return "";
        }
        return file.getAbsolutePath();
    }

    public Blog(String newName, URL newDirectory) {
        String mainDirectory = "";
        try {
            mainDirectory = checkIfDirectoryExists(newDirectory.toURI().getPath());
        } catch (URISyntaxException e) {
            LOGGER.warn("The directory \"" + newDirectory + "\" does not exist!");
        }

        String authorDirectory = mainDirectory + Constants.AUTHORS_DIR;
        this.authorsDirectory = checkIfDirectoryExists(authorDirectory);

        String postsDirectory = mainDirectory + Constants.POSTS_DIR;
        this.postsDirectory = checkIfDirectoryExists(postsDirectory);

        this.directory = mainDirectory;
        this.name = newName;
        readAuthorsDirectory();
        readPostsDirectory();
    }

    public void reload() {
        readPostsDirectory();
        readAuthorsDirectory();
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

    public Map<String, Post> getAuthors() {
        return authors;
    }

    public Map<String, List<Post>> getCategories() {
        return postsByCategory;
    }

    public Map<String, List<Post>> getTags() {
        return postsByTag;
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

    private void readPostsDirectory() {
        posts.clear();
        posts.putAll(readDirectory(postsDirectory));
    }

    private void readAuthorsDirectory() {
        if (!authorsDirectory.equals("")) {
            authors.clear();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(authorsDirectory))) {
                for (Path path : directoryStream) {
                    Post post = new Post(path.toString(), authors);
                    authors.put(post.getShortName(), post);
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
                Post post = new Post(path.toString(), authors);
                result.put(post.getSlug(), post);
                addPostToCategories(post);
                addPostToTags(post);
            }
        } catch (IOException ex) {
            LOGGER.error("[Blog: " + name + "] Error reading post directory (" + directory + "): ", ex.getCause());
        }
        return result;
    }

    private void addPostToCategories(Post post) {
        List<String> categories = (List<String>)post.getFrontMatter().get(Constants.CATEGORY_ID);
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
