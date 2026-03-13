package taller6;

/**
 * Adapter: adapts ExternalPushService to our NotificationChannel interface.
 */
public class PushChannelAdapter implements NotificationChannel {

    private final ExternalPushService externalPushService;
    private final String defaultTitle;

    public PushChannelAdapter(ExternalPushService externalPushService, String defaultTitle) {
        this.externalPushService = externalPushService;
        this.defaultTitle = defaultTitle;
    }

    @Override
    public void deliver(String recipient, String content) {
        externalPushService.pushToDevice(recipient, defaultTitle, content);
    }
}

