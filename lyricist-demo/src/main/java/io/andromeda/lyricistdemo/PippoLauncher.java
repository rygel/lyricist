package io.andromeda.lyricistdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Pippo;

/**
 * Run application from here.
 */
public class PippoLauncher {
    /** The logger instance for this class. */
    private final static Logger log = LoggerFactory.getLogger(PippoLauncher.class);

    public static void main(String[] args) {
        final String applicationName = "Lyricist Demo";
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println(applicationName + " is closing normally.");
                log.info("---------------------------------------------------------------------------------------------------");
            }
        }, "Shutdown-thread"));


        log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.info("Welcome to the " + applicationName);
        Pippo pippo = new Pippo(new PippoApplication());
        /* The static folder for the css and js files. */
        pippo.getApplication().addFileResourceRoute("/assets", "public/assets/");
        pippo.start();

    }

}
