package taller6;

/**
 * This class simulates an external / third-party push notification provider.
 * Imagine this is a library you cannot change.
 */
public class ExternalPushService {

    public void pushToDevice(String deviceToken, String title, String body) {
        System.out.println("External PUSH to device " + deviceToken +
                " | title: " + title +
                " | body: " + body);
    }
}

