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
    public void testEmptyPost() throws Exception {
        Post post = new Post("empty_post.md", null);
        thrown.expect(Exception.class);
        thrown.expectMessage("The uri pattern cannot be null or empty");
    }

}
