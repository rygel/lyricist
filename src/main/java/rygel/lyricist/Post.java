package rygel.lyricist;

import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 08.06.2015.
 */
public class Post {
    private final static Logger logger = LoggerFactory.getLogger(Post.class);

    private Map< String, Object> frontMatter = new HashMap<>();
    private String content = "";
    private String filename;

    public Post(String newFilename) {
        filename = newFilename;
        try {
            parse();
        } catch (IOException ex) {
            logger.error("Error reading post (" + filename + "): ", ex.getCause());
        }
    }

    public String getContent() {
        return content;
    }

    public Map< String, Object> getFrontMatter() {
        return frontMatter;
    }

    public final void parse() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // detect YAML front matter
        String line = br.readLine();
        if (line == null) {
            logger.warn("File \"" + filename + "\" is empty.");
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
    }

    private void parseMarkdown(BufferedReader br) throws IOException {
        String markdown = org.apache.commons.io.IOUtils.toString(br);
        PegDownProcessor processor = new PegDownProcessor();
        content = processor.markdownToHtml(markdown);
    }
}
