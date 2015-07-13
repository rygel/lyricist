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
        return textToSlug.replaceAll("\\s+|\\+|/|\\|*|:", "_").toLowerCase();
    }
}
