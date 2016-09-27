package io.andromeda.lyricist.posttypes;

import io.andromeda.lyricist.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Brandt on 04.08.2016.
 */
public class Author extends Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Author.class);

    public Author(String newFilename) throws Exception {
        filename = newFilename;
        readFile();
    }

    public String getShortName() {
        return (String)context.get("shortName");
    }

    protected void interpretFrontMatterSpecial() {
        context.put("shortName", (String)frontMatter.get(Constants.SHORT_NAME_ID));
    }
}
