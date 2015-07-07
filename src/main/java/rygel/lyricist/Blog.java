package rygel.lyricist;

import org.apache.commons.io.FilenameUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander Brandt on 07.07.2015.
 */
public class Blog {
    private final static Logger LOGGER = LoggerFactory.getLogger(Blog.class);

    private String directory;
    private String name;
    private Map<String, Post> posts = new HashMap<>();

    public Blog(String newName, String newDirectory) {
        if (!Files.isDirectory(Paths.get(newDirectory))) {
            LOGGER.warn("The directory \"" + newDirectory + "\" does not exist!");
        }
        this.directory = newDirectory;
        this.name = newName;
        readDirectory();
    }

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

    private void readDirectory() {
        posts.clear();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                Post file = new Post(path.toString());
                posts.put(FilenameUtils.removeExtension(path.getFileName().toString()), file);
            }
        } catch (IOException ex) {
            LOGGER.error("[Blog: " + name + "] Error reading post directory (" + directory + "): ", ex.getCause());
        }
    }
}
