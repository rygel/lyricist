package io.andromeda.lyricist;

import io.andromeda.lyricist.posttypes.Post;
import io.andromeda.lyricist.posttypes.StaticPage;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.equalTo;

/**
 *
 * @author Alexander Brandt
 */
public class StaticPageTest extends Assert {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testStaticPageFileNotFound() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("file_not_found.md (The system cannot find the file specified)");
        StaticPage staticPage = new StaticPage("file_not_found.md");
    }

    @Test
    public void testEmptyStaticPage() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("File is empty: ");
        StaticPage staticPage = new StaticPage(System.getProperty("user.dir") + "/src/test/resources/lyricist/blog/empty_post.md");
    }

    @Test
    public void testNoFrontMatter() throws Exception {
        thrown.expect(Exception.class);
        thrown.expectMessage("YAML/JSON Front Matter is missing in file: ");
        StaticPage staticPage = new StaticPage(System.getProperty("user.dir") + "/src/test/resources/lyricist/blog/no_front_matter.md");
    }

    @Test
    public void testNoFrontMatterSlug() throws Exception {
        String expected = "no_slug";
        StaticPage staticPage = new StaticPage(System.getProperty("user.dir") + "/src/test/resources/lyricist/blog/static_files/no_slug.md");
        String result = staticPage.getSlug();
        assertThat(expected, equalTo(result));
    }

}
