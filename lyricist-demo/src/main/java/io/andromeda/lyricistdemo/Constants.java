package io.andromeda.lyricistdemo;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class holding all application wide constants.
 * @author Alexander Brandt
 */
public class Constants {
    public static final String APPLICATION_NAME = "lyricist-demo";
    public static final String APPLICATION_VERSION = "0.2.1";
    public static final String APPLICATION_TITLE = "Lyricist Demo";
    public static final String APPLICATION_EMAIL = "lyricist@andromeda.io";
    public static final String ID_NAME = "name";
    public static final String ID_EMAIL = "useremail";
    public static final String ID_SUBJECT = "subject";
    public static final String ID_MESSAGE = "message";

  public static Map<String, Object> getDefaultContext() {
    final Map<String, Object> context = new TreeMap<>();
    context.put("apptitle", APPLICATION_TITLE);
    context.put("appver", APPLICATION_VERSION);
    context.put("email", APPLICATION_EMAIL);

    return context;
  }
}
