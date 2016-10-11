package ${package};

import org.apache.commons.validator.routines.EmailValidator;
import org.simplejavamail.email.Email;
import org.simplejavamail.internal.util.ConfigLoader;
import org.simplejavamail.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.controller.Controller;

import javax.mail.Message;
import java.util.Map;

import static io.andromeda.Constants.getDefaultContext;

/**
 * The controller handling the contacts page
 */
public class ContactController extends Controller {
    /** The logger instance for this class. */
    private final static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    public final void contact() {
        final Map<String, Object> context = getDefaultContext();

        getResponse().render("contact_before_send", context);
    }

    public void postContact() {
        Map<String, Object> context = Constants.getDefaultContext();
        context.put("title", "Contact");

        String name = getRequest().getParameter(Constants.ID_NAME).toString();
        String useremail = getRequest().getParameter(Constants.ID_EMAIL).toString();
        String subject = getRequest().getParameter(Constants.ID_SUBJECT).toString();
        String message = getRequest().getParameter(Constants.ID_MESSAGE).toString();

        LOGGER.info("name: " + name
                + ", useremail: " + useremail
                + ", subject: " + subject
                + ", message: " + message);
        context.put(Constants.ID_NAME, name);
        context.put(Constants.ID_EMAIL, useremail);
        context.put(Constants.ID_SUBJECT, subject);
        context.put(Constants.ID_MESSAGE, message);

        boolean everythingOk = true;
        everythingOk = checkName(name, context) && everythingOk;
        everythingOk = checkEmail(useremail, context) && everythingOk;
        everythingOk = checkSubject(subject, context) && everythingOk;
        everythingOk = checkMessage(message, context) && everythingOk;

        if (!everythingOk) {
            getResponse().render("contact_before_send", context);
        } else

        {
            sendContactEmail(context);
            getResponse().render("contact_send_successful", context);
        }
    }

    public static void sendContactEmail(Map<String, Object> context) {
        ConfigLoader.loadProperties("conf/simplejavamail.properties", false); // optional default

        Email email = new Email();
        email.addRecipient("Andromeda.io Contact Form", "info@andromeda.io", Message.RecipientType.TO);
        email.setFromAddress((String) context.get(Constants.ID_NAME), (String) context.get(Constants.ID_EMAIL));
        email.setSubject((String) context.get(Constants.ID_SUBJECT));
        email.setText((String) context.get(Constants.ID_MESSAGE));

        new Mailer().sendMail(email);
    }

    private static boolean checkName(final String name, Map<String, Object> context) {
        if (name.isEmpty()) {
            context.put("alert_name", "Please enter your name!");
            return false;
        }
        return true;
    }

    private static boolean checkEmail(final String email, Map<String, Object> context) {
        if (email.isEmpty()) {
            context.put("alert_email", "Please enter your email address!");
            return false;
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            context.put("alert_email", "Email is not valid!");
            return false;
        }

        return true;
    }

    private static boolean checkSubject(final String subject, Map<String, Object> context) {
        if (subject.isEmpty()) {
            context.put("alert_subject", "Please enter a subject!");
            return false;
        }
        return true;
    }

    private static boolean checkMessage(final String message, Map<String, Object> context) {
        if (message.isEmpty()) {
            context.put("alert_message", "Please enter your message!");
            return false;
        }
        return true;
    }

}
