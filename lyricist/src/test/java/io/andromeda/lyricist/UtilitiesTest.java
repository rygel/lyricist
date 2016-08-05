package io.andromeda.lyricist;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * @author Alexander Brandt
 */
public class UtilitiesTest {

    @Test
    public void testRemoveTrailingSlashRoot() throws Exception {
        String expected = "/";
        String result = Utilities.removeTrailingSlash("/");
        assertThat(expected, equalTo(result));
    }

    @Test
    public void testRemoveTrailingSlashLevel1() throws Exception {
        String expected = "blog";
        String result = Utilities.removeTrailingSlash("blog/");
        assertThat(expected, equalTo(result));
    }

    @Test
    public void testRemoveTrailingSlashLevel3() throws Exception {
        String expected = "/blog/test/local";
        String result = Utilities.removeTrailingSlash("/blog/test/local/");
        assertThat(expected, equalTo(result));
    }

    @Test
    public void testRemoveTrailingSlashDoNothing() throws Exception {
        String expected = "/blog";
        String result = Utilities.removeTrailingSlash("/blog");
        assertThat(expected, equalTo(result));
    }
}
