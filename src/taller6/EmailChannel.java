package taller6;

public class EmailChannel implements NotificationChannel {

    @Override
    public void deliver(String recipient, String content) {
        System.out.println("Sending EMAIL to " + recipient + " with content: " + content);
    }
}

