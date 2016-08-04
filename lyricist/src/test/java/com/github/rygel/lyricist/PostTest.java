package com.github.rygel.lyricist;

import com.github.rygel.lyricist.posttypes.Post;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Alexander Brandt
 */
public class PostTest extends Assert {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testPostFileNotFound() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("file_not_found.md (The system cannot find the file specified)");
        Post post = new Post("file_not_found.md", null);
    }

    @Test
    public void testEmptyPost() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("File is empty: ");
        Post post = new Post(System.getProperty("user.dir") + "/src/test/resources/lyricist/blog/empty_post.md", null);
    }

    @Test
    public void testNoFrontMatter() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("YAML Front Matter is missing in file: ");
        Post post = new Post(System.getProperty("user.dir") + "/src/test/resources/lyricist/blog/no_front_matter.md", null);
    }

}
