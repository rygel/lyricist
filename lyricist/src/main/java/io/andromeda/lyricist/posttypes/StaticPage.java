package io.andromeda.lyricist.posttypes;

import io.andromeda.lyricist.Constants;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Brandt on 04.08.2016.
 */
public class StaticPage extends Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(StaticPage.class);

    public StaticPage(String newFilename) throws Exception {
        filename = newFilename;
        readFile();
    }

    protected void interpretFrontMatterSpecial() {

    }
}
