package io.andromeda.lyricist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

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
        Path path = Paths.get(file.getAbsolutePath());
        if (!Files.isDirectory(path)) {
            LOGGER.warn("The directory \"{}\" does not exist! Creating it.", newDirectory);
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                LOGGER.error("Cannot create directory \"{}\"!", e.getMessage());
                System.exit(1);
            }
        }
        return file.getAbsolutePath();
    }

  private static Random random = new Random(0x2626);

  public static String obfuscate(String email) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < email.length(); i++) {
      char c = email.charAt(i);
      switch (random.nextInt(5)) {
        case 0:
        case 1:
          sb.append("&#").append((int) c).append(';');
          break;
        case 2:
        case 3:
          sb.append("&#x").append(Integer.toHexString(c)).append(';');
          break;
        case 4:
          String encoded = encode(c);
          if (encoded != null) sb.append(encoded); else sb.append(c);
      }
    }
    return sb.toString();
  }

  public static String encode(char c) {
    switch (c) {
      case '&':  return "&amp;";
      case '<':  return "&lt;";
      case '>':  return "&gt;";
      case '"':  return "&quot;";
      case '\'': return "&#39;";
    }
    return null;
  }

}
