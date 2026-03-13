package taller6;

public class UrgentNotification extends Notification {

    public UrgentNotification(NotificationChannel channel) {
        super(channel);
    }

    @Override
    public void send(String recipient, String message) {
        String taggedMessage = "[URGENT] " + message;
        channel.deliver(recipient, taggedMessage);
    }
}

