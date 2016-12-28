package ${package};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Controller;

import java.util.Map;

import static ${rootArtifactId}.Constants.getDefaultContext;

/**
 *
 * The Main Controller
 */
public class MainController extends Controller {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    public final void index() {
        final Map<String, Object> context = getDefaultContext();
        context.put("title", "Login - Superintendent Website Management Interface");

        getResponse().render("index", context);
    }

    public final void portfolio() {
        final Map<String, Object> context = getDefaultContext();
        context.put("title", "Login - Superintendent Website Management Interface");
        context.put("email", "info@alexbrandt.photography");

        getResponse().render("portfolio", context);
    }

}
