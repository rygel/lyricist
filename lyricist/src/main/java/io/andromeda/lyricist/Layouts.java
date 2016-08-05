package io.andromeda.lyricist;

/**
 * The Layouts class. Here different default layouts, i.e. references to template files, for the blog page,
 * the blog posts, the authors page, the authors, the categories, the tags, and the static pages can be set.
 * @author Alexander Brandt
 */
public class Layouts {
    private String blog;
    private String post;
    private String authors;
    private String author;
    private String categories;
    private String tags;
    private String staticPages;

    public Layouts(String blog, String post, String authors, String author, String categories, String tags, String staticPages) {
        this.blog = blog;
        this.post = post;
        this.authors = authors;
        this.author = author;
        this.categories = categories;
        this.tags = tags;
        this.staticPages = staticPages;
    }

    public String getBlog() {
        return blog;
    }

    public String getPost() {
        return post;
    }

    public String getAuthors() {
        return authors;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategories() {
        return categories;
    }

    public String getTags() {
        return tags;
    }

    public String getStaticPages() {
        return staticPages;
    }
}
