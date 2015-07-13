package rygel.lyricist;

/**
 * Created by Alexander Brandt on 11.07.2015.
 */
public class Utilities {

    /**
     * Protect the constructor.
     */
    private Utilities() {

    }

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
}
