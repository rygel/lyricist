package com.github.rygel.lyricist;

import org.apache.commons.io.FilenameUtils;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander Brandt on 11.07.2015.
 */
public abstract class Page {
    private final static Logger LOGGER = LoggerFactory.getLogger(Post.class);

    private Map<String,Object> frontMatter = new HashMap<>();
    private Map<String,Object> context = new HashMap<>();
    private Map<String,Post> authors = new HashMap<>();
    private String content = "";
    private String filename;
    private String id;
    private Date published;
    private Date validUntil;

    public Page(String newFilename, Map<String, Post> authors) {
        filename = newFilename;
        this.authors = authors;
        try {
            parse();
        } catch (IOException ex) {
            LOGGER.error("Error reading file (" + filename + "): ", ex.getCause());
        }
    }

    public String getContent() {
        return content;
    }

    public Map< String, Object> getFrontMatter() {
        return frontMatter;
    }

    public String getID() {
        return id;
    }

    public String getLayout() {
        return (String)frontMatter.get(Constants.LAYOUT_ID);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    private final void parse() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // detect YAML front matter
        String line = br.readLine();
        if (line == null) {
            LOGGER.warn("File \"" + filename + "\" is empty.");
            return;
        }
        while (line.isEmpty()) {
            line = br.readLine();
        }
        if (!line.matches("[-]{3,}")) { // use at least three dashes
            throw new IllegalArgumentException("No YAML Front Matter");
        }
        final String delimiter = line;

        // scan YAML front matter
        StringBuilder sb = new StringBuilder();
        line = br.readLine();
        while (!line.equals(delimiter)) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }

        // parse data
        parseYamlFrontMatter(sb.toString());
        parseMarkdown(br);
    }

    private void parseYamlFrontMatter(String yamlString) {
        Yaml yaml = new Yaml();
        frontMatter = (Map< String, Object>) yaml.load(yamlString);
        String fmSlug = (String)frontMatter.get(Constants.SLUG_ID);
        if (fmSlug != null) {
            id = fmSlug;
        } else {
            //TODO: slugify!
            String filename2 = FilenameUtils.getName(filename);
            id = FilenameUtils.removeExtension(filename2);
        }
        context.put("slug", id);
        //String published2 = (String)frontMatter.get(Constants.PUBLISHED_ID);
        published = (Date)frontMatter.get(Constants.PUBLISHED_ID);
        validUntil = (Date)frontMatter.get(Constants.VALID_UNTIL_ID);
    }

    private void parseMarkdown(BufferedReader br) throws IOException {
        String markdown = org.apache.commons.io.IOUtils.toString(br);
        PegDownProcessor processor = new PegDownProcessor();
        content = processor.markdownToHtml(markdown);
    }
}
