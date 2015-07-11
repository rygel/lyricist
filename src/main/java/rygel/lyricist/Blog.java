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
import java.util.*;

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

    public Blog(String newName, URL newDirectory) {
        File file;
        try {
            file = new File(newDirectory.toURI());
        } catch(URISyntaxException e) {
            file = new File(newDirectory.getPath());
        }

        if (!Files.isDirectory(Paths.get(file.getAbsolutePath()))) {
            LOGGER.warn("The directory \"" + newDirectory + "\" does not exist!");
        }
        this.directory = file.getAbsolutePath();
        this.name = newName;
        readDirectory();
    }

    public void reload() {
        readDirectory();
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

    private void readDirectory() {
        posts.clear();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                Post post = new Post(path.toString());
                posts.put(post.getSlug(), post);
            }
        } catch (IOException ex) {
            LOGGER.error("[Blog: " + name + "] Error reading post directory (" + directory + "): ", ex.getCause());
        }
    }
}
