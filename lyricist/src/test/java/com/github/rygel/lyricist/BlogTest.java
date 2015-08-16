package com.github.rygel.lyricist;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ro.pippo.core.util.ClasspathUtils;

import java.net.URL;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by Alexander Brandt on 15.07.2015.
 */
public class BlogTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBlogDirectoryDoesNotExist() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage(startsWith("The blog directory is empty! Blog:"));
        URL location = ClasspathUtils.locateOnClasspath("lyricist/test");
        Blog blog = new Blog("test", location);
    }
}
