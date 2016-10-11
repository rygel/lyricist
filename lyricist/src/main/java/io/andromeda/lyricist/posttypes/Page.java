package io.andromeda.lyricist.posttypes;

import com.alibaba.fastjson.JSON;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import io.andromeda.lyricist.Constants;
import io.andromeda.lyricist.Utilities;
import org.apache.commons.io.FilenameUtils;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import ro.pippo.core.util.ClasspathUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static ro.pippo.core.util.ClasspathUtils.*;

/**
 * Created by Alexander Brandt on 11.07.2015.
 */
public abstract class Page {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(Page.class);

    protected Map<String,Object> frontMatter = new HashMap<>();
    protected Map<String,Object> context = new HashMap<>();
    protected FrontMatterType frontMatterType;
    protected String content = "";
    protected String filename;
    protected Path path;
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
        // First try the classpath
        URL url = locateOnClasspath(filename);
        if (url == null) {
            Path path = Paths.get(filename);
            if (Files.exists(path)) {
              url = path.toUri().toURL();
            } else {
              LOGGER.error("Cannot load file \"" + filename + "\"!");
              throw new Exception(filename + " (The system cannot find the file specified)");
            }
        }
        path = Paths.get(url.toURI());
        BufferedReader br = new BufferedReader(new FileReader(url.getFile()));
        final String delimiter;

        // detect YAML front matter
        String line = br.readLine();
        if (line == null) {
            LOGGER.warn("File \"" + filename + "\" is empty.");
            throw new Exception("File is empty: " + path.normalize().toAbsolutePath().toString());
        }
        while (line.isEmpty()) {
            line = br.readLine();
        }
        if (!line.matches("[-]{3,}")) { // use at least three dashes or opening curly braces
            if (!line.matches("[{]{3,}")) {
                throw new IllegalArgumentException("YAML/JSON Front Matter is missing in file: " + path.normalize().toString());
            } else {
                frontMatterType = FrontMatterType.JSON;
                delimiter = "}}}";
            }
        } else {
            frontMatterType = FrontMatterType.YAML;
            delimiter = line;
        }

        // scan front matter
        StringBuilder sb = new StringBuilder();
        if (frontMatterType == FrontMatterType.JSON) {
            sb.append("{");
        }
        line = br.readLine();
        while (!line.equals(delimiter)) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        if (frontMatterType == FrontMatterType.JSON) {
            sb.append("}");
        }

        // readFile data
        if (frontMatterType == FrontMatterType.YAML) {
            parseYamlFrontMatter(sb.toString());
        } else {
            parseJsonFrontMatter(sb.toString());
        }

        interpretFrontMatterGeneral();
        interpretFrontMatterSpecial();
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
    private void interpretFrontMatterGeneral() {
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
    protected abstract void interpretFrontMatterSpecial();

    /**
     * Parses the JSON front-matter.
     * @param jsonString A string containing the complete YAML front-matter
     */
    protected void parseJsonFrontMatter(String jsonString) {

        frontMatter = (Map< String, Object>) JSON.parse(jsonString);
    }

    protected void parseMarkdown(BufferedReader br) throws IOException {
        String markdown = org.apache.commons.io.IOUtils.toString(br);
        //PegDownProcessor processor = new PegDownProcessor();
        //content = processor.markdownToHtml(markdown);
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        content = renderer.render(document);
    }

}
