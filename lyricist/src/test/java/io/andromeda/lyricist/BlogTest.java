package io.andromeda.lyricist;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ro.pippo.core.util.ClasspathUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.startsWith;

/**
 * @author Alexander Brandt
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

    @Test
    public void testBlogDirectoryIsOutsideClassPath() throws Exception {
        Path path = Paths.get("lyricist", "data", "blog");
        Files.createDirectories(path);
        URL location =  path.toUri().toURL();
        Blog blog = new Blog("test", location);
        //Files.deleteIfExists(path);
    }
}
