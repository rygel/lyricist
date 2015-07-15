package com.github.rygel.lyricistdemo;

import ro.pippo.core.Pippo;

/**
 * Run application from here.
 */
public class PippoLauncher {

    public static void main(String[] args) {
        Pippo pippo = new Pippo(new PippoApplication());
        /* The static folder for the css and js files. */
        pippo.getApplication().addFileResourceRoute("/assets", "public/assets/");
        pippo.start();
    }

}
