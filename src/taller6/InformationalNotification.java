package taller6;

public class InformationalNotification extends Notification {

    public InformationalNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String recipient, String message) {
        String taggedMessage = "[INFO] " + message;
        channel.deliver(recipient, taggedMessage);
    }
}

