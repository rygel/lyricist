package io.andromeda.lyricist.posttypes;

import io.andromeda.lyricist.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexander Brandt
 */
public class Post extends Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Post.class);

    private Map<String, Author> authors = new HashMap<>();
    private Boolean draft;
    private String shortName = "";
    private Date published;
    private Date validUntil;

    public Post(String newFilename, Map<String, Author> authors) throws Exception {
        filename = newFilename;
        this.authors = authors;
        readFile();
        addAuthors();
    }

    public Boolean getDraft() {
        return draft;
    }

    protected void interpretFrontMatterSpecial() {
        published = (Date)frontMatter.get(Constants.PUBLISHED_ID);
        validUntil = (Date)frontMatter.get(Constants.VALID_UNTIL_ID);
        draft = (Boolean)frontMatter.get(Constants.DRAFT_ID);
        if (draft == null) {
            draft = false;
        }
    }

    private void addAuthors() {
        List<String> shortAuthors = (List<String>)frontMatter.get("authors");
        if ((authors != null) && (shortAuthors != null)) {
            List<Author> authorList = new ArrayList<>();
            for (String shortAuthor: shortAuthors) {
                Author author = authors.get(shortAuthor);
                authorList.add(author);
            }
            context.put("authors", authorList);
        }
    }
}
