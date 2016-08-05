package io.andromeda.lyricist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Alexander Brandt on 11.07.2015.
 */
public class Utilities {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    /**
     * Protect the constructor.
     */
    private Utilities() {

    }

    /**
     * Remove characters which are not allowed in a slug.
     * @param textToSlug
     * @return
     */
    public static String slugify(String textToSlug) {
        return textToSlug.replaceAll("\\s+|/|\\|:", "_").toLowerCase();
    }

    /**
     * Remove the trailing slash, but beware of "/".
     * @param pattern the URL pattern to check.
     * @return the pattern without the trailing slash.
     */
    public static String removeTrailingSlash(String pattern) {
        if ((!pattern.equals("/")) && (pattern.endsWith("/"))) {
            return pattern.substring(0, pattern.length() - 1);
        } else {
            return pattern;
        }
    }

    /**
     * Checks if a directory exists.
     * @param newDirectory
     * @return
     */
    public static String checkIfDirectoryExists(String newDirectory) {
        File file = new File(newDirectory);
        if (!Files.isDirectory(Paths.get(file.getAbsolutePath()))) {
            LOGGER.warn("The directory \"" + newDirectory + "\" does not exist!");
            return "";
        }
        return file.getAbsolutePath();
    }
}
