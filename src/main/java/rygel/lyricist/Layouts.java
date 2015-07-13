package rygel.lyricist;

/**
 * Created by Alexander Brandt on 13.07.2015.
 */
public class Layouts {
    private String blog;
    private String post;
    private String authors;
    private String author;
    private String categories;
    private String tags;

    public Layouts(String blog, String post, String authors, String author, String categories, String tags) {
        this.blog = blog;
        this.post = post;
        this.authors = authors;
        this.author = author;
        this.categories = categories;
        this.tags = tags;
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
}
