package com.github.rygel.lyricist;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by Alexander Brandt on 15.07.2015.
 */
public class PostTest extends Assert {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testPostFileNotFound() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("File is empty: ");
        Post post = new Post("file_not_found.md", null);
    }

    @Test
    public void testEmptyPost() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("File is empty: ");
        Post post = new Post("empty_post.md", null);
    }

}
