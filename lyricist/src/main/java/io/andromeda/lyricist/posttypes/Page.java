package io.andromeda.lyricist.posttypes;

import io.andromeda.lyricist.Constants;
import io.andromeda.lyricist.Utilities;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Alexander Brandt on 11.07.2015.
 */
public abstract class Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Page.class);

    protected Map<String,Object> frontMatter = new HashMap<>();
    protected Map<String,Object> context = new HashMap<>();
    protected String content = "";
    protected String filename;
    protected String slug;
    public String url;
    protected String layout;

    public Page() {

    }

    public Page(String newFilename) throws Exception {
        filename = newFilename;
        try {
            readFile();
        } catch (Exception ex) {
            LOGGER.error("Error reading file (" + filename + "): ", ex.getCause());
        }
    }

    public void addContext(String key, Object value) {
        context.put(key, value);
    }

    public String getContent() {
        return content;
    }

    public Map< String, Object> getFrontMatter() {
        return frontMatter;
    }

    public String getFilename() {
        return filename;
    }

    public String getSlug() {
        return slug;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getLayout() {
        return this.layout;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setUrl(String newUrl) {
        //LOGGER.debug("New set URL: " + newUrl);
        url = newUrl;
        frontMatter.put("url", newUrl);
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getDefaultContext() {
        final Map<String, Object> context = new TreeMap<>();
        context.put("content", getContent());
        context.put("post", getFrontMatter());

        return context;
    }

    protected final void readFile() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // detect YAML front matter
        String line = br.readLine();
        if (line == null) {
            LOGGER.warn("File \"" + filename + "\" is empty.");
            throw new Exception("File is empty: " + filename);
        }
        while (line.isEmpty()) {
            line = br.readLine();
        }
        if (!line.matches("[-]{3,}")) { // use at least three dashes
            throw new IllegalArgumentException("YAML Front Matter is missing in file: " + filename);
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

        // readFile data
        parseYamlFrontMatter(sb.toString());
        interpretYamlFrontMatterGeneral();
        interpretYamlFrontMatterSpecial();
        parseMarkdown(br);
    }

    /**
     * Parses the YAML front-matter.
     * @param yamlString A string containing the complete YAML front-matter
     */
    protected void parseYamlFrontMatter(String yamlString) {
        Yaml yaml = new Yaml();
        frontMatter = (Map< String, Object>) yaml.load(yamlString);
    }

    /**
     * General front matter entries, which should always be available.
     */
    private void interpretYamlFrontMatterGeneral() {
        String fmSlug = (String)frontMatter.get(Constants.SLUG_ID);
        if (fmSlug != null) {
            slug = fmSlug;
        } else {
            String filename2 = FilenameUtils.getName(filename);
            slug = FilenameUtils.removeExtension(filename2);
            slug = Utilities.slugify(slug);
            frontMatter.put("slug", slug);
        }
        layout = (String)frontMatter.get(Constants.LAYOUT_ID);
    }

    /**
     *  Special front matter entries. Needs to be taken care of in child class.
     */
    protected abstract void interpretYamlFrontMatterSpecial();

    protected void parseMarkdown(BufferedReader br) throws IOException {
        String markdown = org.apache.commons.io.IOUtils.toString(br);
        PegDownProcessor processor = new PegDownProcessor();
        content = processor.markdownToHtml(markdown);
    }

}
